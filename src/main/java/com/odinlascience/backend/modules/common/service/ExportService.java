package com.odinlascience.backend.modules.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Service utilitaire pour exporter des donnees en CSV ou JSON.
 */
@Service
@Slf4j
public class ExportService {

    private final ObjectMapper objectMapper;

    public ExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Exporte une liste de DTOs en CSV (byte[]).
     * Les noms de champs sont utilises comme en-tetes.
     */
    public <D> byte[] exportToCsv(List<D> items, Class<D> dtoClass) {
        Field[] fields = dtoClass.getDeclaredFields();
        String[] headers = Arrays.stream(fields).map(Field::getName).toArray(String[]::new);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            writer.println(String.join(",", headers));

            for (D item : items) {
                String[] values = new String[headers.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = getFieldValue(item, fields[i]);
                }
                writer.println(String.join(",", values));
            }
        }
        log.info("Export CSV : {} lignes, classe={}", items.size(), dtoClass.getSimpleName());
        return baos.toByteArray();
    }

    /**
     * Exporte une liste de DTOs en JSON (byte[]).
     */
    public <D> byte[] exportToJson(List<D> items) {
        try {
            byte[] result = objectMapper.writeValueAsBytes(items);
            log.info("Export JSON : {} elements", items.size());
            return result;
        } catch (Exception e) {
            log.error("Erreur lors de l'export JSON", e);
            throw new RuntimeException("Erreur lors de l'export JSON", e);
        }
    }

    private <D> String getFieldValue(D item, Field field) {
        try {
            String getterName = "get" + capitalize(field.getName());
            Method getter = item.getClass().getMethod(getterName);
            Object value = getter.invoke(item);
            String str = value != null ? value.toString() : "";
            return escapeCsv(str);
        } catch (Exception e) {
            return "";
        }
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

package com.odinlascience.backend.modules.ai.service;

import com.odinlascience.backend.modules.ai.dto.CorrectionDto;
import com.odinlascience.backend.modules.ai.dto.CorrectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageToolCorrectionService implements TextCorrectionService {

    private final RestClient restClient;

    @Value("${ai.languagetool.url}")
    private String languageToolUrl;

    @Value("${ai.languagetool.language}")
    private String defaultLanguage;

    @Override
    public CorrectionResponse correct(String text, String language) {
        String lang = (language != null && !language.isBlank()) ? language : defaultLanguage;

        String body = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                + "&language=" + URLEncoder.encode(lang, StandardCharsets.UTF_8)
                + "&level=picky";

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri(languageToolUrl + "/check")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("matches")) {
            return CorrectionResponse.builder()
                    .correctedText(text)
                    .corrections(List.of())
                    .correctionCount(0)
                    .build();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> matches = (List<Map<String, Object>>) response.get("matches");
        List<CorrectionDto> corrections = new ArrayList<>();

        // Appliquer les corrections en ordre inverse pour preserver les offsets
        StringBuilder corrected = new StringBuilder(text);
        for (int i = matches.size() - 1; i >= 0; i--) {
            Map<String, Object> match = matches.get(i);
            int offset = (int) match.get("offset");
            int length = (int) match.get("length");
            String message = (String) match.get("message");

            @SuppressWarnings("unchecked")
            List<Map<String, String>> replacements = (List<Map<String, String>>) match.get("replacements");
            List<String> replacementValues = replacements.stream()
                    .map(r -> r.get("value"))
                    .limit(3)
                    .toList();

            String original = text.substring(offset, offset + length);

            corrections.addFirst(CorrectionDto.builder()
                    .offset(offset)
                    .length(length)
                    .message(message)
                    .replacements(replacementValues)
                    .original(original)
                    .build());

            // Appliquer le premier remplacement suggere
            if (!replacementValues.isEmpty()) {
                corrected.replace(offset, offset + length, replacementValues.getFirst());
            }
        }

        log.info("Text correction: {} corrections applied for {} chars", corrections.size(), text.length());

        return CorrectionResponse.builder()
                .correctedText(corrected.toString())
                .corrections(corrections)
                .correctionCount(corrections.size())
                .build();
    }
}

package com.odinlascience.backend.modules.bacteriology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.enums.GramStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SerializationTest {

    @Test
    void bacteriumDtoSerializes() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        BacteriumDTO dto = BacteriumDTO.builder()
                .id(1L)
                .species("Escherichia coli")
                .gram(GramStatus.NEGATIVE)
                .build();

        String json = mapper.writeValueAsString(dto);
        assertNotNull(json);
        System.out.println("Serialized DTO: " + json);
    }

    @Test
    void bacteriumDtoDeserializes() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"gram\":\"NEGATIVE\"}";

        com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO dto =
                mapper.readValue(json, com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO.class);

        assertNotNull(dto);
        System.out.println("Deserialized DTO gram: " + dto.getGram());
    }
}

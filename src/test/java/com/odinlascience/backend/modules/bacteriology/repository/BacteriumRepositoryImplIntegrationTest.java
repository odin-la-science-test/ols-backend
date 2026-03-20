package com.odinlascience.backend.modules.bacteriology.repository;

import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BacteriumRepositoryImplIntegrationTest {

    @Autowired
    private BacteriumRepositoryImpl repository;

    @Test
    void findBestMatches_noCriteria_returnsEmpty() {
        BacteriumDTO criteria = new BacteriumDTO();

        List<?> matches = repository.findBestMatches(criteria, 10);

        assertNotNull(matches);
        assertTrue(matches.isEmpty(), "Expected empty list when no identification criteria provided");
    }
}

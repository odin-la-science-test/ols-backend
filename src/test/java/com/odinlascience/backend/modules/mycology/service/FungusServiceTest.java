package com.odinlascience.backend.modules.mycology.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.mycology.dto.FungusDTO;
import com.odinlascience.backend.modules.mycology.enums.FungusType;
import com.odinlascience.backend.modules.mycology.enums.FungusCategory;
import com.odinlascience.backend.modules.mycology.model.Fungus;
import com.odinlascience.backend.modules.common.model.ApiCode;
import com.odinlascience.backend.modules.common.model.IdentifiableMatch;
import com.odinlascience.backend.modules.mycology.repository.FungusRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.odinlascience.backend.modules.mycology.mapper.FungusMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FungusServiceTest {

    @Mock
    private FungusRepository repository;

    private FungusMapper mapper;

    private FungusService service;

    private Fungus f1;

    @BeforeEach
    void setUp() {
        mapper = org.mapstruct.factory.Mappers.getMapper(FungusMapper.class);
        service = new FungusService(repository, mapper);
        f1 = Fungus.builder()
                .id(1L)
                .species("Saccharomyces cerevisiae")
                .type(FungusType.LEVURES)
                .category(FungusCategory.FERMENTATION)
                .description("Levure de boulanger")
                .habitat("Fruits, moûts")
                .aerobic(true)
                .dimorphic(false)
                .apiCodes(Set.of(new ApiCode("API 20 C AUX", "SAC001")))
                .build();
    }

    @Test
    void identifyByProfile_ReturnsMatchesWithScore() {
        FungusDTO criteria = FungusDTO.builder()
                .type(FungusType.LEVURES)
                .build();
        
        IdentifiableMatch<Fungus> match = new IdentifiableMatch<>(f1, 100);
        when(repository.findBestMatches(eq(criteria), eq(20))).thenReturn(List.of(match));

        List<FungusDTO> results = service.identifyByProfile(criteria);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(100, results.get(0).getConfidenceScore());
        assertEquals(FungusType.LEVURES, results.get(0).getType());
        
        verify(repository).findBestMatches(criteria, 20);
    }

    @Test
    void identifyByProfile_ReturnsEmpty_WhenNoResults() {
        when(repository.findBestMatches(any(), eq(20))).thenReturn(List.of());

        List<FungusDTO> results = service.identifyByProfile(FungusDTO.builder().build());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void getAllFungi_ReturnsDTOList() {
        when(repository.findAll()).thenReturn(List.of(f1));

        List<FungusDTO> results = service.getAllFungi();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Saccharomyces cerevisiae", results.get(0).getSpecies());
        verify(repository).findAll();
    }

    @Test
    void getFungusById_ReturnsFungusDTO_WhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(f1));

        FungusDTO result = service.getFungusById(1L);

        assertNotNull(result);
        assertEquals("Saccharomyces cerevisiae", result.getSpecies());
        assertEquals(FungusType.LEVURES, result.getType());
    }

    @Test
    void getFungusById_ThrowsException_WhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> service.getFungusById(99L));
        
        assertTrue(exception.getMessage().contains("Champignon introuvable"));
    }

    @Test
    void searchBySpecies_ReturnsMatches() {
        when(repository.findBySpeciesContainingIgnoreCase("Saccharomyces"))
                .thenReturn(List.of(f1));

        List<FungusDTO> results = service.searchBySpecies("Saccharomyces");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Saccharomyces cerevisiae", results.get(0).getSpecies());
    }

    @Test
    void getFungusByApiCode_ReturnsFungusDTO_WhenFound() {
        when(repository.findByApiCodes_Code("SAC001")).thenReturn(Optional.of(f1));

        FungusDTO result = service.getFungusByApiCode("SAC001");

        assertNotNull(result);
        assertEquals("Saccharomyces cerevisiae", result.getSpecies());
    }

    @Test
    void getFungusByApiCode_ThrowsException_WhenNotFound() {
        when(repository.findByApiCodes_Code("UNKNOWN")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> service.getFungusByApiCode("UNKNOWN"));
        
        assertTrue(exception.getMessage().contains("ne correspond au code API"));
    }
}

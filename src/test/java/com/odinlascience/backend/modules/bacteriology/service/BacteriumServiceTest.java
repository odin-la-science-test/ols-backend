package com.odinlascience.backend.modules.bacteriology.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.enums.BacterialMorphology;
import com.odinlascience.backend.modules.bacteriology.enums.GramStatus;
import com.odinlascience.backend.modules.bacteriology.model.Bacterium;
import com.odinlascience.backend.modules.common.model.ApiCode;
import com.odinlascience.backend.modules.common.model.IdentifiableMatch;
import com.odinlascience.backend.modules.bacteriology.repository.BacteriumRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.odinlascience.backend.modules.bacteriology.mapper.BacteriumMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BacteriumServiceTest {

    @Mock
    private BacteriumRepository repository;

    private BacteriumMapper mapper;

    private BacteriumService service;

    private Bacterium b1;

    @BeforeEach
    void setUp() {
        mapper = org.mapstruct.factory.Mappers.getMapper(BacteriumMapper.class);
        service = new BacteriumService(repository, mapper);
        b1 = Bacterium.builder()
                .id(1L)
                .species("Escherichia coli")
                .strain("K12")
                .gram(GramStatus.NEGATIVE)
                .morpho(BacterialMorphology.BACILLI)
                .genomeSize(4.6)
                .resistanceGenes(Set.of("bla"))
                .catalase(true)
                .oxydase(false)
                .apiCodes(Set.of(new ApiCode("API 20 E", "API123")))
                .build();
    }

    @Test
    void identifyByBiochemicalProfile_ReturnsMatchesWithScore() {
        BacteriumDTO criteria = BacteriumDTO.builder()
                .gram(GramStatus.NEGATIVE)
                .build();
        
        IdentifiableMatch<Bacterium> match = new IdentifiableMatch<>(b1, 100);
        when(repository.findBestMatches(eq(criteria), eq(20))).thenReturn(List.of(match));

        List<BacteriumDTO> results = service.identifyByBiochemicalProfile(criteria);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(100, results.get(0).getConfidenceScore());
        assertEquals(GramStatus.NEGATIVE, results.get(0).getGram());
        
        verify(repository).findBestMatches(criteria, 20);
    }

    @Test
    void identifyByBiochemicalProfile_ReturnsEmpty_WhenNoResults() {
        when(repository.findBestMatches(any(), eq(20))).thenReturn(List.of());
        
        List<BacteriumDTO> results = service.identifyByBiochemicalProfile(BacteriumDTO.builder().build());
        
        assertTrue(results.isEmpty());
    }

    @Test
    void getAllBacteriaReturnsMappedDTOs() {
        when(repository.findAll()).thenReturn(List.of(b1));

        List<BacteriumDTO> dtos = service.getAllBacteria();

        assertEquals(1, dtos.size());
        BacteriumDTO dto = dtos.get(0);
        assertEquals(b1.getId(), dto.getId());
        assertEquals(b1.getSpecies(), dto.getSpecies());
        assertEquals(b1.getGram(), dto.getGram());
    }

    @Test
    void getBacteriumByIdFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(b1));

        BacteriumDTO dto = service.getBacteriumById(1L);

        assertEquals(1L, dto.getId());
        assertEquals("Escherichia coli", dto.getSpecies());
        assertEquals(GramStatus.NEGATIVE, dto.getGram());
    }

    @Test
    void getBacteriumByIdNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getBacteriumById(2L));
    }

    @Test
    void getBacteriumByApiCodeFound() {
        when(repository.findByApiCodes_Code("API123")).thenReturn(Optional.of(b1));
        BacteriumDTO dto = service.getBacteriumByApiCode("API123");
        assertTrue(dto.getApiCodes().stream().anyMatch(c -> c.getCode().equals("API123")));
    }

    @Test
    void getBacteriumByApiCodeNotFound() {
        when(repository.findByApiCodes_Code("NOPE")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getBacteriumByApiCode("NOPE"));
    }

    @Test
    void searchBySpeciesReturnsMatches() {
        when(repository.findBySpeciesContainingIgnoreCase(ArgumentMatchers.eq("coli")))
                .thenReturn(List.of(b1));

        List<BacteriumDTO> results = service.searchBySpecies("coli");

        assertEquals(1, results.size());
        assertTrue(results.get(0).getSpecies().toLowerCase().contains("coli"));
    }

    @Test
    void mapToDTOHandlesNullGram() {
        Bacterium bGramNull = Bacterium.builder()
                .id(3L)
                .species("Bacillus subtilis")
                .gram(null)
                .morpho(BacterialMorphology.BACILLI)
                .build();

        when(repository.findById(3L)).thenReturn(Optional.of(bGramNull));

        BacteriumDTO dto = service.getBacteriumById(3L);

        assertNull(dto.getGram());
        assertEquals(BacterialMorphology.BACILLI, dto.getMorpho());
    }

    @Test
    void mapToDTOHandlesNullMorpho() {
        Bacterium bMorphoNull = Bacterium.builder()
                .id(4L)
                .species("Staphylococcus aureus")
                .gram(GramStatus.POSITIVE)
                .morpho(null)
                .build();

        when(repository.findById(4L)).thenReturn(Optional.of(bMorphoNull));

        BacteriumDTO dto = service.getBacteriumById(4L);

        assertNull(dto.getMorpho());
        assertEquals(GramStatus.POSITIVE, dto.getGram());
    }
}
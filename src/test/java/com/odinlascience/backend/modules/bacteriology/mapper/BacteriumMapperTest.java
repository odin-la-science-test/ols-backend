package com.odinlascience.backend.modules.bacteriology.mapper;

import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.enums.BacterialMorphology;
import com.odinlascience.backend.modules.bacteriology.enums.GramStatus;
import com.odinlascience.backend.modules.bacteriology.model.Bacterium;
import com.odinlascience.backend.modules.common.model.ApiCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BacteriumMapperTest {

    private BacteriumMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = org.mapstruct.factory.Mappers.getMapper(BacteriumMapper.class);
    }

    @Test
    void toDTO_NullInput_ReturnsNull() {
        assertNull(mapper.toDTO((Bacterium) null));
        BacteriumDTO dtoFromNullWithScore = mapper.toDTO((Bacterium) null, 10);
        assertNotNull(dtoFromNullWithScore);
        assertEquals(10, dtoFromNullWithScore.getConfidenceScore());
        assertNull(mapper.toDTO((Bacterium) null, (Integer) null));
    }

    @Test
    void toDTO_FullMapping_WithoutScore_MapsAllFields() {
        Set<String> plasmids = new HashSet<>();
        plasmids.add("p1");
        plasmids.add("p2");

        Set<Integer> snp = new HashSet<>();
        snp.add(1);
        snp.add(2);

        Set<String> resist = new HashSet<>();
        resist.add("r1");

        Set<String> vir = new HashSet<>();
        vir.add("v1");

        Bacterium b = Bacterium.builder()
                .id(123L)
                .species("Testus sampleus")
                .strain("S1")
                .gram(GramStatus.POSITIVE)
                .morpho(BacterialMorphology.COCCI)
                .genomeSize(3.14)
                .mlst("MLST1")
                .plasmids(plasmids)
                .snpSignature(snp)
                .resistanceGenes(resist)
                .virulenceFactors(vir)
                .maldiProfile("MALDI1")
                .apiCodes(Set.of(new ApiCode("API 20 E", "API-1")))
                .catalase(true)
                .oxydase(false)
                .coagulase(true)
                .lactose(false)
                .indole(true)
                .mannitol(false)
                .mobilite(true)
                .hemolyse("beta")
                .pathogenicity("low")
                .habitat("soil")
                .build();

        BacteriumDTO dto = mapper.toDTO(b);

        assertNotNull(dto);
        assertEquals(b.getId(), dto.getId());
        assertEquals(b.getSpecies(), dto.getSpecies());
        assertEquals(b.getStrain(), dto.getStrain());
        assertEquals(b.getGram(), dto.getGram());
        assertEquals(b.getMorpho(), dto.getMorpho());
        assertEquals(b.getGenomeSize(), dto.getGenomeSize());
        assertEquals(b.getMlst(), dto.getMlst());
        assertEquals(b.getPlasmids(), dto.getPlasmids());
        assertEquals(b.getSnpSignature(), dto.getSnpSignature());
        assertEquals(b.getResistanceGenes(), dto.getResistanceGenes());
        assertEquals(b.getVirulenceFactors(), dto.getVirulenceFactors());
        assertEquals(b.getMaldiProfile(), dto.getMaldiProfile());
        assertEquals(b.getApiCodes(), dto.getApiCodes());
        assertEquals(b.getCatalase(), dto.getCatalase());
        assertEquals(b.getOxydase(), dto.getOxydase());
        assertEquals(b.getCoagulase(), dto.getCoagulase());
        assertEquals(b.getLactose(), dto.getLactose());
        assertEquals(b.getIndole(), dto.getIndole());
        assertEquals(b.getMannitol(), dto.getMannitol());
        assertEquals(b.getMobilite(), dto.getMobilite());
        assertEquals(b.getHemolyse(), dto.getHemolyse());
        assertEquals(b.getPathogenicity(), dto.getPathogenicity());
        assertEquals(b.getHabitat(), dto.getHabitat());
        assertNull(dto.getConfidenceScore());
    }

    @Test
    void toDTO_WithScore_MapsConfidenceScore() {
        Bacterium b = Bacterium.builder()
                .id(5L)
                .species("Scoredus")
                .build();

        BacteriumDTO dto = mapper.toDTO(b, 77);

        assertNotNull(dto);
        assertEquals(77, dto.getConfidenceScore());
        assertEquals(b.getSpecies(), dto.getSpecies());
        assertEquals(b.getId(), dto.getId());
    }

    @Test
    void toDTO_WithScore_FullMapping_MapsCollectionsAndFields() {
        Set<String> plasmids = new HashSet<>();
        plasmids.add("pA");

        Set<Integer> snp = new HashSet<>();
        snp.add(42);

        Set<String> vir = new HashSet<>();
        vir.add("vX");

        Bacterium b = Bacterium.builder()
                .id(77L)
                .species("FullScore")
                .plasmids(plasmids)
                .snpSignature(snp)
                .virulenceFactors(vir)
                .catalase(false)
                .oxydase(true)
                .build();

        BacteriumDTO dto = mapper.toDTO(b, 55);

        assertNotNull(dto);
        assertEquals(55, dto.getConfidenceScore());
        assertEquals(b.getId(), dto.getId());
        assertEquals(b.getSpecies(), dto.getSpecies());
        assertEquals(b.getPlasmids(), dto.getPlasmids());
        assertEquals(b.getSnpSignature(), dto.getSnpSignature());
        assertEquals(b.getVirulenceFactors(), dto.getVirulenceFactors());
        assertEquals(b.getCatalase(), dto.getCatalase());
        assertEquals(b.getOxydase(), dto.getOxydase());
    }
}

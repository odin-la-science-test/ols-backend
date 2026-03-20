package com.odinlascience.backend.modules.bacteriology.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;

import com.odinlascience.backend.modules.common.annotation.IdentificationCriterion;
import com.odinlascience.backend.modules.common.model.ApiCode;
import com.odinlascience.backend.modules.bacteriology.enums.BacterialMorphology;
import com.odinlascience.backend.modules.bacteriology.enums.GramStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BacteriumDTO {
    private Long id;
    private String species;
    private String strain;
    private String mlst;

    @IdentificationCriterion
    private GramStatus gram;

    @IdentificationCriterion
    private BacterialMorphology morpho;

    private String pathogenicity;
    private String habitat;

    private Double genomeSize;
    private Set<String> resistanceGenes;
    private Set<String> virulenceFactors;
    private Set<String> plasmids;
    private Set<Integer> snpSignature;

    private String maldiProfile;
    private Set<ApiCode> apiCodes;

    @IdentificationCriterion
    private Boolean catalase;

    @IdentificationCriterion
    private Boolean oxydase;

    @IdentificationCriterion
    private Boolean coagulase;

    @IdentificationCriterion
    private Boolean lactose;

    @IdentificationCriterion
    private Boolean indole;

    @IdentificationCriterion
    private Boolean mannitol;

    @IdentificationCriterion
    private Boolean mobilite;

    @IdentificationCriterion
    private String hemolyse;

    private Integer confidenceScore;
}
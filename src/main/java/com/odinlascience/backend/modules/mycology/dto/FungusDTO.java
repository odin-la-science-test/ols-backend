package com.odinlascience.backend.modules.mycology.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;

import com.odinlascience.backend.modules.common.annotation.IdentificationCriterion;
import com.odinlascience.backend.modules.common.model.ApiCode;
import com.odinlascience.backend.modules.mycology.enums.FungusType;
import com.odinlascience.backend.modules.mycology.enums.FungusCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FungusDTO {
    private Long id;
    private String species;

    @IdentificationCriterion
    private FungusType type;

    @IdentificationCriterion
    private FungusCategory category;

    private String description;
    private String habitat;
    private String morphology;
    private Double optimalTemperature;
    private Double maximalTemperature;
    private String applications;
    private String metabolism;
    private String pathogenicity;
    private String cultureMedium;
    private Set<ApiCode> apiCodes;

    @IdentificationCriterion
    private Boolean aerobic;

    @IdentificationCriterion
    private Boolean dimorphic;

    @IdentificationCriterion
    private Boolean encapsulated;

    @IdentificationCriterion
    private Boolean melaninProducer;

    private String reproduction;
    private Set<String> secondaryMetabolites;
    private Set<String> enzymes;
    private Set<String> degradableSubstrates;
    private Set<String> hosts;
    private String toxins;
    private String allergens;

    private Integer confidenceScore;
}

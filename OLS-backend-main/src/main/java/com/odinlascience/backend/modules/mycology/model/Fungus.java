package com.odinlascience.backend.modules.mycology.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import com.odinlascience.backend.modules.mycology.enums.FungusType;
import com.odinlascience.backend.modules.mycology.enums.FungusCategory;
import com.odinlascience.backend.modules.common.model.ApiCode;

@Entity
@Table(name = "fungi")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fungus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String species;

    @Enumerated(EnumType.STRING)
    private FungusType type;

    @Enumerated(EnumType.STRING)
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

    @ElementCollection
    @CollectionTable(name = "fungus_api_codes", joinColumns = @JoinColumn(name = "fungus_id"))
    private Set<ApiCode> apiCodes;

    private Boolean aerobic;

    private Boolean dimorphic;

    private Boolean encapsulated;

    private Boolean melaninProducer;

    private String reproduction;

    @ElementCollection
    @CollectionTable(name = "fungus_metabolites", joinColumns = @JoinColumn(name = "fungus_id"))
    @Column(name = "metabolite")
    private Set<String> secondaryMetabolites;

    @ElementCollection
    @CollectionTable(name = "fungus_enzymes", joinColumns = @JoinColumn(name = "fungus_id"))
    @Column(name = "enzyme")
    private Set<String> enzymes;

    @ElementCollection
    @CollectionTable(name = "fungus_degradable_substrates", joinColumns = @JoinColumn(name = "fungus_id"))
    @Column(name = "substrate")
    private Set<String> degradableSubstrates;

    @ElementCollection
    @CollectionTable(name = "fungus_hosts", joinColumns = @JoinColumn(name = "fungus_id"))
    @Column(name = "host")
    private Set<String> hosts;

    private String toxins;

    private String allergens;
}

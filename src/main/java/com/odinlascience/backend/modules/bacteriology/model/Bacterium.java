package com.odinlascience.backend.modules.bacteriology.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import com.odinlascience.backend.modules.bacteriology.enums.BacterialMorphology;
import com.odinlascience.backend.modules.bacteriology.enums.GramStatus;
import com.odinlascience.backend.modules.common.model.ApiCode;

@Entity
@Table(name = "bacteria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bacterium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String species;

    private String strain;

    @Enumerated(EnumType.STRING)
    private GramStatus gram;

    @Enumerated(EnumType.STRING)
    private BacterialMorphology morpho;

    private Double genomeSize;

    private String mlst;

    @ElementCollection
    @CollectionTable(name = "bacterium_plasmids", joinColumns = @JoinColumn(name = "bacterium_id"))
    @Column(name = "plasmid_name")
    private Set<String> plasmids;

    @ElementCollection
    @CollectionTable(name = "bacterium_snp_signatures", joinColumns = @JoinColumn(name = "bacterium_id"))
    @Column(name = "snp_marker")
    private Set<Integer> snpSignature;

    @ElementCollection
    @CollectionTable(name = "bacterium_resistance_genes", joinColumns = @JoinColumn(name = "bacterium_id"))
    @Column(name = "gene_code")
    private Set<String> resistanceGenes;

    @ElementCollection
    @CollectionTable(name = "bacterium_virulence_factors", joinColumns = @JoinColumn(name = "bacterium_id"))
    @Column(name = "virulence_factor")
    private Set<String> virulenceFactors;

    private String pathogenicity;
    private String habitat;

    private String maldiProfile;

    @ElementCollection
    @CollectionTable(name = "bacterium_api_codes", joinColumns = @JoinColumn(name = "bacterium_id"))
    private Set<ApiCode> apiCodes;

    private Boolean catalase;
    private Boolean oxydase;
    private Boolean coagulase;
    private Boolean lactose;
    private Boolean indole;
    private Boolean mannitol;
    private Boolean mobilite;

    private String hemolyse;
}
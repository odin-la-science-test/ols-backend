package com.odinlascience.backend.modules.catalog.model;

import com.odinlascience.backend.modules.catalog.enums.ModuleType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity
@Table(name = "modules")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank
    @Column(name = "module_key", unique = true, nullable = false)
    private String moduleKey;

    @NotBlank
    @Column(unique = true)
    private String slug;

    @NotBlank
    private String icon;

    @Column(length = 500)
    private String description;

    @Column(name = "route_path")
    private String routePath;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ModuleType type;

    @Builder.Default
    private Double price = 0.0;

    @Builder.Default
    private Boolean active = true;

    @Column(name = "admin_only")
    @Builder.Default
    private Boolean adminOnly = false;
}
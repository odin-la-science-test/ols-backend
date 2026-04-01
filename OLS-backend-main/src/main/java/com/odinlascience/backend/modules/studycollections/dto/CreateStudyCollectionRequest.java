package com.odinlascience.backend.modules.studycollections.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de requete pour creer une collection d'etude.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudyCollectionRequest {

    /** Nom de la collection */
    @NotBlank
    @Size(max = 255)
    private String name;

    /** Description optionnelle */
    private String description;
}

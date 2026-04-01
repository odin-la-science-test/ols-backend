package com.odinlascience.backend.modules.studycollections.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de requete pour mettre a jour une collection d'etude.
 * Tous les champs sont optionnels (mise a jour partielle).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudyCollectionRequest {

    /** Nom de la collection */
    @Size(max = 255)
    private String name;

    /** Description */
    private String description;
}

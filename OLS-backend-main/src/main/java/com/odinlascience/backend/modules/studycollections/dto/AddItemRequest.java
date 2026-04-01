package com.odinlascience.backend.modules.studycollections.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de requete pour ajouter un element a une collection d'etude.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {

    /** Identifiant du module source (ex: "bacteriology", "mycology") */
    @NotBlank
    @Size(max = 100)
    private String moduleId;

    /** ID de l'entite dans le module source */
    @NotNull
    private Long entityId;

    /** Note personnelle optionnelle sur cet element */
    private String notes;
}

package com.odinlascience.backend.modules.annotations.dto;

import com.odinlascience.backend.modules.annotations.enums.AnnotationColor;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de requete pour mettre a jour une annotation.
 * Tous les champs sont optionnels (mise a jour partielle).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnnotationRequest {

    /** Contenu de l'annotation (Markdown) */
    @Size(max = 2000)
    private String content;

    /** Couleur de l'annotation */
    private AnnotationColor color;
}

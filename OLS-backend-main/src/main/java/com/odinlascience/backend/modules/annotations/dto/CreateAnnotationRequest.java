package com.odinlascience.backend.modules.annotations.dto;

import com.odinlascience.backend.modules.annotations.enums.AnnotationColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de requete pour creer une annotation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnnotationRequest {

    /** Type de l'entite annotee */
    @NotBlank
    private String entityType;

    /** ID de l'entite annotee */
    @NotNull
    private Long entityId;

    /** Contenu de l'annotation (Markdown) */
    @NotBlank
    @Size(max = 2000)
    private String content;

    /** Couleur de l'annotation */
    private AnnotationColor color;
}

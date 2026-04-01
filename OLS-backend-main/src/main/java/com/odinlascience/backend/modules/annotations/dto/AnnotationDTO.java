package com.odinlascience.backend.modules.annotations.dto;

import com.odinlascience.backend.modules.annotations.enums.AnnotationColor;
import lombok.*;

import java.time.Instant;

/**
 * DTO de reponse pour une annotation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationDTO {

    private Long id;
    private String entityType;
    private Long entityId;
    private String content;
    private AnnotationColor color;
    private Instant createdAt;
    private Instant updatedAt;

    /** Nom du proprietaire de l'annotation */
    private String ownerName;
}

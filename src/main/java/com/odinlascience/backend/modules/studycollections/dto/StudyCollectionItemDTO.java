package com.odinlascience.backend.modules.studycollections.dto;

import lombok.*;

import java.time.Instant;

/**
 * DTO de reponse pour un element d'une collection d'etude.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyCollectionItemDTO {

    private Long id;
    private String moduleId;
    private Long entityId;
    private String notes;
    private Instant addedAt;
}

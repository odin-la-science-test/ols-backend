package com.odinlascience.backend.modules.studycollections.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * DTO de reponse pour une collection d'etude.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyCollectionDTO {

    private Long id;
    private String name;
    private String description;
    private String ownerName;
    private List<StudyCollectionItemDTO> items;
    private Instant createdAt;
    private Instant updatedAt;
}

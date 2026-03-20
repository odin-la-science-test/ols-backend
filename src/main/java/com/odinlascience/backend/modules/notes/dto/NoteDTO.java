package com.odinlascience.backend.modules.notes.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * DTO de réponse pour une note.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {

    private Long id;
    private String title;
    private String content;
    private String color;
    private Boolean pinned;
    private List<String> tags;

    private Instant createdAt;
    private Instant updatedAt;

    /** Nom du propriétaire */
    private String ownerName;
}

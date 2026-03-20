package com.odinlascience.backend.modules.notes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

/**
 * DTO de requête pour créer une note.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoteRequest {

    /** Titre de la note (requis) */
    @NotBlank(message = "Le titre est requis")
    private String title;

    /** Contenu textuel de la note */
    private String content;

    /** Couleur d'accent */
    private String color;

    /** Épinglée ou non */
    private Boolean pinned;

    /** Liste de tags */
    private List<String> tags;
}

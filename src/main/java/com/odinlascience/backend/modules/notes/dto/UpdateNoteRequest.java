package com.odinlascience.backend.modules.notes.dto;

import com.odinlascience.backend.modules.notes.enums.NoteColor;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * DTO de requête pour mettre à jour une note.
 * Tous les champs sont optionnels (mise à jour partielle).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNoteRequest {

    /** Nouveau titre */
    @Size(max = 255)
    private String title;

    /** Nouveau contenu */
    private String content;

    /** Nouvelle couleur */
    private NoteColor color;

    /** Épinglée ou non */
    private Boolean pinned;

    /** Nouveaux tags */
    private List<String> tags;
}

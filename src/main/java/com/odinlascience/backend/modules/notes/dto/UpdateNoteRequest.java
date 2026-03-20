package com.odinlascience.backend.modules.notes.dto;

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
    private String title;

    /** Nouveau contenu */
    private String content;

    /** Nouvelle couleur */
    private String color;

    /** Épinglée ou non */
    private Boolean pinned;

    /** Nouveaux tags */
    private List<String> tags;
}

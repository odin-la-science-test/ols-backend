package com.odinlascience.backend.modules.support.dto;

import com.odinlascience.backend.modules.support.enums.TicketCategory;

import lombok.*;

/**
 * DTO de requête pour mettre à jour un ticket de support.
 * Mise à jour partielle — tous les champs sont optionnels.
 * Seul un ticket OPEN peut être modifié par l'utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketRequest {

    /** Nouveau sujet */
    private String subject;

    /** Nouvelle description */
    private String description;

    /** Nouvelle catégorie */
    private TicketCategory category;
}

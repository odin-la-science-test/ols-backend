package com.odinlascience.backend.modules.quickshare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * DTO de requête pour créer un partage de texte.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTextShareRequest {

    /** Titre optionnel du partage */
    private String title;

    /** Contenu texte à partager */
    @NotNull(message = "Le contenu texte est requis")
    private String textContent;

    /** Nombre max de consultations (null = illimité) */
    private Integer maxDownloads;

    /** Date d'expiration (null = jamais) */
    private Instant expiresAt;

    /** Email du destinataire (partage direct à un contact, optionnel) */
    private String recipientEmail;
}

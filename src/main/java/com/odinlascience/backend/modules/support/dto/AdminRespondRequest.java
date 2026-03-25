package com.odinlascience.backend.modules.support.dto;

import com.odinlascience.backend.modules.support.enums.TicketStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO de requête pour qu'un admin réponde à un ticket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRespondRequest {

    /** Texte de la réponse admin (requis) */
    @NotBlank(message = "La réponse est requise")
    private String response;

    /** Nouveau statut du ticket (requis) */
    @NotNull(message = "Le statut est requis")
    private TicketStatus status;
}

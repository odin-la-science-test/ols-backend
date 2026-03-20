package com.odinlascience.backend.modules.support.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO de requête pour envoyer un message dans un ticket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    /** Contenu du message (requis) */
    @NotBlank(message = "Le message est requis")
    private String content;
}

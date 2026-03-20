package com.odinlascience.backend.modules.support.dto;

import lombok.*;

import java.time.Instant;

/**
 * DTO de réponse pour un message de ticket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageDTO {

    private Long id;
    private String content;
    private boolean admin;
    private String authorName;
    private Instant createdAt;
}

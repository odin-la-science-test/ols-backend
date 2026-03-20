package com.odinlascience.backend.modules.support.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * DTO de réponse pour un ticket de support.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketDTO {

    private Long id;
    private String subject;
    private String description;
    private String category;
    private String priority;
    private String status;

    /** Fil de messages */
    private List<TicketMessageDTO> messages;

    private Instant createdAt;
    private Instant updatedAt;

    /** Nom du propriétaire du ticket */
    private String ownerName;

    /** Email du propriétaire du ticket */
    private String ownerEmail;
}

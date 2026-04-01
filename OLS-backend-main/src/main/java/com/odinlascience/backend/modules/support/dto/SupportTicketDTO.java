package com.odinlascience.backend.modules.support.dto;

import com.odinlascience.backend.modules.support.enums.TicketCategory;
import com.odinlascience.backend.modules.support.enums.TicketPriority;
import com.odinlascience.backend.modules.support.enums.TicketStatus;
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
    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;

    /** Fil de messages */
    private List<TicketMessageDTO> messages;

    private Instant createdAt;
    private Instant updatedAt;

    /** Nom du propriétaire du ticket */
    private String ownerName;

    /** Email du propriétaire du ticket */
    private String ownerEmail;
}

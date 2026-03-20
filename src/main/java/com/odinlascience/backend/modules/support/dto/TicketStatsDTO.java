package com.odinlascience.backend.modules.support.dto;

import lombok.*;

/**
 * DTO de statistiques des tickets de support (admin).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatsDTO {

    private long total;
    private long open;
    private long inProgress;
    private long resolved;
    private long closed;
}

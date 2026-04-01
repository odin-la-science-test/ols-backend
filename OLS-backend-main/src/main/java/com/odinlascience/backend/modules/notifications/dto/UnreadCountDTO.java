package com.odinlascience.backend.modules.notifications.dto;

import lombok.*;

/**
 * DTO pour le compteur de notifications non lues.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCountDTO {

    private long count;
}

package com.odinlascience.backend.modules.notifications.dto;

import com.odinlascience.backend.modules.notifications.enums.NotificationType;
import lombok.*;

import java.time.Instant;

/**
 * DTO de réponse pour une notification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private String actionUrl;
    private String metadata;
    private Instant createdAt;
}

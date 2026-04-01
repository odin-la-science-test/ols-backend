package com.odinlascience.backend.modules.notifications.mapper;

import com.odinlascience.backend.modules.notifications.dto.NotificationDTO;
import com.odinlascience.backend.modules.notifications.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper Notification → NotificationDTO.
 */
@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.getRead())
                .actionUrl(notification.getActionUrl())
                .metadata(notification.getMetadata())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

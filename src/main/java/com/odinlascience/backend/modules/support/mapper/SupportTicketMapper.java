package com.odinlascience.backend.modules.support.mapper;

import com.odinlascience.backend.modules.support.dto.SupportTicketDTO;
import com.odinlascience.backend.modules.support.dto.TicketMessageDTO;
import com.odinlascience.backend.modules.support.model.SupportTicket;
import com.odinlascience.backend.modules.support.model.TicketMessage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper SupportTicket <-> SupportTicketDTO.
 */
@Component
public class SupportTicketMapper {

    public SupportTicketDTO toDTO(SupportTicket entity) {
        if (entity == null) return null;

        String ownerName = "";
        String ownerEmail = "";
        if (entity.getOwner() != null) {
            ownerName = (entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName()).trim();
            ownerEmail = entity.getOwner().getEmail();
        }

        List<TicketMessageDTO> messageDTOs = Collections.emptyList();
        if (entity.getMessages() != null) {
            messageDTOs = entity.getMessages().stream()
                    .map(this::toMessageDTO)
                    .toList();
        }

        return SupportTicketDTO.builder()
                .id(entity.getId())
                .subject(entity.getSubject())
                .description(entity.getDescription())
                .category(entity.getCategory().name())
                .priority(entity.getPriority().name())
                .status(entity.getStatus().name())
                .messages(messageDTOs)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .ownerName(ownerName)
                .ownerEmail(ownerEmail)
                .build();
    }

    public TicketMessageDTO toMessageDTO(TicketMessage message) {
        if (message == null) return null;

        String authorName = "";
        if (message.getAuthor() != null) {
            authorName = (message.getAuthor().getFirstName() + " " + message.getAuthor().getLastName()).trim();
        }

        return TicketMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .admin(Boolean.TRUE.equals(message.getAdmin()))
                .authorName(authorName)
                .createdAt(message.getCreatedAt())
                .build();
    }
}

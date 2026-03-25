package com.odinlascience.backend.modules.support.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.event.TicketRepliedEvent;
import com.odinlascience.backend.modules.common.event.TicketStatusChangedEvent;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.support.dto.SendMessageRequest;
import com.odinlascience.backend.modules.support.dto.SupportTicketDTO;
import com.odinlascience.backend.modules.support.dto.TicketMessageDTO;
import com.odinlascience.backend.modules.support.dto.TicketStatsDTO;
import com.odinlascience.backend.modules.support.enums.TicketPriority;
import com.odinlascience.backend.modules.support.enums.TicketStatus;
import com.odinlascience.backend.modules.support.mapper.SupportTicketMapper;
import com.odinlascience.backend.modules.support.model.SupportTicket;
import com.odinlascience.backend.modules.support.model.TicketMessage;
import com.odinlascience.backend.modules.support.repository.SupportTicketRepository;
import com.odinlascience.backend.modules.support.repository.TicketMessageRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Operations admin du module Support : listing global, stats, messages admin, changement statut/priorite.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupportTicketAdminService {

    private final SupportTicketRepository repository;
    private final TicketMessageRepository messageRepository;
    private final SupportTicketMapper mapper;
    private final UserHelper userHelper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<SupportTicketDTO> getAllTickets() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(mapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public TicketStatsDTO getStats() {
        return TicketStatsDTO.builder()
                .total(repository.count())
                .open(repository.countByStatus(TicketStatus.OPEN))
                .inProgress(repository.countByStatus(TicketStatus.IN_PROGRESS))
                .resolved(repository.countByStatus(TicketStatus.RESOLVED))
                .closed(repository.countByStatus(TicketStatus.CLOSED))
                .build();
    }

    @Transactional
    public TicketMessageDTO sendAdminMessage(Long ticketId, SendMessageRequest request, String adminEmail) {
        SupportTicket ticket = findTicketById(ticketId);
        User admin = userHelper.findByEmail(adminEmail);

        TicketMessage message = TicketMessage.builder()
                .content(request.getContent())
                .admin(true)
                .author(admin)
                .ticket(ticket)
                .createdAt(Instant.now())
                .build();

        TicketMessage saved = messageRepository.save(message);

        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }
        ticket.setUpdatedAt(Instant.now());
        repository.save(ticket);

        eventPublisher.publishEvent(new TicketRepliedEvent(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getOwner().getEmail(),
                "/lab/support"
        ));

        log.info("Admin message sent on ticket: ticketId={}, messageId={}, admin={}", ticketId, saved.getId(), adminEmail);
        return mapper.toMessageDTO(saved);
    }

    @Transactional
    public SupportTicketDTO updateStatus(Long id, TicketStatus status) {
        SupportTicket ticket = findTicketById(id);
        ticket.setStatus(status);
        ticket.setUpdatedAt(Instant.now());

        SupportTicket saved = repository.save(ticket);
        log.info("Admin changed ticket status: id={}, status={}", saved.getId(), status);

        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            eventPublisher.publishEvent(new TicketStatusChangedEvent(
                    saved.getId(),
                    saved.getSubject(),
                    saved.getOwner().getEmail(),
                    status.name(),
                    "/lab/support"
            ));
        }

        return mapper.toDTO(saved);
    }

    @Transactional
    public SupportTicketDTO updatePriority(Long id, TicketPriority priority) {
        SupportTicket ticket = findTicketById(id);
        ticket.setPriority(priority);
        ticket.setUpdatedAt(Instant.now());

        SupportTicket saved = repository.save(ticket);
        log.info("Admin changed ticket priority: id={}, priority={}", saved.getId(), priority);
        return mapper.toDTO(saved);
    }

    private SupportTicket findTicketById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket introuvable avec l'ID : " + id));
    }
}

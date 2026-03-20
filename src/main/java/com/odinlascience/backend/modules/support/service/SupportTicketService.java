package com.odinlascience.backend.modules.support.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.notifications.enums.NotificationType;
import com.odinlascience.backend.modules.notifications.service.NotificationService;
import com.odinlascience.backend.modules.support.dto.*;
import com.odinlascience.backend.modules.support.enums.TicketPriority;
import com.odinlascience.backend.modules.support.enums.TicketStatus;
import com.odinlascience.backend.modules.support.mapper.SupportTicketMapper;
import com.odinlascience.backend.modules.support.model.SupportTicket;
import com.odinlascience.backend.modules.support.model.TicketMessage;
import com.odinlascience.backend.modules.support.repository.SupportTicketRepository;
import com.odinlascience.backend.modules.support.repository.TicketMessageRepository;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service principal du module Support.
 * Gère le CRUD des tickets (user) et les actions admin.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository repository;
    private final TicketMessageRepository messageRepository;
    private final SupportTicketMapper mapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // ═══════════════════════════════════════════════════════════════════════
    // USER ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    /** Créer un ticket de support */
    @Transactional
    public SupportTicketDTO create(CreateTicketRequest request, String userEmail) {
        User owner = findUserByEmail(userEmail);

        SupportTicket ticket = SupportTicket.builder()
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(TicketPriority.MEDIUM)
                .status(TicketStatus.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .owner(owner)
                .build();

        SupportTicket saved = repository.save(ticket);
        log.info("Support ticket created: id={}, subject='{}', owner={}", saved.getId(), saved.getSubject(), userEmail);
        return mapper.toDTO(saved);
    }

    /** Lister mes tickets */
    @Transactional(readOnly = true)
    public List<SupportTicketDTO> getMyTickets(String userEmail) {
        User owner = findUserByEmail(userEmail);
        return repository.findByOwnerIdOrderByCreatedAtDesc(owner.getId())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /** Détail d'un ticket par ID (l'utilisateur doit en être le propriétaire) */
    @Transactional(readOnly = true)
    public SupportTicketDTO getById(Long id, String userEmail) {
        SupportTicket ticket = findTicketOwnedBy(id, userEmail);
        return mapper.toDTO(ticket);
    }

    /** Mettre à jour un ticket (uniquement si OPEN) */
    @Transactional
    public SupportTicketDTO update(Long id, UpdateTicketRequest request, String userEmail) {
        SupportTicket ticket = findTicketOwnedBy(id, userEmail);

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new IllegalStateException("Seuls les tickets ouverts peuvent être modifiés");
        }

        if (request.getSubject() != null) {
            ticket.setSubject(request.getSubject());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            ticket.setCategory(request.getCategory());
        }

        ticket.setUpdatedAt(Instant.now());
        SupportTicket saved = repository.save(ticket);
        log.info("Support ticket updated: id={}, owner={}", saved.getId(), userEmail);
        return mapper.toDTO(saved);
    }

    /** Supprimer un ticket (uniquement si OPEN) */
    @Transactional
    public void delete(Long id, String userEmail) {
        SupportTicket ticket = findTicketOwnedBy(id, userEmail);

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new IllegalStateException("Seuls les tickets ouverts peuvent être supprimés");
        }

        repository.delete(ticket);
        log.info("Support ticket deleted: id={}, owner={}", id, userEmail);
    }

    /** Envoyer un message dans un ticket (user — doit en être le propriétaire, ticket non fermé) */
    @Transactional
    public TicketMessageDTO sendUserMessage(Long ticketId, SendMessageRequest request, String userEmail) {
        SupportTicket ticket = findTicketOwnedBy(ticketId, userEmail);
        User author = findUserByEmail(userEmail);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new IllegalStateException("Impossible d'envoyer un message sur un ticket fermé");
        }

        TicketMessage message = TicketMessage.builder()
                .content(request.getContent())
                .admin(false)
                .author(author)
                .ticket(ticket)
                .createdAt(Instant.now())
                .build();

        TicketMessage saved = messageRepository.save(message);
        ticket.setUpdatedAt(Instant.now());
        repository.save(ticket);

        log.info("User message sent on ticket: ticketId={}, messageId={}, user={}", ticketId, saved.getId(), userEmail);
        return mapper.toMessageDTO(saved);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ADMIN ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    /** Lister tous les tickets (admin) */
    @Transactional(readOnly = true)
    public List<SupportTicketDTO> getAllTickets() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /** Statistiques des tickets (admin) */
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

    /** Envoyer un message admin dans un ticket + notifier le propriétaire */
    @Transactional
    public TicketMessageDTO sendAdminMessage(Long ticketId, SendMessageRequest request, String adminEmail) {
        SupportTicket ticket = findTicketById(ticketId);
        User admin = findUserByEmail(adminEmail);

        TicketMessage message = TicketMessage.builder()
                .content(request.getContent())
                .admin(true)
                .author(admin)
                .ticket(ticket)
                .createdAt(Instant.now())
                .build();

        TicketMessage saved = messageRepository.save(message);

        // Passer le ticket en IN_PROGRESS s'il est toujours OPEN
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }
        ticket.setUpdatedAt(Instant.now());
        repository.save(ticket);

        // Notifier le propriétaire du ticket
        String ownerEmail = ticket.getOwner().getEmail();
        notificationService.send(
                ownerEmail,
                NotificationType.SUPPORT_REPLY,
                "Réponse à votre ticket #" + ticket.getId(),
                "Votre ticket \"" + ticket.getSubject() + "\" a reçu une nouvelle réponse.",
                "/lab/support",
                "{\"ticketId\":" + ticket.getId() + "}"
        );

        log.info("Admin message sent on ticket: ticketId={}, messageId={}, admin={}", ticketId, saved.getId(), adminEmail);
        return mapper.toMessageDTO(saved);
    }

    /** Changer le statut d'un ticket (admin) */
    @Transactional
    public SupportTicketDTO updateStatus(Long id, TicketStatus status) {
        SupportTicket ticket = findTicketById(id);
        ticket.setStatus(status);
        ticket.setUpdatedAt(Instant.now());

        SupportTicket saved = repository.save(ticket);
        log.info("Admin changed ticket status: id={}, status={}", saved.getId(), status);
        return mapper.toDTO(saved);
    }

    /** Changer la priorité d'un ticket (admin) */
    @Transactional
    public SupportTicketDTO updatePriority(Long id, TicketPriority priority) {
        SupportTicket ticket = findTicketById(id);
        ticket.setPriority(priority);
        ticket.setUpdatedAt(Instant.now());

        SupportTicket saved = repository.save(ticket);
        log.info("Admin changed ticket priority: id={}, priority={}", saved.getId(), priority);
        return mapper.toDTO(saved);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
    }

    private SupportTicket findTicketById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket introuvable avec l'ID : " + id));
    }

    private SupportTicket findTicketOwnedBy(Long ticketId, String userEmail) {
        SupportTicket ticket = findTicketById(ticketId);
        User owner = findUserByEmail(userEmail);

        if (!ticket.getOwner().getId().equals(owner.getId())) {
            throw new ResourceNotFoundException("Ticket introuvable avec l'ID : " + ticketId);
        }

        return ticket;
    }
}

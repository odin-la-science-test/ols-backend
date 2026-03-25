package com.odinlascience.backend.modules.support.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.support.dto.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service du module Support cote utilisateur.
 * Les operations admin sont dans {@link SupportTicketAdminService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository repository;
    private final TicketMessageRepository messageRepository;
    private final SupportTicketMapper mapper;
    private final UserHelper userHelper;
    // ─── User CRUD ───

    /** Créer un ticket de support */
    @Transactional
    public SupportTicketDTO create(CreateTicketRequest request, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);

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

    /** Rechercher dans mes tickets */
    @Transactional(readOnly = true)
    public List<SupportTicketDTO> search(String query, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.searchByOwner(owner.getId(), query)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /** Lister mes tickets */
    @Transactional(readOnly = true)
    public List<SupportTicketDTO> getMyTickets(String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
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
        User author = userHelper.findByEmail(userEmail);

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

    // ─── Helpers ───

    private SupportTicket findTicketById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket introuvable avec l'ID : " + id));
    }

    private SupportTicket findTicketOwnedBy(Long ticketId, String userEmail) {
        SupportTicket ticket = findTicketById(ticketId);
        return userHelper.verifyOwnership(ticket, userEmail, "Ticket", ticketId);
    }
}

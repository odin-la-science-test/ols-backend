package com.odinlascience.backend.modules.support.service;

import com.odinlascience.backend.modules.common.service.AbstractOwnedCrudService;
import com.odinlascience.backend.modules.common.service.HtmlSanitizer;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
public class SupportTicketService extends AbstractOwnedCrudService<SupportTicket, SupportTicketDTO, CreateTicketRequest, UpdateTicketRequest> {

    private final SupportTicketRepository repository;
    private final TicketMessageRepository messageRepository;
    private final SupportTicketMapper mapper;

    public SupportTicketService(UserHelper userHelper, ApplicationEventPublisher eventPublisher,
                                SupportTicketRepository repository,
                                TicketMessageRepository messageRepository, SupportTicketMapper mapper) {
        super(userHelper, eventPublisher);
        this.repository = repository;
        this.messageRepository = messageRepository;
        this.mapper = mapper;
    }

    // ─── Méthodes abstraites ───

    @Override
    protected SupportTicket toEntity(CreateTicketRequest request, User owner) {
        return SupportTicket.builder()
                .subject(HtmlSanitizer.sanitize(request.getSubject()))
                .description(HtmlSanitizer.sanitize(request.getDescription()))
                .category(request.getCategory())
                .priority(TicketPriority.MEDIUM)
                .status(TicketStatus.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .owner(owner)
                .build();
    }

    @Override
    protected void applyUpdate(SupportTicket ticket, UpdateTicketRequest request) {
        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new IllegalStateException("Seuls les tickets ouverts peuvent être modifiés");
        }

        if (request.getSubject() != null) {
            ticket.setSubject(HtmlSanitizer.sanitize(request.getSubject()));
        }
        if (request.getDescription() != null) {
            ticket.setDescription(HtmlSanitizer.sanitize(request.getDescription()));
        }
        if (request.getCategory() != null) {
            ticket.setCategory(request.getCategory());
        }

        ticket.setUpdatedAt(Instant.now());
    }

    @Override
    protected SupportTicketDTO toDTO(SupportTicket entity) {
        return mapper.toDTO(entity);
    }

    @Override
    public Class<SupportTicketDTO> getDtoClass() {
        return SupportTicketDTO.class;
    }

    @Override
    protected String getEntityName() {
        return "Ticket";
    }

    @Override
    protected String getModuleSlug() {
        return "support";
    }

    @Override
    protected JpaRepository<SupportTicket, Long> getRepository() {
        return repository;
    }

    @Override
    protected List<SupportTicket> findAllByOwner(User owner) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(owner.getId());
    }

    @Override
    protected List<SupportTicket> searchByOwner(String query, Long ownerId) {
        return repository.searchByOwner(ownerId, query);
    }

    @Override
    protected Page<SupportTicket> findAllByOwnerPaged(User owner, Pageable pageable) {
        return Page.empty(pageable);
    }

    @Override
    protected Page<SupportTicket> searchByOwnerPaged(String query, Long ownerId, Pageable pageable) {
        return Page.empty(pageable);
    }

    // ─── Delete avec vérification de statut ───

    @Override
    @Transactional
    public void delete(Long id, String userEmail) {
        SupportTicket ticket = findEntityOwnedBy(id, userEmail);
        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new IllegalStateException("Seuls les tickets ouverts peuvent être supprimés");
        }
        super.delete(id, userEmail);
    }

    // ─── Messagerie ───

    /** Envoyer un message dans un ticket (user — doit en être le propriétaire, ticket non fermé) */
    @Transactional
    public TicketMessageDTO sendUserMessage(Long ticketId, SendMessageRequest request, String userEmail) {
        SupportTicket ticket = findEntityOwnedBy(ticketId, userEmail);
        User author = userHelper.findByEmail(userEmail);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new IllegalStateException("Impossible d'envoyer un message sur un ticket fermé");
        }

        TicketMessage message = TicketMessage.builder()
                .content(HtmlSanitizer.sanitize(request.getContent()))
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
}

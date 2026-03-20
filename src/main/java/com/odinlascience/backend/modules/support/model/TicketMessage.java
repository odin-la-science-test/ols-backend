package com.odinlascience.backend.modules.support.model;

import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

/**
 * Entité représentant un message dans le fil de discussion d'un ticket de support.
 * Les messages peuvent être envoyés par l'utilisateur ou par un admin.
 */
@Entity
@Table(name = "ticket_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Contenu du message */
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** Indique si le message provient d'un admin */
    @Builder.Default
    @Column(name = "is_admin", nullable = false)
    private Boolean admin = false;

    /** Date de création */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Auteur du message */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** Ticket associé */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;
}

package com.odinlascience.backend.modules.support.model;

import com.odinlascience.backend.modules.support.enums.TicketCategory;
import com.odinlascience.backend.modules.support.enums.TicketPriority;
import com.odinlascience.backend.modules.support.enums.TicketStatus;
import com.odinlascience.backend.modules.common.model.OwnedEntity;
import com.odinlascience.backend.modules.common.model.SoftDeletable;
import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un ticket de support utilisateur.
 */
@Entity
@Table(name = "support_tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket implements OwnedEntity, SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Sujet du ticket */
    @NotBlank
    @Column(nullable = false)
    private String subject;

    /** Description détaillée */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Catégorie du ticket */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    /** Priorité (définie par défaut à MEDIUM, modifiable uniquement par un admin) */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;

    /** Statut du ticket */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    /** Fil de messages de ce ticket */
    @Builder.Default
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<TicketMessage> messages = new ArrayList<>();

    /** Date de création */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Date de dernière modification */
    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Date de suppression logique (soft delete) */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Propriétaire du ticket */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}

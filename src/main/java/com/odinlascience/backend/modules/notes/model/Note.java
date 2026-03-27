package com.odinlascience.backend.modules.notes.model;

import com.odinlascience.backend.modules.common.model.AuditableEntity;
import com.odinlascience.backend.modules.common.model.OwnedEntity;
import com.odinlascience.backend.modules.common.model.SoftDeletable;
import com.odinlascience.backend.modules.notes.enums.NoteColor;
import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

/**
 * Entité représentant une note de cahier de laboratoire.
 * Chaque note appartient à un utilisateur et peut être épinglée, colorée et taguée.
 */
@Entity
@Table(name = "notes")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note extends AuditableEntity implements OwnedEntity, SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre de la note */
    @NotBlank
    @Column(nullable = false)
    private String title;

    /** Contenu textuel de la note */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** Couleur d'accent de la note */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private NoteColor color;

    /** Note épinglée en haut de la liste */
    @Builder.Default
    @Column(nullable = false)
    private Boolean pinned = false;

    /** Tags séparés par des virgules (ex: "biochimie,protocole,urgent") */
    @Column(length = 500)
    private String tags;

    /** Date de suppression logique (soft delete) */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Propriétaire de la note */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}

package com.odinlascience.backend.modules.history.model;

import com.odinlascience.backend.modules.common.model.AuditableEntity;
import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entree d'historique enregistrant une action CRUD pour le undo/redo persistant.
 * Creee automatiquement par le listener sur CrudActionEvent.
 */
@Entity
@Table(name = "history_entries", indexes = {
        @Index(name = "idx_history_owner_module", columnList = "owner_id, module_slug, created_at")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntry extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Slug du module concerne (ex: "notes", "contacts") */
    @Column(name = "module_slug", nullable = false, length = 50)
    private String moduleSlug;

    /** Type d'action */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 10)
    private HistoryActionType actionType;

    /** ID de l'entite cible */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /** Cle i18n pour le label (ex: "history.notes.create") */
    @Column(name = "label_key", nullable = false)
    private String labelKey;

    /** Nom d'icone Lucide (ex: "plus", "pencil", "trash-2") */
    @Column(length = 50)
    private String icon;

    /** Snapshot JSON de l'entite avant l'action (null pour CREATE) */
    @Column(name = "previous_data", columnDefinition = "TEXT")
    private String previousData;

    /** Snapshot JSON de l'entite apres l'action (null pour DELETE) */
    @Column(name = "new_data", columnDefinition = "TEXT")
    private String newData;

    /** Proprietaire de l'entree */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}

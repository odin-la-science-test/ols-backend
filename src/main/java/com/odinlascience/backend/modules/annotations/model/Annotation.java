package com.odinlascience.backend.modules.annotations.model;

import com.odinlascience.backend.modules.annotations.enums.AnnotationColor;
import com.odinlascience.backend.modules.common.model.AuditableEntity;
import com.odinlascience.backend.modules.common.model.OwnedEntity;
import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entite representant une annotation personnelle sur une entite quelconque
 * (bacterie, champignon, contact, etc.).
 * Chaque annotation appartient a un utilisateur (owned entity).
 */
@Entity
@Table(name = "annotations")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Annotation extends AuditableEntity implements OwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Type de l'entite annotee (ex: "bacterium", "fungus", "contact") */
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    /** ID de l'entite annotee */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /** Contenu de l'annotation (texte Markdown) */
    @Column(nullable = false, length = 2000)
    private String content;

    /** Couleur de l'annotation */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnotationColor color = AnnotationColor.YELLOW;

    /** Proprietaire de l'annotation */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}

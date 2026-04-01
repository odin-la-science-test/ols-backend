package com.odinlascience.backend.modules.studycollections.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entite representant un element dans une collection d'etude.
 * Reference une entite d'un module specifique (ex: bacteriology, mycology).
 */
@Entity
@Table(name = "study_collection_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyCollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Collection parente */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private StudyCollection collection;

    /** Identifiant du module source (ex: "bacteriology", "mycology") */
    @Column(name = "module_id", nullable = false, length = 100)
    private String moduleId;

    /** ID de l'entite dans le module source */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /** Note personnelle sur cet element */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Date d'ajout a la collection */
    @Builder.Default
    @Column(name = "added_at", nullable = false, updatable = false)
    private Instant addedAt = Instant.now();
}

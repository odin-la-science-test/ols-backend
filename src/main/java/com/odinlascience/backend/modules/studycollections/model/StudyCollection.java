package com.odinlascience.backend.modules.studycollections.model;

import com.odinlascience.backend.modules.common.model.AuditableEntity;
import com.odinlascience.backend.modules.common.model.OwnedEntity;
import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entite representant une collection d'etude (playlist) regroupant des entites
 * de differents modules. Chaque collection appartient a un utilisateur.
 */
@Entity
@Table(name = "study_collections")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyCollection extends AuditableEntity implements OwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de la collection */
    @Column(nullable = false, length = 255)
    private String name;

    /** Description optionnelle */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Proprietaire de la collection */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /** Elements de la collection, tries par date d'ajout */
    @Builder.Default
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("addedAt ASC")
    private List<StudyCollectionItem> items = new ArrayList<>();
}

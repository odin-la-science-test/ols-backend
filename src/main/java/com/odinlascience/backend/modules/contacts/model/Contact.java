package com.odinlascience.backend.modules.contacts.model;

import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entité représentant un contact dans le carnet de contacts d'un utilisateur.
 * Chaque contact appartient à un owner et peut être marqué comme favori.
 */
@Entity
@Table(name = "contacts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"owner_id", "email"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Prénom du contact */
    @Column(length = 255)
    private String firstName;

    /** Nom du contact */
    @Column(length = 255)
    private String lastName;

    /** Adresse email du contact */
    @Column(length = 255)
    private String email;

    /** Numéro de téléphone */
    @Column(length = 30)
    private String phone;

    /** Organisation / Laboratoire / Institution */
    @Column(length = 255)
    private String organization;

    /** Titre / Fonction (ex: "Chercheur", "Technicien") */
    @Column(name = "job_title", length = 255)
    private String jobTitle;

    /** Notes libres sur le contact */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Contact favori / épinglé */
    @Builder.Default
    @Column(nullable = false)
    private Boolean favorite = false;

    /** Date de création */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Date de dernière modification */
    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Propriétaire du contact (le user qui a ce contact dans son carnet) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}

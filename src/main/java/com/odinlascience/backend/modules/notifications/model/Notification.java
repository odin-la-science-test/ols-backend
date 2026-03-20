package com.odinlascience.backend.modules.notifications.model;

import com.odinlascience.backend.modules.notifications.enums.NotificationType;
import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entité représentant une notification utilisateur.
 * Les notifications sont créées côté serveur et récupérées par polling côté client.
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Type de notification */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    /** Titre court de la notification */
    @Column(nullable = false)
    private String title;

    /** Message descriptif */
    @Column(columnDefinition = "TEXT")
    private String message;

    /** Notification lue ou non */
    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

    /** URL d'action (ex: "/lab/quickshare" pour naviguer vers le partage) */
    @Column(name = "action_url")
    private String actionUrl;

    /** Métadonnées additionnelles en JSON (ex: shareCode, senderName, etc.) */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    /** Date de création */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Destinataire de la notification */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
}

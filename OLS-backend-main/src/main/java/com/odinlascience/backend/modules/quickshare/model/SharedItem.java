package com.odinlascience.backend.modules.quickshare.model;

import com.odinlascience.backend.modules.common.model.OwnedEntity;
import com.odinlascience.backend.modules.common.model.SoftDeletable;
import com.odinlascience.backend.modules.quickshare.enums.ShareType;
import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un élément partagé via QuickShare.
 * Peut contenir du texte OU un/plusieurs fichier(s), identifié par un code de partage unique.
 */
@Entity
@Table(name = "shared_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedItem implements OwnedEntity, SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code court unique de partage (ex: "aB3xK9mQ") */
    @NotBlank
    @Column(name = "share_code", unique = true, nullable = false, length = 12)
    private String shareCode;

    /** Titre / label donné par l'utilisateur */
    @Column(length = 255)
    private String title;

    /** Type de partage : TEXT ou FILE */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareType type;

    // ─── Champs TEXT ───
    /** Contenu texte (pour type=TEXT uniquement) */
    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    // ─── Champs FILE ───
    /** Liste des fichiers attachés (pour type=FILE) */
    @Builder.Default
    @OneToMany(mappedBy = "sharedItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SharedFile> files = new ArrayList<>();

    // ─── Métadonnées ───
    /** Nombre de téléchargements / consultations */
    @Builder.Default
    @Column(name = "download_count")
    private Integer downloadCount = 0;

    /** Nombre max de téléchargements autorisés (null = illimité) */
    @Column(name = "max_downloads")
    private Integer maxDownloads;

    /** Date d'expiration (null = jamais) */
    @Column(name = "expires_at")
    private Instant expiresAt;

    /** Date de création */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Date de suppression logique (soft delete) */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    // ─── Relations ───
    /** Propriétaire du partage */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /** Email du destinataire direct (partage ciblé, optionnel) */
    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    // ─── Méthodes utilitaires ───
    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isDownloadLimitReached() {
        return maxDownloads != null && downloadCount >= maxDownloads;
    }

    /** Incrémente le compteur de téléchargements */
    public void incrementDownloadCount() {
        this.downloadCount = (this.downloadCount == null ? 0 : this.downloadCount) + 1;
    }
}

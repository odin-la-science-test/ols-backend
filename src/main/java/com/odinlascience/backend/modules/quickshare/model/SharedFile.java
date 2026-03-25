package com.odinlascience.backend.modules.quickshare.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant un fichier attaché à un partage QuickShare.
 * Un SharedItem de type FILE peut contenir un ou plusieurs SharedFile.
 */
@Entity
@Table(name = "shared_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom original du fichier uploadé */
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    /** Nom de stockage interne (UUID) */
    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    /** Type MIME du fichier */
    @Column(name = "content_type")
    private String contentType;

    /** Taille du fichier en octets */
    @Column(name = "file_size")
    private Long fileSize;

    /** Partage parent */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_item_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SharedItem sharedItem;
}

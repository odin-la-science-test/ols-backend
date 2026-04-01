package com.odinlascience.backend.modules.history.repository;

import com.odinlascience.backend.modules.history.model.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface HistoryEntryRepository extends JpaRepository<HistoryEntry, Long> {

    /** Entries d'un utilisateur pour un module, triees par date ASC (plus ancien en premier) */
    List<HistoryEntry> findByOwnerIdAndModuleSlugOrderByCreatedAtAsc(Long ownerId, String moduleSlug);

    /** Supprimer toutes les entries d'un scope */
    @Modifying
    void deleteByOwnerIdAndModuleSlug(Long ownerId, String moduleSlug);

    /** Couper le redo stack : supprimer les entries creees apres une date donnee */
    @Modifying
    @Query("DELETE FROM HistoryEntry h WHERE h.owner.id = :ownerId AND h.moduleSlug = :slug AND h.createdAt > :after")
    void truncateAfter(@Param("ownerId") Long ownerId, @Param("slug") String slug, @Param("after") Instant after);

    /** Toutes les entries d'un utilisateur, triees par date DESC */
    List<HistoryEntry> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    /** Purge : supprimer les entries plus anciennes que la date limite */
    @Modifying
    @Query("DELETE FROM HistoryEntry h WHERE h.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") Instant cutoff);
}

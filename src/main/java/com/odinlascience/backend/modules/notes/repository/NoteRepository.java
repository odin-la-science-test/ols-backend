package com.odinlascience.backend.modules.notes.repository;

import com.odinlascience.backend.modules.notes.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /** Toutes les notes d'un utilisateur (non supprimées), épinglées d'abord, puis par date décroissante */
    List<Note> findByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(Long ownerId);

    /** Recherche par titre ou contenu (insensible à la casse, exclut supprimées) */
    @Query("SELECT n FROM Note n WHERE n.owner.id = :ownerId AND n.deletedAt IS NULL " +
           "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(n.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY n.pinned DESC, n.updatedAt DESC")
    List<Note> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);

    /** Recherche par tag uniquement (prefix match, insensible à la casse, exclut supprimées) */
    @Query("SELECT n FROM Note n WHERE n.owner.id = :ownerId AND n.deletedAt IS NULL " +
           "AND LOWER(n.tags) LIKE LOWER(CONCAT('%', :tag, '%')) " +
           "ORDER BY n.pinned DESC, n.updatedAt DESC")
    List<Note> searchByTag(@Param("ownerId") Long ownerId, @Param("tag") String tag);

    /** Version paginée : notes d'un utilisateur (non supprimées), épinglées d'abord, puis par date décroissante */
    Page<Note> findByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(Long ownerId, Pageable pageable);

    /** Version paginée : recherche par titre, contenu ou tags (insensible à la casse, exclut supprimées) */
    @Query("SELECT n FROM Note n WHERE n.owner.id = :ownerId AND n.deletedAt IS NULL " +
           "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(n.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY n.pinned DESC, n.updatedAt DESC")
    Page<Note> searchByOwnerPaged(@Param("ownerId") Long ownerId, @Param("query") String query, Pageable pageable);
}

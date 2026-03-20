package com.odinlascience.backend.modules.notes.repository;

import com.odinlascience.backend.modules.notes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /** Toutes les notes d'un utilisateur, épinglées d'abord, puis par date de mise à jour décroissante */
    List<Note> findByOwnerIdOrderByPinnedDescUpdatedAtDesc(Long ownerId);

    /** Recherche par titre ou contenu (insensible à la casse) */
    @Query("SELECT n FROM Note n WHERE n.owner.id = :ownerId " +
           "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(n.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY n.pinned DESC, n.updatedAt DESC")
    List<Note> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);
}

package com.odinlascience.backend.modules.studycollections.repository;

import com.odinlascience.backend.modules.studycollections.model.StudyCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyCollectionRepository extends JpaRepository<StudyCollection, Long> {

    /** Toutes les collections d'un utilisateur, triees par date de creation descendante */
    List<StudyCollection> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    /** Recherche par nom ou description (insensible a la casse) */
    @Query("SELECT sc FROM StudyCollection sc WHERE sc.owner.id = :ownerId " +
           "AND (LOWER(sc.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(sc.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY sc.createdAt DESC")
    List<StudyCollection> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);
}

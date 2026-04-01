package com.odinlascience.backend.modules.studycollections.repository;

import com.odinlascience.backend.modules.studycollections.model.StudyCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyCollectionRepository extends JpaRepository<StudyCollection, Long> {

    /** Toutes les collections d'un utilisateur, triees par date de creation descendante */
    List<StudyCollection> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long ownerId);

    /** Toutes les collections d'un utilisateur, triees par date de creation descendante (page) */
    Page<StudyCollection> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    /** Supprimer toutes les collections d'un utilisateur (RGPD) */
    void deleteByOwnerId(Long ownerId);

    /** Recherche par nom ou description (insensible a la casse) */
    @Query("SELECT sc FROM StudyCollection sc WHERE sc.owner.id = :ownerId AND sc.deletedAt IS NULL " +
           "AND (LOWER(sc.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(sc.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY sc.createdAt DESC")
    List<StudyCollection> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);

    /** Recherche par nom ou description avec pagination (insensible a la casse) */
    @Query("SELECT sc FROM StudyCollection sc WHERE sc.owner.id = :ownerId AND sc.deletedAt IS NULL " +
           "AND (LOWER(sc.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(sc.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY sc.createdAt DESC")
    Page<StudyCollection> searchByOwnerPaged(@Param("ownerId") Long ownerId, @Param("query") String query, Pageable pageable);
}

package com.odinlascience.backend.modules.annotations.repository;

import com.odinlascience.backend.modules.annotations.model.Annotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {

    /** Toutes les annotations d'un utilisateur, les plus recentes en premier */
    List<Annotation> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    /** Annotations d'un utilisateur pour une entite donnee */
    List<Annotation> findByOwnerIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            Long ownerId, String entityType, Long entityId);

    /** Recherche dans le contenu des annotations d'un utilisateur (insensible a la casse) */
    @Query("SELECT a FROM Annotation a WHERE a.owner.id = :ownerId " +
           "AND (LOWER(a.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(a.entityType) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY a.createdAt DESC")
    List<Annotation> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);

    /** Version paginee : toutes les annotations d'un utilisateur */
    Page<Annotation> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    /** Version paginee : recherche dans le contenu */
    @Query("SELECT a FROM Annotation a WHERE a.owner.id = :ownerId " +
           "AND (LOWER(a.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(a.entityType) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY a.createdAt DESC")
    Page<Annotation> searchByOwnerPaged(
            @Param("ownerId") Long ownerId, @Param("query") String query, Pageable pageable);
}

package com.odinlascience.backend.modules.quickshare.repository;

import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SharedItemRepository extends JpaRepository<SharedItem, Long> {

    /** Trouver un élément par son code de partage (non supprimé) */
    Optional<SharedItem> findByShareCodeAndDeletedAtIsNull(String shareCode);

    /** Tous les partages d'un utilisateur, triés par date de création décroissante */
    List<SharedItem> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long ownerId);

    /** Trouver les éléments expirés (non supprimés) */
    List<SharedItem> findByExpiresAtBeforeAndDeletedAtIsNull(LocalDateTime dateTime);

    /** Supprimer tous les partages d'un utilisateur (RGPD) */
    void deleteByOwnerId(Long ownerId);

    /** Recherche par titre, code de partage ou contenu texte (insensible à la casse) */
    @Query("SELECT s FROM SharedItem s WHERE s.owner.id = :ownerId AND s.deletedAt IS NULL " +
           "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.shareCode) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.textContent) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY s.createdAt DESC")
    List<SharedItem> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);
}

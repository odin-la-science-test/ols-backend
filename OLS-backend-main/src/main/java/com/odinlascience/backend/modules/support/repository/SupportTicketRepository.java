package com.odinlascience.backend.modules.support.repository;

import com.odinlascience.backend.modules.support.enums.TicketStatus;
import com.odinlascience.backend.modules.support.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    /** Tous les tickets d'un utilisateur, du plus récent au plus ancien */
    List<SupportTicket> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long ownerId);

    /** Tous les tickets, du plus récent au plus ancien (admin) */
    List<SupportTicket> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    /** Compter les tickets par statut (non supprimés) */
    long countByStatusAndDeletedAtIsNull(TicketStatus status);

    /** Supprimer tous les tickets d'un utilisateur (RGPD) */
    void deleteByOwnerId(Long ownerId);

    /** Recherche par sujet ou description (insensible à la casse) */
    @Query("SELECT t FROM SupportTicket t WHERE t.owner.id = :ownerId AND t.deletedAt IS NULL " +
           "AND (LOWER(t.subject) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<SupportTicket> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);
}

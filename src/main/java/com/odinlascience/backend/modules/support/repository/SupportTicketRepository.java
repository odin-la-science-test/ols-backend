package com.odinlascience.backend.modules.support.repository;

import com.odinlascience.backend.modules.support.enums.TicketStatus;
import com.odinlascience.backend.modules.support.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    /** Tous les tickets d'un utilisateur, du plus récent au plus ancien */
    List<SupportTicket> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    /** Tous les tickets, du plus récent au plus ancien (admin) */
    List<SupportTicket> findAllByOrderByCreatedAtDesc();

    /** Compter les tickets par statut */
    long countByStatus(TicketStatus status);
}

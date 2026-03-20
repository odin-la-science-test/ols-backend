package com.odinlascience.backend.modules.support.repository;

import com.odinlascience.backend.modules.support.model.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {

    /** Tous les messages d'un ticket, du plus ancien au plus récent */
    List<TicketMessage> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}

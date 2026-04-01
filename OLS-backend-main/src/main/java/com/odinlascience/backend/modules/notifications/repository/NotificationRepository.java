package com.odinlascience.backend.modules.notifications.repository;

import com.odinlascience.backend.modules.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Toutes les notifications d'un utilisateur, les plus récentes en premier */
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    /** Nombre de notifications non lues */
    long countByRecipientIdAndReadFalse(Long recipientId);

    /** Supprimer toutes les notifications d'un utilisateur */
    @Modifying
    void deleteByRecipientId(Long recipientId);

    /** Marquer toutes les notifications d'un utilisateur comme lues */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipient.id = :recipientId AND n.read = false")
    int markAllAsReadByRecipientId(@Param("recipientId") Long recipientId);
}

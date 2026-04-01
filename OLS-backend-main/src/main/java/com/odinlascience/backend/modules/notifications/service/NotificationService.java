package com.odinlascience.backend.modules.notifications.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.notifications.dto.NotificationDTO;
import com.odinlascience.backend.modules.notifications.dto.UnreadCountDTO;
import com.odinlascience.backend.modules.notifications.enums.NotificationType;
import com.odinlascience.backend.modules.notifications.mapper.NotificationMapper;
import com.odinlascience.backend.modules.notifications.model.Notification;
import com.odinlascience.backend.modules.notifications.repository.NotificationRepository;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service de gestion des notifications.
 * Fournit l'envoi, la lecture et le marquage des notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final UserQuerySPI userQuerySPI;
    private final SseEmitterService sseEmitterService;

    // ─── Envoyer une notification ───

    /**
     * Crée et persiste une notification pour un utilisateur donné.
     *
     * @param recipientEmail email du destinataire
     * @param type           type de notification
     * @param title          titre court
     * @param message        message détaillé (optionnel)
     * @param actionUrl      URL de navigation (optionnel)
     * @param metadata       métadonnées JSON (optionnel)
     */
    @Transactional
    public NotificationDTO send(
            String recipientEmail,
            NotificationType type,
            String title,
            String message,
            String actionUrl,
            String metadata
    ) {
        User recipient = userQuerySPI.findByEmail(recipientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + recipientEmail));

        Notification notification = Notification.builder()
                .type(type)
                .title(title)
                .message(message)
                .actionUrl(actionUrl)
                .metadata(metadata)
                .createdAt(Instant.now())
                .recipient(recipient)
                .build();

        Notification saved = repository.save(notification);
        NotificationDTO dto = mapper.toDTO(saved);

        sseEmitterService.pushNotification(recipient.getId(), dto);

        log.info("Notification sent: type={}, recipient={}, title={}", type, recipientEmail, title);
        return dto;
    }

    // ─── Mes notifications ───

    @Transactional(readOnly = true)
    public List<NotificationDTO> getMyNotifications(String userEmail) {
        User user = findUserByEmail(userEmail);
        return repository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── Compteur non lues ───

    @Transactional(readOnly = true)
    public UnreadCountDTO getUnreadCount(String userEmail) {
        User user = findUserByEmail(userEmail);
        long count = repository.countByRecipientIdAndReadFalse(user.getId());
        return UnreadCountDTO.builder().count(count).build();
    }

    // ─── Marquer une notification comme lue ───

    @Transactional
    public NotificationDTO markAsRead(Long id, String userEmail) {
        Notification notification = findOwnedNotification(id, userEmail);
        notification.setRead(true);
        return mapper.toDTO(repository.save(notification));
    }

    // ─── Marquer toutes comme lues ───

    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = findUserByEmail(userEmail);
        int updated = repository.markAllAsReadByRecipientId(user.getId());
        log.info("Marked {} notifications as read for user={}", updated, userEmail);
    }

    // ─── Supprimer une notification ───

    @Transactional
    public void delete(Long id, String userEmail) {
        Notification notification = findOwnedNotification(id, userEmail);
        repository.delete(notification);
        log.info("Notification deleted: id={}, user={}", id, userEmail);
    }

    // ─── Helpers ───

    private User findUserByEmail(String email) {
        return userQuerySPI.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
    }

    private Notification findOwnedNotification(Long id, String userEmail) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable : " + id));

        User user = findUserByEmail(userEmail);
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Notification introuvable : " + id);
        }
        return notification;
    }
}

package com.odinlascience.backend.modules.notifications.listener;

import com.odinlascience.backend.modules.common.event.MemberAddedEvent;
import com.odinlascience.backend.modules.common.event.MemberRemovedEvent;
import com.odinlascience.backend.modules.common.event.MemberRoleChangedEvent;
import com.odinlascience.backend.modules.common.event.ModuleAccessGrantedEvent;
import com.odinlascience.backend.modules.common.event.NewLoginEvent;
import com.odinlascience.backend.modules.common.event.ShareCreatedEvent;
import com.odinlascience.backend.modules.common.event.TicketRepliedEvent;
import com.odinlascience.backend.modules.common.event.TicketStatusChangedEvent;
import com.odinlascience.backend.modules.notifications.enums.NotificationType;
import com.odinlascience.backend.modules.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Ecoute les evenements inter-modules pour creer les notifications correspondantes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onShareCreated(ShareCreatedEvent event) {
        String senderName = event.senderFullName();
        if (senderName == null || senderName.isBlank()) senderName = event.senderEmail();

        String title = senderName.trim() + " vous a partagé du contenu";
        String message = event.shareTitle() != null && !event.shareTitle().isBlank()
                ? "\"" + event.shareTitle() + "\" — Code : " + event.shareCode()
                : "Code de partage : " + event.shareCode();

        String metadata = "{\"shareCode\":\"" + event.shareCode()
                + "\",\"senderEmail\":\"" + event.senderEmail() + "\"}";

        try {
            notificationService.send(event.recipientEmail(),
                    NotificationType.QUICKSHARE_RECEIVED, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification pour partage {}: {}", event.shareCode(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketReplied(TicketRepliedEvent event) {
        String title = "Réponse à votre ticket #" + event.ticketId();
        String message = "Votre ticket \"" + event.ticketSubject() + "\" a reçu une nouvelle réponse.";
        String metadata = "{\"ticketId\":" + event.ticketId() + "}";

        try {
            notificationService.send(event.recipientEmail(),
                    NotificationType.SUPPORT_REPLY, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification pour ticket #{}: {}", event.ticketId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketStatusChanged(TicketStatusChangedEvent event) {
        String title = "Ticket #" + event.ticketId() + " — statut mis à jour";
        String message = "Votre ticket \"" + event.ticketSubject() + "\" est désormais : " + event.newStatus() + ".";
        String metadata = "{\"ticketId\":" + event.ticketId() + ",\"newStatus\":\"" + event.newStatus() + "\"}";

        try {
            notificationService.send(event.recipientEmail(),
                    NotificationType.SUPPORT_STATUS_CHANGED, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification statut ticket #{}: {}", event.ticketId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNewLogin(NewLoginEvent event) {
        String title = "Nouvelle connexion detectee";
        String message = "Connexion depuis " + event.deviceInfo() + " (" + event.ipAddress() + ")";
        String metadata = "{\"deviceInfo\":\"" + event.deviceInfo()
                + "\",\"ipAddress\":\"" + event.ipAddress() + "\"}";

        try {
            notificationService.send(event.userEmail(),
                    NotificationType.NEW_LOGIN, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification nouvelle connexion pour {}: {}", event.userEmail(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onModuleAccessGranted(ModuleAccessGrantedEvent event) {
        String title = "Accès débloqué : " + event.moduleTitle();
        String message = "Vous avez désormais accès au module \"" + event.moduleTitle() + "\".";
        String metadata = "{\"moduleKey\":\"" + event.moduleKey() + "\"}";

        try {
            notificationService.send(event.userEmail(),
                    NotificationType.MODULE_ACCESS_GRANTED, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification acces module {}: {}", event.moduleKey(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberAdded(MemberAddedEvent event) {
        String title = "Invitation : " + event.organizationName();
        String message = event.addedByFullName() + " vous a invite en tant que " + event.roleName() + ".";
        String metadata = "{\"organizationId\":" + event.organizationId() + "}";

        try {
            notificationService.send(event.memberEmail(),
                    NotificationType.ORGANIZATION_INVITED, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification invitation org {}: {}", event.organizationId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberRemoved(MemberRemovedEvent event) {
        String title = "Retrait : " + event.organizationName();
        String message = "Vous avez ete retire de l'organisation \"" + event.organizationName() + "\".";
        String metadata = "{\"organizationId\":" + event.organizationId() + "}";

        try {
            notificationService.send(event.memberEmail(),
                    NotificationType.ORGANIZATION_REMOVED, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification retrait org {}: {}", event.organizationId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberRoleChanged(MemberRoleChangedEvent event) {
        String title = "Role modifie : " + event.organizationName();
        String message = "Votre role dans \"" + event.organizationName() + "\" a change de " + event.oldRole() + " a " + event.newRole() + ".";
        String metadata = "{\"organizationId\":" + event.organizationId()
                + ",\"oldRole\":\"" + event.oldRole() + "\",\"newRole\":\"" + event.newRole() + "\"}";

        try {
            notificationService.send(event.memberEmail(),
                    NotificationType.ORGANIZATION_ROLE_CHANGED, title, message,
                    event.actionUrl(), metadata);
        } catch (Exception e) {
            log.warn("Echec notification changement role org {}: {}", event.organizationId(), e.getMessage());
        }
    }
}

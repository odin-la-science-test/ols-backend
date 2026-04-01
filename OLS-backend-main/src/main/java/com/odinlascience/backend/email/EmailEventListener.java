package com.odinlascience.backend.email;

import com.odinlascience.backend.modules.common.event.MemberAddedEvent;
import com.odinlascience.backend.modules.common.event.TicketRepliedEvent;
import com.odinlascience.backend.modules.common.event.TicketStatusChangedEvent;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.model.UserPreferences;
import com.odinlascience.backend.user.repository.UserPreferencesRepository;
import com.odinlascience.backend.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

/**
 * Ecoute les evenements inter-modules pour envoyer des emails de notification.
 * Parallele au NotificationEventListener (qui gere les notifs in-app).
 * Les emails de notification sont desactivables via les preferences utilisateur.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final UserPreferencesRepository preferencesRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${mail.enabled:false}")
    private boolean mailEnabled;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketReplied(TicketRepliedEvent event) {
        if (!mailEnabled || !isEmailNotificationsEnabled(event.recipientEmail())) return;

        String firstName = getFirstName(event.recipientEmail());
        String title = "Reponse a votre ticket #" + event.ticketId();
        String message = "Votre ticket \"" + event.ticketSubject() + "\" a recu une nouvelle reponse.";

        try {
            emailService.sendHtmlEmail(event.recipientEmail(),
                    title,
                    "support-ticket-reply",
                    Map.of("firstName", firstName, "title", title,
                            "message", message, "actionUrl", event.actionUrl()));
        } catch (Exception e) {
            log.warn("Echec email pour ticket #{}: {}", event.ticketId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketStatusChanged(TicketStatusChangedEvent event) {
        if (!mailEnabled || !isEmailNotificationsEnabled(event.recipientEmail())) return;

        String firstName = getFirstName(event.recipientEmail());
        String title = "Ticket #" + event.ticketId() + " — statut mis a jour";
        String message = "Votre ticket \"" + event.ticketSubject() + "\" est desormais : " + event.newStatus() + ".";

        try {
            emailService.sendHtmlEmail(event.recipientEmail(),
                    title,
                    "support-ticket-reply",
                    Map.of("firstName", firstName, "title", title,
                            "message", message, "actionUrl", event.actionUrl()));
        } catch (Exception e) {
            log.warn("Echec email statut ticket #{}: {}", event.ticketId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberAdded(MemberAddedEvent event) {
        if (!mailEnabled || !isEmailNotificationsEnabled(event.memberEmail())) return;

        try {
            emailService.sendHtmlEmail(event.memberEmail(),
                    "Invitation : " + event.organizationName(),
                    "organization-invitation",
                    Map.of("inviterName", event.addedByFullName(),
                            "organizationName", event.organizationName(),
                            "roleName", event.roleName(),
                            "actionUrl", event.actionUrl()));
        } catch (Exception e) {
            log.warn("Echec email invitation org {}: {}", event.organizationId(), e.getMessage());
        }
    }

    /**
     * Verifie si l'utilisateur a active les notifications par email.
     * Par defaut, les notifications email sont activees.
     */
    private boolean isEmailNotificationsEnabled(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> preferencesRepository.findByUserId(user.getId()))
                .map(this::parseEmailNotificationsPref)
                .orElse(true);
    }

    private boolean parseEmailNotificationsPref(UserPreferences prefs) {
        if (prefs.getPreferencesJson() == null || prefs.getPreferencesJson().isBlank()) {
            return true;
        }
        try {
            JsonNode node = objectMapper.readTree(prefs.getPreferencesJson());
            JsonNode emailNotif = node.get("emailNotifications");
            return emailNotif == null || emailNotif.asBoolean(true);
        } catch (Exception e) {
            log.warn("Erreur parsing preferences: {}", e.getMessage());
            return true;
        }
    }

    private String getFirstName(String email) {
        return userRepository.findByEmail(email)
                .map(User::getFirstName)
                .orElse("Utilisateur");
    }
}

package com.odinlascience.backend.modules.contacts.listener;

import com.odinlascience.backend.modules.common.event.ShareCreatedEvent;
import com.odinlascience.backend.modules.contacts.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Ecoute les evenements inter-modules pour enrichir le carnet de contacts automatiquement.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContactEventListener {

    private final ContactService contactService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onShareCreated(ShareCreatedEvent event) {
        try {
            contactService.ensureContactExistsByEmail(event.senderEmail(), event.recipientEmail());
        } catch (Exception e) {
            log.warn("Echec auto-ajout contact pour {}: {}", event.recipientEmail(), e.getMessage());
        }
    }
}

package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand un partage QuickShare est cree avec un destinataire.
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → envoie une notification au destinataire</li>
 *   <li>contacts → auto-ajoute le destinataire dans le carnet du sender</li>
 * </ul>
 */
public record ShareCreatedEvent(
        String shareCode,
        String shareTitle,
        String senderEmail,
        String senderFullName,
        String recipientEmail,
        String actionUrl
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "quickshare";
    }
}

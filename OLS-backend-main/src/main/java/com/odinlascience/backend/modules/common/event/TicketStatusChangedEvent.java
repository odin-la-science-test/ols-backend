package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand un admin change le statut d'un ticket de support (RESOLVED, CLOSED).
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → notifie le proprietaire du ticket du changement de statut</li>
 * </ul>
 */
public record TicketStatusChangedEvent(
        Long ticketId,
        String ticketSubject,
        String recipientEmail,
        String newStatus,
        String actionUrl
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "support";
    }
}

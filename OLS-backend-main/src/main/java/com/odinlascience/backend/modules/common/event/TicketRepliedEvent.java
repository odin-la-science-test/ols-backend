package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand un admin repond a un ticket de support.
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → envoie une notification au proprietaire du ticket</li>
 * </ul>
 */
public record TicketRepliedEvent(
        Long ticketId,
        String ticketSubject,
        String recipientEmail,
        String actionUrl
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "support";
    }
}

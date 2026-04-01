package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand un membre est retire d'une organisation.
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → envoie une notification de retrait au membre</li>
 * </ul>
 */
public record MemberRemovedEvent(
        String organizationName,
        Long organizationId,
        String memberEmail,
        String removedByEmail,
        String actionUrl
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "organization";
    }
}

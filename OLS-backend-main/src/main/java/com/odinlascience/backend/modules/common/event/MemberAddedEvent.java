package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand un membre est ajoute/invite dans une organisation.
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → envoie une notification d'invitation au membre</li>
 * </ul>
 */
public record MemberAddedEvent(
        String organizationName,
        Long organizationId,
        String memberEmail,
        String memberFullName,
        String roleName,
        String addedByEmail,
        String addedByFullName,
        String actionUrl
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "organization";
    }
}

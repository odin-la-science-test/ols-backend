package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand le role d'un membre change dans une organisation.
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → envoie une notification de changement de role au membre</li>
 * </ul>
 */
public record MemberRoleChangedEvent(
        String organizationName,
        Long organizationId,
        String memberEmail,
        String oldRole,
        String newRole,
        String changedByEmail,
        String actionUrl
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "organization";
    }
}

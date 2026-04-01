package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand un utilisateur obtient l'acces a un module payant.
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → notifie l'utilisateur qu'il a desormais acces au module</li>
 * </ul>
 */
public record ModuleAccessGrantedEvent(
        String userEmail,
        String moduleKey
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "catalog";
    }
}

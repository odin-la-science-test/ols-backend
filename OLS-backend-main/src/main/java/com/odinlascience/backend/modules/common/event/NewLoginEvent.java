package com.odinlascience.backend.modules.common.event;

/**
 * Emis quand un utilisateur se connecte depuis un nouvel appareil.
 * <p>
 * Listeners potentiels :
 * <ul>
 *   <li>notifications → envoie une notification a l'utilisateur</li>
 * </ul>
 */
public record NewLoginEvent(
        String userEmail,
        String deviceInfo,
        String ipAddress,
        String actionUrl
) implements ModuleEvent {

    @Override
    public String sourceModule() {
        return "auth";
    }
}

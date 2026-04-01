package com.odinlascience.backend.modules.common.event;

/**
 * Marqueur pour les evenements de domaine inter-modules.
 * <p>
 * Communication inter-modules :
 * <ul>
 *   <li>"Il s'est passe quelque chose" → Domain Event (fire-and-forget, 0..N listeners)</li>
 *   <li>"J'ai besoin de donnees d'un autre module" → SPI interface (synchrone, 1:1)</li>
 * </ul>
 * Events : {@code modules/common/event/} — SPI : {@code modules/common/spi/}
 */
public interface ModuleEvent {

    /** Module source de l'evenement. */
    String sourceModule();
}

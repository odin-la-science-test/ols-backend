/**
 * Service Provider Interfaces (SPI) pour les requetes inter-modules synchrones.
 * <p>
 * Chaque module qui expose des donnees a d'autres modules declare une interface ici.
 * L'implementation reste dans le module concerne, Spring l'autowire automatiquement.
 * <p>
 * Utiliser un SPI quand un module a besoin de donnees d'un autre module (requete/reponse).
 * Pour les side-effects (fire-and-forget), utiliser un Domain Event dans {@code common/event/}.
 */
package com.odinlascience.backend.modules.common.spi;

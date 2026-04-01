package com.odinlascience.backend.modules.common.model;

import com.odinlascience.backend.user.model.User;

/**
 * Interface pour les entités possédées par un utilisateur.
 * Permet la vérification d'ownership de manière générique.
 */
public interface OwnedEntity {
    Long getId();
    User getOwner();
}

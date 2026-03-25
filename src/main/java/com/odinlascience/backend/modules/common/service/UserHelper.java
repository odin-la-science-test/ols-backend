package com.odinlascience.backend.modules.common.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.model.OwnedEntity;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Utilitaire partagé pour la résolution d'utilisateurs et la vérification d'ownership.
 * Évite la duplication de findUserByEmail et verifyOwnership dans chaque service owned.
 */
@Component
@RequiredArgsConstructor
public class UserHelper {

    private final UserQuerySPI userQuerySPI;

    /**
     * Trouve un utilisateur par email ou lève ResourceNotFoundException.
     */
    public User findByEmail(String email) {
        return userQuerySPI.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
    }

    /**
     * Vérifie que l'entité appartient à l'utilisateur authentifié.
     * Lève ResourceNotFoundException si l'ownership échoue (masque l'existence de la ressource).
     *
     * @param entity     l'entité owned
     * @param userEmail  email de l'utilisateur authentifié
     * @param entityName nom de l'entité (pour le message d'erreur)
     * @param entityId   ID de l'entité (pour le message d'erreur)
     * @return l'entité si l'ownership est vérifié
     */
    public <T extends OwnedEntity> T verifyOwnership(T entity, String userEmail, String entityName, Long entityId) {
        User owner = findByEmail(userEmail);
        if (!entity.getOwner().getId().equals(owner.getId())) {
            throw new ResourceNotFoundException(entityName + " introuvable avec l'ID : " + entityId);
        }
        return entity;
    }
}

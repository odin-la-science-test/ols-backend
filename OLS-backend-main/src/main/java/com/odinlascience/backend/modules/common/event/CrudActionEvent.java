package com.odinlascience.backend.modules.common.event;

/**
 * Evenement publie automatiquement par AbstractOwnedCrudService apres chaque operation CRUD.
 * Ecoute par le module history pour enregistrer l'historique des actions.
 */
public record CrudActionEvent(
        String moduleSlug,
        String actionType,
        Long entityId,
        String previousData,
        String newData,
        String userEmail,
        String labelKeySuffix,
        String icon
) implements ModuleEvent {
    @Override
    public String sourceModule() {
        return moduleSlug;
    }
}

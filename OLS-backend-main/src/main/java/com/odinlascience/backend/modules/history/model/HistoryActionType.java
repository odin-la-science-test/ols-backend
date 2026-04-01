package com.odinlascience.backend.modules.history.model;

/**
 * Types d'action enregistres dans l'historique.
 * Les toggles (pin, favorite) sont traites comme des UPDATE.
 */
public enum HistoryActionType {
    CREATE,
    UPDATE,
    DELETE
}

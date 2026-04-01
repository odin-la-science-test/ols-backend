package com.odinlascience.backend.modules.common.model;

import java.time.Instant;

/**
 * Interface marqueur pour le soft delete.
 * Les entités qui l'implémentent doivent déclarer un champ {@code deletedAt}.
 */
public interface SoftDeletable {

    Instant getDeletedAt();

    void setDeletedAt(Instant deletedAt);

    default boolean isDeleted() {
        return getDeletedAt() != null;
    }
}

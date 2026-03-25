package com.odinlascience.backend.modules.common.model;

/**
 * Record générique représentant un résultat de matching entre des critères
 * d'identification et une entité avec un score de correspondance.
 *
 * @param <T> Le type de l'entité identifiée
 * @param entity L'entité qui correspond aux critères
 * @param score Le score de correspondance en pourcentage (0-100)
 */
public record IdentifiableMatch<T>(T entity, Integer score) {}

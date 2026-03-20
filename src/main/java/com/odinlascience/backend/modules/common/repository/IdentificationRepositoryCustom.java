package com.odinlascience.backend.modules.common.repository;

import com.odinlascience.backend.modules.common.model.IdentifiableMatch;

import java.util.List;

/**
 * Interface générique pour les repositories qui supportent l'identification
 * par critères multiples avec scoring.
 *
 * @param <E> Le type de l'entité
 * @param <D> Le type du DTO contenant les critères
 */
public interface IdentificationRepositoryCustom<E, D> {
    
    /**
     * Trouve les meilleures correspondances pour un ensemble de critères.
     *
     * @param criteria Le DTO contenant les critères d'identification
     * @param limit Le nombre maximum de résultats à retourner
     * @return Une liste de matches avec leurs scores
     */
    List<IdentifiableMatch<E>> findBestMatches(D criteria, int limit);
}

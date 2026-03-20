package com.odinlascience.backend.modules.common.repository;

import com.odinlascience.backend.modules.common.annotation.IdentificationCriterion;
import com.odinlascience.backend.modules.common.model.IdentifiableMatch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Repository abstrait générique pour l'identification d'entités par critères multiples.
 * Utilise la réflexion et l'annotation @IdentificationCriterion pour calculer
 * automatiquement un score de correspondance.
 *
 * @param <E> Le type de l'entité
 * @param <D> Le type du DTO contenant les critères
 */
public abstract class AbstractIdentificationRepository<E, D> implements IdentificationRepositoryCustom<E, D> {

    @PersistenceContext
    protected EntityManager em;

    /**
     * Retourne la classe de l'entité pour les opérations JPA.
     */
    protected abstract Class<E> getEntityClass();

    /**
     * Retourne la classe du DTO pour l'analyse par réflexion.
     */
    protected abstract Class<D> getDtoClass();

    @Override
    public List<IdentifiableMatch<E>> findBestMatches(D criteria, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<E> root = query.from(getEntityClass());

        Expression<Integer> totalScore = cb.literal(0);
        int criteriaCount = 0;

        BeanWrapper criteriaWrapper = new BeanWrapperImpl(criteria);

        // Parcourir tous les champs du DTO et ajouter ceux annotés au scoring
        for (Field field : getDtoClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(IdentificationCriterion.class)) {
                String fieldName = field.getName();
                Object userValue = criteriaWrapper.getPropertyValue(fieldName);

                if (userValue != null) {
                    criteriaCount++;

                    Expression<Integer> matchPoint = cb.selectCase()
                            .when(cb.equal(root.get(fieldName), userValue), 1)
                            .otherwise(0)
                            .as(Integer.class);

                    totalScore = cb.sum(totalScore, matchPoint);
                }
            }
        }

        // Si aucun critère fourni, retourner liste vide
        if (criteriaCount == 0) return List.of();

        query.select(cb.tuple(root, totalScore));
        query.orderBy(cb.desc(totalScore));

        List<Tuple> results = em.createQuery(query)
                .setMaxResults(limit)
                .getResultList();

        int finalCount = criteriaCount;
        
        // Filtrer et mapper les résultats, ne garder que les matches avec au moins 20% de confiance
        return results.stream()
                .map(tuple -> {
                    E entity = tuple.get(0, getEntityClass());
                    Integer rawScore = tuple.get(1, Integer.class);
                    
                    int confidencePercent = (finalCount > 0) ? (int) (((double) rawScore / finalCount) * 100) : 0;
                    
                    return new IdentifiableMatch<>(entity, confidencePercent);
                })
                .filter(match -> match.score() >= 20) // Ne garder que les correspondances significatives (≥20%)
                .toList();
    }
}

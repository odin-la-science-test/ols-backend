package com.odinlascience.backend.modules.common.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.model.IdentifiableMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service abstrait generique pour la gestion d'entites identifiables.
 * Fournit les operations CRUD standard et l'identification par criteres multiples.
 * Le cache est gere par les sous-classes via @Cacheable avec leurs noms de cache.
 *
 * @param <E> Le type de l'entite
 * @param <D> Le type du DTO
 * @param <R> Le type du repository
 */
public abstract class AbstractIdentificationService<E, D, R extends JpaRepository<E, Long>> {

    protected final R repository;

    protected AbstractIdentificationService(R repository) {
        this.repository = repository;
    }

    protected abstract D toDTO(E entity);

    protected abstract D toDTO(E entity, Integer score);

    protected abstract String getEntityName();

    protected abstract List<IdentifiableMatch<E>> findBestMatches(D criteria, int limit);

    /**
     * Recupere toutes les entites. Les sous-classes overrident avec @Cacheable.
     */
    public List<D> getAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Recupere une entite par son ID. Les sous-classes overrident avec @Cacheable.
     */
    public D getById(Long id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " introuvable avec l'ID : " + id));
        return toDTO(entity);
    }

    /**
     * Recupere une entite par son code API.
     */
    public D getByApiCode(String apiCode) {
        Optional<E> entityOpt = findByApiCode(apiCode);
        E entity = entityOpt.orElseThrow(() -> new ResourceNotFoundException(
                "Aucun(e) " + getEntityName().toLowerCase() + " ne correspond au code API : " + apiCode));
        return toDTO(entity);
    }

    /**
     * Recherche des entites par nom d'espece.
     */
    public List<D> searchBySpecies(String query) {
        List<E> entities = findBySpeciesContaining(query);
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Identifie les entités correspondant le mieux aux critères fournis.
     * Non mise en cache car les criteres varient fortement.
     */
    public List<D> identifyByCriteria(D criteria) {
        List<IdentifiableMatch<E>> matches = findBestMatches(criteria, 20);

        return matches.stream()
                .map(match -> toDTO(match.entity(), match.score()))
                .toList();
    }

    /**
     * Méthode abstraite pour la recherche par code API (implémentation spécifique).
     */
    protected abstract Optional<E> findByApiCode(String apiCode);

    /**
     * Méthode abstraite pour la recherche par espèce (implémentation spécifique).
     */
    protected abstract List<E> findBySpeciesContaining(String query);
}

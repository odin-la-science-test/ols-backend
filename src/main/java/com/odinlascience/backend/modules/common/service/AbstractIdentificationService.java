package com.odinlascience.backend.modules.common.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.model.IdentifiableMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service abstrait générique pour la gestion d'entités identifiables.
 * Fournit les opérations CRUD standard et l'identification par critères multiples.
 *
 * @param <E> Le type de l'entité
 * @param <D> Le type du DTO
 * @param <R> Le type du repository
 */
public abstract class AbstractIdentificationService<E, D, R extends JpaRepository<E, Long>> {

    protected final R repository;

    protected AbstractIdentificationService(R repository) {
        this.repository = repository;
    }

    /**
     * Convertit une entité en DTO.
     */
    protected abstract D toDTO(E entity);

    /**
     * Convertit une entité avec son score en DTO.
     */
    protected abstract D toDTO(E entity, Integer score);

    /**
     * Retourne le nom de l'entité pour les messages d'erreur.
     */
    protected abstract String getEntityName();

    /**
     * Trouve les meilleures correspondances selon les critères fournis.
     */
    protected abstract List<IdentifiableMatch<E>> findBestMatches(D criteria, int limit);

    /**
     * Récupère toutes les entités.
     */
    public List<D> getAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Récupère une entité par son ID.
     *
     * @throws ResourceNotFoundException si l'entité n'existe pas
     */
    public D getById(Long id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " introuvable avec l'ID : " + id));
        return toDTO(entity);
    }

    /**
     * Récupère une entité par son code API.
     *
     * @throws ResourceNotFoundException si l'entité n'existe pas
     */
    public D getByApiCode(String apiCode) {
        Optional<E> entityOpt = findByApiCode(apiCode);
        E entity = entityOpt.orElseThrow(() -> new ResourceNotFoundException(
                "Aucun(e) " + getEntityName().toLowerCase() + " ne correspond au code API : " + apiCode));
        return toDTO(entity);
    }

    /**
     * Recherche des entités par nom d'espèce.
     */
    public List<D> searchBySpecies(String query) {
        List<E> entities = findBySpeciesContaining(query);
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Identifie les entités correspondant le mieux aux critères fournis.
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

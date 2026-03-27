package com.odinlascience.backend.modules.common.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.model.OwnedEntity;
import com.odinlascience.backend.modules.common.model.SoftDeletable;
import com.odinlascience.backend.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

/**
 * Service abstrait generique pour les entites owned (CRUD complet avec verification d'ownership).
 * Les sous-classes ajoutent @Service et implementent les methodes abstraites.
 *
 * @param <E> Le type de l'entite (doit implementer OwnedEntity)
 * @param <D> Le type du DTO de reponse
 * @param <C> Le type de la requete de creation
 * @param <U> Le type de la requete de mise a jour
 */
@Slf4j
public abstract class AbstractOwnedCrudService<E extends OwnedEntity, D, C, U> {

    protected final UserHelper userHelper;

    protected AbstractOwnedCrudService(UserHelper userHelper) {
        this.userHelper = userHelper;
    }

    // --- Methodes abstraites ---

    /** Construit une entite a partir de la requete de creation et du proprietaire. */
    protected abstract E toEntity(C request, User owner);

    /** Applique les champs de mise a jour partielle sur l'entite existante. */
    protected abstract void applyUpdate(E entity, U request);

    /** Convertit une entite en DTO de reponse. */
    protected abstract D toDTO(E entity);

    /** Retourne le nom de l'entite pour les messages de log et d'erreur. */
    protected abstract String getEntityName();

    /** Retourne le repository JPA de l'entite. */
    protected abstract JpaRepository<E, Long> getRepository();

    /** Retourne toutes les entites du proprietaire (avec tri). */
    protected abstract List<E> findAllByOwner(User owner);

    /** Recherche les entites du proprietaire correspondant a la requete. */
    protected abstract List<E> searchByOwner(String query, Long ownerId);

    /** Retourne toutes les entites du proprietaire avec pagination. */
    protected abstract Page<E> findAllByOwnerPaged(User owner, Pageable pageable);

    /** Recherche les entites du proprietaire avec pagination. */
    protected abstract Page<E> searchByOwnerPaged(String query, Long ownerId, Pageable pageable);

    /** Retourne la classe du DTO de reponse (pour l'export). */
    public abstract Class<D> getDtoClass();

    // --- CRUD ---

    @Transactional
    public D create(C request, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        E entity = toEntity(request, owner);
        E saved = getRepository().save(entity);
        log.info("{} created: owner={}", getEntityName(), userEmail);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<D> getMyItems(String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return findAllByOwner(owner).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public D getById(Long id, String userEmail) {
        E entity = findEntityOwnedBy(id, userEmail);
        return toDTO(entity);
    }

    @Transactional
    public D update(Long id, U request, String userEmail) {
        E entity = findEntityOwnedBy(id, userEmail);
        applyUpdate(entity, request);
        E saved = getRepository().save(entity);
        log.info("{} updated: id={}, owner={}", getEntityName(), id, userEmail);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id, String userEmail) {
        E entity = findEntityOwnedBy(id, userEmail);
        if (entity instanceof SoftDeletable softDeletable) {
            softDeletable.setDeletedAt(Instant.now());
            getRepository().save(entity);
            log.info("{} soft-deleted: id={}, owner={}", getEntityName(), id, userEmail);
        } else {
            getRepository().delete(entity);
            log.info("{} deleted: id={}, owner={}", getEntityName(), id, userEmail);
        }
    }

    @Transactional
    public D restore(Long id, String userEmail) {
        E entity = findEntityOwnedBy(id, userEmail);
        if (entity instanceof SoftDeletable softDeletable) {
            if (softDeletable.getDeletedAt() == null) {
                throw new IllegalArgumentException(getEntityName() + " avec l'ID " + id + " n'est pas supprime");
            }
            softDeletable.setDeletedAt(null);
            E saved = getRepository().save(entity);
            log.info("{} restored: id={}, owner={}", getEntityName(), id, userEmail);
            return toDTO(saved);
        }
        throw new IllegalArgumentException(getEntityName() + " ne supporte pas la restauration");
    }

    @Transactional(readOnly = true)
    public List<D> search(String query, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return searchByOwner(query, owner.getId()).stream().map(this::toDTO).toList();
    }

    // --- Batch ---

    @Transactional
    public void deleteBatch(List<Long> ids, String userEmail) {
        ids.forEach(id -> delete(id, userEmail));
        log.info("{} batch deleted: count={}, owner={}", getEntityName(), ids.size(), userEmail);
    }

    // --- Pagination ---

    @Transactional(readOnly = true)
    public Page<D> getMyItemsPaged(String userEmail, Pageable pageable) {
        User owner = userHelper.findByEmail(userEmail);
        return findAllByOwnerPaged(owner, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<D> searchPaged(String query, String userEmail, Pageable pageable) {
        User owner = userHelper.findByEmail(userEmail);
        return searchByOwnerPaged(query, owner.getId(), pageable).map(this::toDTO);
    }

    // --- Helper ---

    /**
     * Trouve une entite par ID et verifie qu'elle appartient a l'utilisateur.
     * Accessible aux sous-classes pour des operations custom (toggle favori, etc.).
     */
    protected E findEntityOwnedBy(Long id, String userEmail) {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " introuvable avec l'ID : " + id));
        return userHelper.verifyOwnership(entity, userEmail, getEntityName(), id);
    }
}

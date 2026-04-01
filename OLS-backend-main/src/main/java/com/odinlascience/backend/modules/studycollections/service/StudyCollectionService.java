package com.odinlascience.backend.modules.studycollections.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.service.AbstractOwnedCrudService;
import com.odinlascience.backend.modules.common.service.HtmlSanitizer;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.studycollections.dto.*;
import com.odinlascience.backend.modules.studycollections.mapper.StudyCollectionMapper;
import com.odinlascience.backend.modules.studycollections.model.StudyCollection;
import com.odinlascience.backend.modules.studycollections.model.StudyCollectionItem;
import com.odinlascience.backend.modules.studycollections.repository.StudyCollectionRepository;
import com.odinlascience.backend.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service principal du module Study Collections.
 * Gere le CRUD des collections et l'ajout/suppression d'elements.
 */
@Slf4j
@Service
public class StudyCollectionService extends AbstractOwnedCrudService<StudyCollection, StudyCollectionDTO, CreateStudyCollectionRequest, UpdateStudyCollectionRequest> {

    private final StudyCollectionRepository repository;
    private final StudyCollectionMapper mapper;

    public StudyCollectionService(UserHelper userHelper, ApplicationEventPublisher eventPublisher,
                                  StudyCollectionRepository repository, StudyCollectionMapper mapper) {
        super(userHelper, eventPublisher);
        this.repository = repository;
        this.mapper = mapper;
    }

    // ─── Methodes abstraites ───

    @Override
    protected StudyCollection toEntity(CreateStudyCollectionRequest request, User owner) {
        return StudyCollection.builder()
                .name(request.getName())
                .description(HtmlSanitizer.sanitize(request.getDescription()))
                .owner(owner)
                .build();
    }

    @Override
    protected void applyUpdate(StudyCollection entity, UpdateStudyCollectionRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(HtmlSanitizer.sanitize(request.getDescription()));
        }
    }

    @Override
    protected StudyCollectionDTO toDTO(StudyCollection entity) {
        return mapper.toDTO(entity);
    }

    @Override
    public Class<StudyCollectionDTO> getDtoClass() {
        return StudyCollectionDTO.class;
    }

    @Override
    protected String getEntityName() {
        return "StudyCollection";
    }

    @Override
    protected String getModuleSlug() {
        return "study-collections";
    }

    @Override
    protected JpaRepository<StudyCollection, Long> getRepository() {
        return repository;
    }

    @Override
    protected List<StudyCollection> findAllByOwner(User owner) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(owner.getId());
    }

    @Override
    protected List<StudyCollection> searchByOwner(String query, Long ownerId) {
        return repository.searchByOwner(ownerId, query);
    }

    @Override
    protected Page<StudyCollection> findAllByOwnerPaged(User owner, Pageable pageable) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(owner.getId(), pageable);
    }

    @Override
    protected Page<StudyCollection> searchByOwnerPaged(String query, Long ownerId, Pageable pageable) {
        return repository.searchByOwnerPaged(ownerId, query, pageable);
    }

    // ─── Ajouter un element a une collection ───

    @Transactional
    public StudyCollectionDTO addItem(Long collectionId, AddItemRequest request, String userEmail) {
        StudyCollection collection = findEntityOwnedBy(collectionId, userEmail);

        StudyCollectionItem item = StudyCollectionItem.builder()
                .collection(collection)
                .moduleId(request.getModuleId())
                .entityId(request.getEntityId())
                .notes(HtmlSanitizer.sanitize(request.getNotes()))
                .addedAt(Instant.now())
                .build();

        collection.getItems().add(item);

        StudyCollection saved = repository.save(collection);

        log.info("Item added to StudyCollection: collectionId={}, moduleId={}, entityId={}, owner={}",
                collectionId, request.getModuleId(), request.getEntityId(), userEmail);
        return mapper.toDTO(saved);
    }

    // ─── Supprimer un element d'une collection ───

    @Transactional
    public void removeItem(Long collectionId, Long itemId, String userEmail) {
        StudyCollection collection = findEntityOwnedBy(collectionId, userEmail);

        boolean removed = collection.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new ResourceNotFoundException("Element introuvable avec l'ID : " + itemId);
        }

        repository.save(collection);
        log.info("Item removed from StudyCollection: collectionId={}, itemId={}, owner={}",
                collectionId, itemId, userEmail);
    }
}

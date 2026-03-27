package com.odinlascience.backend.modules.studycollections.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.studycollections.dto.*;
import com.odinlascience.backend.modules.studycollections.mapper.StudyCollectionMapper;
import com.odinlascience.backend.modules.studycollections.model.StudyCollection;
import com.odinlascience.backend.modules.studycollections.model.StudyCollectionItem;
import com.odinlascience.backend.modules.studycollections.repository.StudyCollectionRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class StudyCollectionService {

    private final StudyCollectionRepository repository;
    private final StudyCollectionMapper mapper;
    private final UserHelper userHelper;

    // ─── Creer une collection ───

    @Transactional
    public StudyCollectionDTO create(CreateStudyCollectionRequest request, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);

        StudyCollection collection = StudyCollection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        StudyCollection saved = repository.save(collection);
        log.info("StudyCollection created: id={}, name='{}', owner={}", saved.getId(), saved.getName(), userEmail);
        return mapper.toDTO(saved);
    }

    // ─── Lister mes collections ───

    @Transactional(readOnly = true)
    public List<StudyCollectionDTO> getMyCollections(String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.findByOwnerIdOrderByCreatedAtDesc(owner.getId()).stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── Detail d'une collection par ID ───

    @Transactional(readOnly = true)
    public StudyCollectionDTO getById(Long id, String userEmail) {
        StudyCollection collection = findCollectionOwnedBy(id, userEmail);
        return mapper.toDTO(collection);
    }

    // ─── Mettre a jour une collection ───

    @Transactional
    public StudyCollectionDTO update(Long id, UpdateStudyCollectionRequest request, String userEmail) {
        StudyCollection collection = findCollectionOwnedBy(id, userEmail);

        if (request.getName() != null) {
            collection.setName(request.getName());
        }
        if (request.getDescription() != null) {
            collection.setDescription(request.getDescription());
        }


        StudyCollection saved = repository.save(collection);
        log.info("StudyCollection updated: id={}, owner={}", saved.getId(), userEmail);
        return mapper.toDTO(saved);
    }

    // ─── Supprimer une collection ───

    @Transactional
    public void delete(Long id, String userEmail) {
        StudyCollection collection = findCollectionOwnedBy(id, userEmail);
        repository.delete(collection);
        log.info("StudyCollection deleted: id={}, owner={}", id, userEmail);
    }

    // ─── Recherche ───

    @Transactional(readOnly = true)
    public List<StudyCollectionDTO> search(String query, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.searchByOwner(owner.getId(), query).stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── Ajouter un element a une collection ───

    @Transactional
    public StudyCollectionDTO addItem(Long collectionId, AddItemRequest request, String userEmail) {
        StudyCollection collection = findCollectionOwnedBy(collectionId, userEmail);

        StudyCollectionItem item = StudyCollectionItem.builder()
                .collection(collection)
                .moduleId(request.getModuleId())
                .entityId(request.getEntityId())
                .notes(request.getNotes())
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
        StudyCollection collection = findCollectionOwnedBy(collectionId, userEmail);

        boolean removed = collection.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new ResourceNotFoundException("Element introuvable avec l'ID : " + itemId);
        }


        repository.save(collection);
        log.info("Item removed from StudyCollection: collectionId={}, itemId={}, owner={}",
                collectionId, itemId, userEmail);
    }

    // ─── Helpers ───

    private StudyCollection findCollectionOwnedBy(Long collectionId, String userEmail) {
        StudyCollection collection = repository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Collection introuvable avec l'ID : " + collectionId));
        return userHelper.verifyOwnership(collection, userEmail, "StudyCollection", collectionId);
    }
}

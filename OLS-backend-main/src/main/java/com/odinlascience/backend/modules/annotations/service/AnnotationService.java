package com.odinlascience.backend.modules.annotations.service;

import com.odinlascience.backend.modules.annotations.dto.AnnotationDTO;
import com.odinlascience.backend.modules.annotations.dto.CreateAnnotationRequest;
import com.odinlascience.backend.modules.annotations.dto.UpdateAnnotationRequest;
import com.odinlascience.backend.modules.annotations.enums.AnnotationColor;
import com.odinlascience.backend.modules.annotations.mapper.AnnotationMapper;
import com.odinlascience.backend.modules.annotations.model.Annotation;
import com.odinlascience.backend.modules.annotations.repository.AnnotationRepository;
import com.odinlascience.backend.modules.common.service.AbstractOwnedCrudService;
import com.odinlascience.backend.modules.common.service.HtmlSanitizer;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Service principal du module Annotations.
 * Gere le CRUD complet des annotations personnelles sur les entites.
 */
@Slf4j
@Service
public class AnnotationService
        extends AbstractOwnedCrudService<Annotation, AnnotationDTO, CreateAnnotationRequest, UpdateAnnotationRequest> {

    private final AnnotationRepository repository;
    private final AnnotationMapper mapper;

    public AnnotationService(UserHelper userHelper, ApplicationEventPublisher eventPublisher,
                             AnnotationRepository repository, AnnotationMapper mapper) {
        super(userHelper, eventPublisher);
        this.repository = repository;
        this.mapper = mapper;
    }

    // ─── Methodes abstraites implementees ───

    @Override
    protected Annotation toEntity(CreateAnnotationRequest request, User owner) {
        return Annotation.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .content(HtmlSanitizer.sanitize(request.getContent()))
                .color(request.getColor() != null ? request.getColor() : AnnotationColor.YELLOW)
                .owner(owner)
                .build();
    }

    @Override
    protected void applyUpdate(Annotation entity, UpdateAnnotationRequest request) {
        if (request.getContent() != null) entity.setContent(HtmlSanitizer.sanitize(request.getContent()));
        if (request.getColor() != null) entity.setColor(request.getColor());
    }

    @Override
    protected AnnotationDTO toDTO(Annotation entity) {
        return mapper.toDTO(entity);
    }

    @Override
    public Class<AnnotationDTO> getDtoClass() {
        return AnnotationDTO.class;
    }

    @Override
    protected String getEntityName() {
        return "Annotation";
    }

    @Override
    protected String getModuleSlug() {
        return "annotations";
    }

    @Override
    protected JpaRepository<Annotation, Long> getRepository() {
        return repository;
    }

    @Override
    protected List<Annotation> findAllByOwner(User owner) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(owner.getId());
    }

    @Override
    protected List<Annotation> searchByOwner(String query, Long ownerId) {
        return repository.searchByOwner(ownerId, query);
    }

    @Override
    protected Page<Annotation> findAllByOwnerPaged(User owner, Pageable pageable) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(owner.getId(), pageable);
    }

    @Override
    protected Page<Annotation> searchByOwnerPaged(String query, Long ownerId, Pageable pageable) {
        return repository.searchByOwnerPaged(ownerId, query, pageable);
    }

    // ─── Methodes custom ───

    /**
     * Retourne les annotations d'un utilisateur pour une entite specifique.
     */
    @Transactional(readOnly = true)
    public List<AnnotationDTO> getByEntity(String entityType, Long entityId, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.findByOwnerIdAndEntityTypeAndEntityIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                owner.getId(), entityType, entityId)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}

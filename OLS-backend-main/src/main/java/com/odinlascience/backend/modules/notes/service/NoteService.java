package com.odinlascience.backend.modules.notes.service;

import com.odinlascience.backend.modules.common.service.AbstractOwnedCrudService;
import com.odinlascience.backend.modules.common.service.HtmlSanitizer;
import com.odinlascience.backend.modules.common.service.UserHelper;
import org.springframework.context.ApplicationEventPublisher;
import com.odinlascience.backend.modules.notes.dto.CreateNoteRequest;
import com.odinlascience.backend.modules.notes.dto.NoteDTO;
import com.odinlascience.backend.modules.notes.dto.UpdateNoteRequest;
import com.odinlascience.backend.modules.notes.mapper.NoteMapper;
import com.odinlascience.backend.modules.notes.model.Note;
import com.odinlascience.backend.modules.notes.repository.NoteRepository;
import com.odinlascience.backend.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service principal du module Notes.
 * Gère le CRUD complet des notes de cahier de laboratoire.
 */
@Slf4j
@Service
public class NoteService extends AbstractOwnedCrudService<Note, NoteDTO, CreateNoteRequest, UpdateNoteRequest> {

    private final NoteRepository repository;
    private final NoteMapper mapper;

    public NoteService(UserHelper userHelper, ApplicationEventPublisher eventPublisher,
                       NoteRepository repository, NoteMapper mapper) {
        super(userHelper, eventPublisher);
        this.repository = repository;
        this.mapper = mapper;
    }

    // ─── Méthodes abstraites ───

    @Override
    protected Note toEntity(CreateNoteRequest request, User owner) {
        return Note.builder()
                .title(request.getTitle())
                .content(HtmlSanitizer.sanitize(request.getContent()))
                .color(request.getColor())
                .pinned(request.getPinned() != null ? request.getPinned() : false)
                .tags(NoteMapper.tagsToString(request.getTags()))
                .owner(owner)
                .build();
    }

    @Override
    protected void applyUpdate(Note note, UpdateNoteRequest request) {
        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(HtmlSanitizer.sanitize(request.getContent()));
        }
        if (request.getColor() != null) {
            note.setColor(request.getColor());
        }
        if (request.getPinned() != null) {
            note.setPinned(request.getPinned());
        }
        if (request.getTags() != null) {
            note.setTags(NoteMapper.tagsToString(request.getTags()));
        }
    }

    @Override
    protected NoteDTO toDTO(Note entity) {
        return mapper.toDTO(entity);
    }

    @Override
    public Class<NoteDTO> getDtoClass() {
        return NoteDTO.class;
    }

    @Override
    protected String getEntityName() {
        return "Note";
    }

    @Override
    protected String getModuleSlug() {
        return "notes";
    }

    @Override
    protected JpaRepository<Note, Long> getRepository() {
        return repository;
    }

    @Override
    protected List<Note> findAllByOwner(User owner) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(owner.getId());
    }

    @Override
    protected List<Note> searchByOwner(String query, Long ownerId) {
        return repository.searchByOwner(ownerId, query);
    }

    @Override
    protected Page<Note> findAllByOwnerPaged(User owner, Pageable pageable) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(owner.getId(), pageable);
    }

    @Override
    protected Page<Note> searchByOwnerPaged(String query, Long ownerId, Pageable pageable) {
        return repository.searchByOwnerPaged(ownerId, query, pageable);
    }

    // ─── Toggle pin ───

    @Transactional
    public NoteDTO togglePin(Long id, String userEmail) {
        Note note = findEntityOwnedBy(id, userEmail);
        String previousJson = toJson(mapper.toDTO(note));
        note.setPinned(!note.getPinned());
        Note saved = repository.save(note);
        log.info("Note pin toggled: id={}, pinned={}, owner={}", saved.getId(), saved.getPinned(), userEmail);
        NoteDTO dto = mapper.toDTO(saved);
        publishAction("UPDATE", id, previousJson, toJson(dto), "update", "pin", userEmail);
        return dto;
    }

    // ─── Recherche par tag ───

    @Transactional(readOnly = true)
    public List<NoteDTO> searchByTag(String tag, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.searchByTag(owner.getId(), tag)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}

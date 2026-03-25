package com.odinlascience.backend.modules.notes.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.notes.dto.CreateNoteRequest;
import com.odinlascience.backend.modules.notes.dto.NoteDTO;
import com.odinlascience.backend.modules.notes.dto.UpdateNoteRequest;
import com.odinlascience.backend.modules.notes.mapper.NoteMapper;
import com.odinlascience.backend.modules.notes.model.Note;
import com.odinlascience.backend.modules.notes.repository.NoteRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service principal du module Notes.
 * Gère le CRUD complet des notes de cahier de laboratoire.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository repository;
    private final NoteMapper mapper;
    private final UserHelper userHelper;

    // ─── Créer une note ───

    @Transactional
    public NoteDTO create(CreateNoteRequest request, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);

        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .color(request.getColor())
                .pinned(request.getPinned() != null ? request.getPinned() : false)
                .tags(NoteMapper.tagsToString(request.getTags()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .owner(owner)
                .build();

        Note saved = repository.save(note);
        log.info("Note created: id={}, title='{}', owner={}", saved.getId(), saved.getTitle(), userEmail);
        return mapper.toDTO(saved);
    }

    // ─── Lister mes notes ───

    @Transactional(readOnly = true)
    public List<NoteDTO> getMyNotes(String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.findByOwnerIdOrderByPinnedDescUpdatedAtDesc(owner.getId())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── Détail d'une note par ID ───

    @Transactional(readOnly = true)
    public NoteDTO getById(Long id, String userEmail) {
        Note note = findNoteOwnedBy(id, userEmail);
        return mapper.toDTO(note);
    }

    // ─── Mettre à jour une note ───

    @Transactional
    public NoteDTO update(Long id, UpdateNoteRequest request, String userEmail) {
        Note note = findNoteOwnedBy(id, userEmail);

        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
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

        note.setUpdatedAt(Instant.now());
        Note saved = repository.save(note);
        log.info("Note updated: id={}, owner={}", saved.getId(), userEmail);
        return mapper.toDTO(saved);
    }

    // ─── Supprimer une note ───

    @Transactional
    public void delete(Long id, String userEmail) {
        Note note = findNoteOwnedBy(id, userEmail);
        repository.delete(note);
        log.info("Note deleted: id={}, owner={}", id, userEmail);
    }

    // ─── Toggle pin ───

    @Transactional
    public NoteDTO togglePin(Long id, String userEmail) {
        Note note = findNoteOwnedBy(id, userEmail);
        note.setPinned(!note.getPinned());
        note.setUpdatedAt(Instant.now());
        Note saved = repository.save(note);
        log.info("Note pin toggled: id={}, pinned={}, owner={}", saved.getId(), saved.getPinned(), userEmail);
        return mapper.toDTO(saved);
    }

    // ─── Recherche ───

    @Transactional(readOnly = true)
    public List<NoteDTO> search(String query, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.searchByOwner(owner.getId(), query)
                .stream()
                .map(mapper::toDTO)
                .toList();
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

    // ─── Helpers ───

    private Note findNoteOwnedBy(Long noteId, String userEmail) {
        Note note = repository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note introuvable avec l'ID : " + noteId));
        return userHelper.verifyOwnership(note, userEmail, "Note", noteId);
    }
}

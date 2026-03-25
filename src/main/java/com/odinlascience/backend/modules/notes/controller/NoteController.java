package com.odinlascience.backend.modules.notes.controller;

import com.odinlascience.backend.modules.notes.dto.CreateNoteRequest;
import com.odinlascience.backend.modules.notes.dto.NoteDTO;
import com.odinlascience.backend.modules.notes.dto.UpdateNoteRequest;
import com.odinlascience.backend.modules.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "Cahier de laboratoire et prise de notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService service;

    // ─── Créer une note ───

    @PostMapping
    @Operation(summary = "Créer une note",
               description = "Crée une nouvelle note dans le cahier de laboratoire")
    public ResponseEntity<NoteDTO> create(
            @Valid @RequestBody CreateNoteRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.create(request, auth.getName()));
    }

    // ─── Lister mes notes ───

    @GetMapping
    @Operation(summary = "Lister mes notes",
               description = "Retourne toutes les notes de l'utilisateur connecté (épinglées en premier)")
    public ResponseEntity<List<NoteDTO>> getMyNotes(Authentication auth) {
        return ResponseEntity.ok(service.getMyNotes(auth.getName()));
    }

    // ─── Détail d'une note ───

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une note",
               description = "Récupère les détails d'une note par son ID")
    public ResponseEntity<NoteDTO> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.getById(id, auth.getName()));
    }

    // ─── Mettre à jour une note ───

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une note",
               description = "Met à jour les champs fournis d'une note existante")
    public ResponseEntity<NoteDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoteRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.update(id, request, auth.getName()));
    }

    // ─── Supprimer une note ───

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une note",
               description = "Supprime une note du cahier de laboratoire")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    // ─── Toggle pin ───

    @PatchMapping("/{id}/pin")
    @Operation(summary = "Épingler / Désépingler une note",
               description = "Bascule l'état épinglé d'une note")
    public ResponseEntity<NoteDTO> togglePin(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.togglePin(id, auth.getName()));
    }

    // ─── Recherche ───

    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes notes",
               description = "Recherche par titre, contenu ou tags dans les notes de l'utilisateur")
    public ResponseEntity<List<NoteDTO>> search(
            @RequestParam("query") String query,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.search(query, auth.getName()));
    }

    // ─── Recherche par tag ───

    @GetMapping("/search/tags")
    @Operation(summary = "Rechercher par tag",
               description = "Recherche les notes contenant un tag spécifique (prefix match)")
    public ResponseEntity<List<NoteDTO>> searchByTag(
            @RequestParam("query") String tag,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.searchByTag(tag, auth.getName()));
    }
}

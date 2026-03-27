package com.odinlascience.backend.modules.notes.controller;

import com.odinlascience.backend.modules.common.controller.AbstractOwnedCrudController;
import com.odinlascience.backend.modules.notes.dto.CreateNoteRequest;
import com.odinlascience.backend.modules.notes.dto.NoteDTO;
import com.odinlascience.backend.modules.notes.dto.UpdateNoteRequest;
import com.odinlascience.backend.modules.notes.service.NoteService;
import com.odinlascience.backend.modules.common.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "Cahier de laboratoire et prise de notes")
public class NoteController extends AbstractOwnedCrudController<NoteDTO, CreateNoteRequest, UpdateNoteRequest, NoteService> {

    public NoteController(NoteService service, ExportService exportService) {
        super(service, exportService);
    }

    // ─── Swagger overrides pour les endpoints hérités ───

    @Override
    @PostMapping
    @Operation(summary = "Créer une note",
               description = "Crée une nouvelle note dans le cahier de laboratoire")
    public ResponseEntity<NoteDTO> create(@Valid @RequestBody CreateNoteRequest request, Authentication auth) {
        return super.create(request, auth);
    }

    @Override
    @GetMapping
    @Operation(summary = "Lister mes notes",
               description = "Retourne toutes les notes de l'utilisateur connecté (épinglées en premier)")
    public ResponseEntity<List<NoteDTO>> getMyItems(Authentication auth) {
        return super.getMyItems(auth);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une note",
               description = "Récupère les détails d'une note par son ID")
    public ResponseEntity<NoteDTO> getById(@PathVariable Long id, Authentication auth) {
        return super.getById(id, auth);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une note",
               description = "Met à jour les champs fournis d'une note existante")
    public ResponseEntity<NoteDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoteRequest request,
            Authentication auth
    ) {
        return super.update(id, request, auth);
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une note",
               description = "Supprime une note du cahier de laboratoire")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        return super.delete(id, auth);
    }

    @Override
    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes notes",
               description = "Recherche par titre, contenu ou tags dans les notes de l'utilisateur")
    public ResponseEntity<List<NoteDTO>> search(@RequestParam("query") String query, Authentication auth) {
        return super.search(query, auth);
    }

    // ─── Restaurer ───

    @Override
    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurer une note supprimée",
               description = "Annule la suppression (soft delete) d'une note")
    public ResponseEntity<NoteDTO> restore(@PathVariable Long id, Authentication auth) {
        return super.restore(id, auth);
    }

    // ─── Endpoints custom ───

    @PatchMapping("/{id}/pin")
    @Operation(summary = "Épingler / Désépingler une note",
               description = "Bascule l'état épinglé d'une note")
    public ResponseEntity<NoteDTO> togglePin(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.togglePin(id, auth.getName()));
    }

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

package com.odinlascience.backend.modules.studycollections.controller;

import com.odinlascience.backend.modules.studycollections.dto.*;
import com.odinlascience.backend.modules.studycollections.service.StudyCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour les collections d'etude.
 * Permet de gerer des playlists d'entites cross-modules.
 */
@RestController
@RequestMapping("/api/study-collections")
@Tag(name = "Study Collections", description = "Playlists d'etude cross-modules")
@RequiredArgsConstructor
@Validated
public class StudyCollectionController {

    private final StudyCollectionService service;

    // ─── Creer une collection ───

    @PostMapping
    @Operation(summary = "Creer une collection d'etude",
               description = "Cree une nouvelle collection d'etude pour l'utilisateur connecte")
    public ResponseEntity<StudyCollectionDTO> create(
            @Valid @RequestBody CreateStudyCollectionRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.create(request, auth.getName()));
    }

    // ─── Lister mes collections ───

    @GetMapping
    @Operation(summary = "Lister mes collections",
               description = "Retourne toutes les collections de l'utilisateur connecte")
    public ResponseEntity<List<StudyCollectionDTO>> getMyCollections(Authentication auth) {
        return ResponseEntity.ok(service.getMyItems(auth.getName()));
    }

    // ─── Detail d'une collection ───

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une collection",
               description = "Recupere les details d'une collection par son ID, avec ses elements")
    public ResponseEntity<StudyCollectionDTO> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.getById(id, auth.getName()));
    }

    // ─── Mettre a jour une collection ───

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour une collection",
               description = "Met a jour le nom et/ou la description d'une collection")
    public ResponseEntity<StudyCollectionDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudyCollectionRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.update(id, request, auth.getName()));
    }

    // ─── Supprimer une collection ───

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une collection",
               description = "Supprime une collection et tous ses elements")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    // ─── Recherche ───

    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes collections",
               description = "Recherche par nom ou description dans les collections de l'utilisateur")
    public ResponseEntity<List<StudyCollectionDTO>> search(
            @RequestParam("query") @NotBlank @Size(max = 200) String query,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.search(query, auth.getName()));
    }

    // ─── Ajouter un element ───

    @PostMapping("/{id}/items")
    @Operation(summary = "Ajouter un element a une collection",
               description = "Ajoute une entite d'un module (ex: bacteriology) a la collection")
    public ResponseEntity<StudyCollectionDTO> addItem(
            @PathVariable Long id,
            @Valid @RequestBody AddItemRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.addItem(id, request, auth.getName()));
    }

    // ─── Supprimer un element ───

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Supprimer un element d'une collection",
               description = "Retire un element de la collection")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            Authentication auth
    ) {
        service.removeItem(id, itemId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

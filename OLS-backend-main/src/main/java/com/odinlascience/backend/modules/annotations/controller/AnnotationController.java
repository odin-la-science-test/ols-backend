package com.odinlascience.backend.modules.annotations.controller;

import com.odinlascience.backend.modules.annotations.dto.AnnotationDTO;
import com.odinlascience.backend.modules.annotations.dto.CreateAnnotationRequest;
import com.odinlascience.backend.modules.annotations.dto.UpdateAnnotationRequest;
import com.odinlascience.backend.modules.annotations.service.AnnotationService;
import com.odinlascience.backend.modules.common.controller.AbstractOwnedCrudController;
import com.odinlascience.backend.modules.common.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour le module Annotations.
 * Fournit le CRUD standard (via AbstractOwnedCrudController) + endpoint par entite.
 */
@RestController
@RequestMapping("/api/annotations")
@Tag(name = "Annotations", description = "Notes personnelles sur les entites (bacteries, champignons, contacts, etc.)")
@Validated
public class AnnotationController
        extends AbstractOwnedCrudController<AnnotationDTO, CreateAnnotationRequest, UpdateAnnotationRequest, AnnotationService> {

    public AnnotationController(AnnotationService service, ExportService exportService) {
        super(service, exportService);
    }

    // ─── Swagger overrides ───

    @Override
    @PostMapping
    @Operation(summary = "Creer une annotation",
               description = "Ajoute une annotation personnelle sur une entite")
    public ResponseEntity<AnnotationDTO> create(@RequestBody CreateAnnotationRequest request, Authentication auth) {
        return super.create(request, auth);
    }

    @Override
    @GetMapping
    @Operation(summary = "Lister mes annotations",
               description = "Retourne toutes les annotations de l'utilisateur connecte")
    public ResponseEntity<List<AnnotationDTO>> getMyItems(Authentication auth) {
        return super.getMyItems(auth);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une annotation",
               description = "Recupere les details d'une annotation par son ID")
    public ResponseEntity<AnnotationDTO> getById(@PathVariable Long id, Authentication auth) {
        return super.getById(id, auth);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour une annotation",
               description = "Met a jour le contenu ou la couleur d'une annotation existante")
    public ResponseEntity<AnnotationDTO> update(@PathVariable Long id,
                                                @RequestBody UpdateAnnotationRequest request,
                                                Authentication auth) {
        return super.update(id, request, auth);
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une annotation",
               description = "Supprime une annotation")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        return super.delete(id, auth);
    }

    @Override
    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes annotations",
               description = "Recherche par contenu ou type d'entite dans les annotations de l'utilisateur")
    public ResponseEntity<List<AnnotationDTO>> search(@RequestParam("query") @NotBlank @Size(max = 200) String query, Authentication auth) {
        return super.search(query, auth);
    }

    // ─── Endpoint custom : annotations par entite ───

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Annotations d'une entite",
               description = "Retourne les annotations de l'utilisateur pour une entite specifique")
    public ResponseEntity<List<AnnotationDTO>> getByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            Authentication auth) {
        return ResponseEntity.ok(service.getByEntity(entityType, entityId, auth.getName()));
    }
}

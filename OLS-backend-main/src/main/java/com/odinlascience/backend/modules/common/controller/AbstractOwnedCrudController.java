package com.odinlascience.backend.modules.common.controller;

import com.odinlascience.backend.modules.common.dto.BatchDeleteRequest;
import com.odinlascience.backend.modules.common.service.AbstractOwnedCrudService;
import com.odinlascience.backend.modules.common.service.ExportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controleur abstrait generique fournissant les endpoints standards
 * pour les entites owned (CRUD complet avec ownership).
 * Les sous-classes ajoutent @RestController, @RequestMapping, @Tag
 * et peuvent override les methodes pour ajouter @Operation.
 *
 * @param <D> Le type du DTO de reponse
 * @param <C> Le type de la requete de creation
 * @param <U> Le type de la requete de mise a jour
 * @param <S> Le type du service
 */
public abstract class AbstractOwnedCrudController<D, C, U, S extends AbstractOwnedCrudService<?, D, C, U>> {

    protected final S service;
    protected final ExportService exportService;

    protected AbstractOwnedCrudController(S service, ExportService exportService) {
        this.service = service;
        this.exportService = exportService;
    }

    // --- Creer ---

    @PostMapping
    public ResponseEntity<D> create(@Valid @RequestBody C request, Authentication auth) {
        return ResponseEntity.ok(service.create(request, auth.getName()));
    }

    // --- Lister ---

    @GetMapping
    public ResponseEntity<List<D>> getMyItems(Authentication auth) {
        return ResponseEntity.ok(service.getMyItems(auth.getName()));
    }

    // --- Detail par ID ---

    @GetMapping("/{id}")
    public ResponseEntity<D> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.getById(id, auth.getName()));
    }

    // --- Mettre a jour ---

    @PutMapping("/{id}")
    public ResponseEntity<D> update(
            @PathVariable Long id,
            @Valid @RequestBody U request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.update(id, request, auth.getName()));
    }

    // --- Supprimer ---

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    // --- Restaurer (soft-delete undo) ---

    @PatchMapping("/{id}/restore")
    public ResponseEntity<D> restore(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.restore(id, auth.getName()));
    }

    // --- Rechercher ---

    @GetMapping("/search")
    public ResponseEntity<List<D>> search(
            @RequestParam("query") @NotBlank @Size(max = 200) String query,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.search(query, auth.getName()));
    }

    // --- Suppression en lot ---

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteBatch(
            @Valid @RequestBody BatchDeleteRequest request,
            Authentication auth
    ) {
        service.deleteBatch(request.getIds(), auth.getName());
        return ResponseEntity.noContent().build();
    }

    // --- Pagination ---

    @GetMapping("/paged")
    public ResponseEntity<Page<D>> getMyItemsPaged(Pageable pageable, Authentication auth) {
        return ResponseEntity.ok(service.getMyItemsPaged(auth.getName(), pageable));
    }

    @GetMapping("/search/paged")
    public ResponseEntity<Page<D>> searchPaged(
            @RequestParam("query") @NotBlank @Size(max = 200) String query,
            Pageable pageable,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.searchPaged(query, auth.getName(), pageable));
    }

    // --- Export ---

    /**
     * Exporte toutes les entites de l'utilisateur en CSV ou JSON.
     * GET /export?format=csv|json
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "csv") String format,
            Authentication auth
    ) {
        List<D> items = service.getMyItems(auth.getName());
        boolean json = "json".equalsIgnoreCase(format);

        byte[] data = json
                ? exportService.exportToJson(items)
                : exportService.exportToCsv(items, service.getDtoClass());

        String contentType = json ? MediaType.APPLICATION_JSON_VALUE : "text/csv";
        String extension = json ? "json" : "csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"export." + extension + "\"")
                .body(data);
    }
}

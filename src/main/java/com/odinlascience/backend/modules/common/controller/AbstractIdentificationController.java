package com.odinlascience.backend.modules.common.controller;

import com.odinlascience.backend.modules.common.service.AbstractIdentificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur abstrait générique fournissant les endpoints standards
 * pour les entités identifiables.
 *
 * @param <D> Le type du DTO
 * @param <S> Le type du service
 */
public abstract class AbstractIdentificationController<D, S extends AbstractIdentificationService<?, D, ?>> {

    protected final S service;

    protected AbstractIdentificationController(S service) {
        this.service = service;
    }

    /**
     * Liste toutes les entités.
     * GET /{basePath}
     */
    @GetMapping
    public ResponseEntity<List<D>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Récupère une entité par son ID.
     * GET /{basePath}/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<D> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Recherche des entités par nom d'espèce.
     * GET /{basePath}/search?query=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<D>> searchBySpecies(@RequestParam String query) {
        return ResponseEntity.ok(service.searchBySpecies(query));
    }

    /**
     * Identifie une entité par son code API.
     * GET /{basePath}/identify/api/{code}
     */
    @GetMapping("/identify/api/{code}")
    public ResponseEntity<D> identifyByApiCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByApiCode(code));
    }

    /**
     * Identifie les entités correspondant le mieux aux critères fournis.
     * POST /{basePath}/identify
     */
    @PostMapping("/identify")
    public ResponseEntity<List<D>> identifyByCriteria(@RequestBody D criteria) {
        return ResponseEntity.ok(service.identifyByCriteria(criteria));
    }
}

package com.odinlascience.backend.modules.history.controller;

import com.odinlascience.backend.modules.history.dto.HistoryEntryDTO;
import com.odinlascience.backend.modules.history.service.HistoryEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@Tag(name = "History", description = "Historique persistant undo/redo")
@RequiredArgsConstructor
public class HistoryEntryController {

    private final HistoryEntryService service;

    @GetMapping
    @Operation(summary = "Lister les entries d'un module")
    public ResponseEntity<List<HistoryEntryDTO>> getByModule(
            @RequestParam("module") String moduleSlug,
            Authentication auth) {
        return ResponseEntity.ok(service.getByModule(moduleSlug, auth.getName()));
    }

    @DeleteMapping
    @Operation(summary = "Supprimer tout l'historique d'un module")
    public ResponseEntity<Void> clearModule(
            @RequestParam("module") String moduleSlug,
            Authentication auth) {
        service.clearModule(moduleSlug, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/truncate-after/{id}")
    @Operation(summary = "Couper le redo stack apres une entree")
    public ResponseEntity<Void> truncateAfter(
            @PathVariable Long id,
            @RequestParam("module") String moduleSlug,
            Authentication auth) {
        service.truncateAfter(id, moduleSlug, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

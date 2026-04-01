package com.odinlascience.backend.modules.organization.controller;

import com.odinlascience.backend.modules.organization.dto.CreateSupervisionRequest;
import com.odinlascience.backend.modules.organization.dto.SupervisionDTO;
import com.odinlascience.backend.modules.organization.service.SupervisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{orgId}/supervisions")
@Tag(name = "Supervision", description = "Relations de supervision au sein d'une organisation")
@RequiredArgsConstructor
public class SupervisionController {

    private final SupervisionService service;

    @PostMapping
    @Operation(summary = "Creer une supervision", description = "Assigne un superviseur a un supervise (OWNER ou MANAGER requis)")
    public ResponseEntity<SupervisionDTO> create(
            @PathVariable Long orgId,
            @Valid @RequestBody CreateSupervisionRequest request,
            Authentication auth) {
        return ResponseEntity.ok(service.create(orgId, request, auth.getName()));
    }

    @GetMapping
    @Operation(summary = "Lister les supervisions", description = "Liste toutes les relations de supervision de l'organisation")
    public ResponseEntity<List<SupervisionDTO>> getByOrganization(@PathVariable Long orgId, Authentication auth) {
        return ResponseEntity.ok(service.getByOrganization(orgId, auth.getName()));
    }

    @GetMapping("/my-supervisees")
    @Operation(summary = "Mes supervises", description = "Liste les personnes que je supervise dans cette organisation")
    public ResponseEntity<List<SupervisionDTO>> getMySupervisees(@PathVariable Long orgId, Authentication auth) {
        return ResponseEntity.ok(service.getMySupervisees(orgId, auth.getName()));
    }

    @GetMapping("/my-supervisors")
    @Operation(summary = "Mes superviseurs", description = "Liste mes superviseurs dans cette organisation")
    public ResponseEntity<List<SupervisionDTO>> getMySupervisors(@PathVariable Long orgId, Authentication auth) {
        return ResponseEntity.ok(service.getMySupervisors(orgId, auth.getName()));
    }

    @DeleteMapping("/{supervisionId}")
    @Operation(summary = "Supprimer une supervision", description = "Supprime une relation de supervision (OWNER ou MANAGER requis)")
    public ResponseEntity<Void> delete(
            @PathVariable Long orgId,
            @PathVariable Long supervisionId,
            Authentication auth) {
        service.delete(orgId, supervisionId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

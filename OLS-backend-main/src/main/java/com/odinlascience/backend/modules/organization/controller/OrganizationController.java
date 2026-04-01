package com.odinlascience.backend.modules.organization.controller;

import com.odinlascience.backend.modules.organization.dto.CreateOrganizationRequest;
import com.odinlascience.backend.modules.organization.dto.OrganizationDTO;
import com.odinlascience.backend.modules.organization.dto.UpdateOrganizationRequest;
import com.odinlascience.backend.modules.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organizations", description = "Gestion des organisations (laboratoires, universites, entreprises)")
@RequiredArgsConstructor
@Validated
public class OrganizationController {

    private final OrganizationService service;

    // ═══════════════════════════════════════════════════════════════════════
    // USER ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    @GetMapping
    @Operation(summary = "Mes organisations", description = "Liste les organisations dont l'utilisateur est membre actif")
    public ResponseEntity<List<OrganizationDTO>> getMyOrganizations(Authentication auth) {
        return ResponseEntity.ok(service.getMyOrganizations(auth.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une organisation", description = "Retourne le detail d'une organisation (necessite d'etre membre)")
    public ResponseEntity<OrganizationDTO> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.getById(id, auth.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une organisation", description = "Modifie les informations (OWNER ou MANAGER requis)")
    public ResponseEntity<OrganizationDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrganizationRequest request,
            Authentication auth) {
        return ResponseEntity.ok(service.update(id, request, auth.getName()));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher mes organisations", description = "Recherche par nom parmi les organisations de l'utilisateur")
    public ResponseEntity<List<OrganizationDTO>> search(
            @RequestParam("query") @NotBlank @Size(max = 200) String query,
            Authentication auth) {
        return ResponseEntity.ok(service.search(query, auth.getName()));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ADMIN ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les organisations (admin)", description = "Retourne toutes les organisations")
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        return ResponseEntity.ok(service.getAllOrganizations());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Creer une organisation (admin)", description = "Cree une organisation et assigne un OWNER")
    public ResponseEntity<OrganizationDTO> create(
            @Valid @RequestBody CreateOrganizationRequest request,
            Authentication auth) {
        return ResponseEntity.ok(service.create(request, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une organisation (admin)", description = "Supprime l'organisation et tous ses membres")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

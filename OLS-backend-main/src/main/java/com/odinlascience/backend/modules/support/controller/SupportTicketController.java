package com.odinlascience.backend.modules.support.controller;

import com.odinlascience.backend.modules.support.dto.*;
import com.odinlascience.backend.modules.support.enums.TicketPriority;
import com.odinlascience.backend.modules.support.enums.TicketStatus;
import com.odinlascience.backend.modules.support.service.SupportTicketAdminService;
import com.odinlascience.backend.modules.support.service.SupportTicketService;
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
@RequestMapping("/api/support")
@Tag(name = "Support", description = "Signalement de bugs, suggestions et contact avec l'équipe OLS")
@RequiredArgsConstructor
@Validated
public class SupportTicketController {

    private final SupportTicketService service;
    private final SupportTicketAdminService adminService;

    // ═══════════════════════════════════════════════════════════════════════
    // USER ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    @PostMapping
    @Operation(summary = "Créer un ticket de support",
               description = "Crée un nouveau ticket de support pour l'utilisateur connecté")
    public ResponseEntity<SupportTicketDTO> create(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.create(request, auth.getName()));
    }

    @GetMapping
    @Operation(summary = "Lister mes tickets",
               description = "Retourne tous les tickets de support de l'utilisateur connecté")
    public ResponseEntity<List<SupportTicketDTO>> getMyTickets(Authentication auth) {
        return ResponseEntity.ok(service.getMyItems(auth.getName()));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes tickets",
               description = "Recherche par sujet ou description")
    public ResponseEntity<List<SupportTicketDTO>> search(
            @RequestParam("query") @NotBlank @Size(max = 200) String query,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.search(query, auth.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un ticket",
               description = "Récupère les détails d'un ticket par son ID")
    public ResponseEntity<SupportTicketDTO> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.getById(id, auth.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un ticket",
               description = "Met à jour un ticket (uniquement si OPEN)")
    public ResponseEntity<SupportTicketDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.update(id, request, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un ticket",
               description = "Supprime un ticket (uniquement si OPEN)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Envoyer un message",
               description = "Envoie un message dans le fil de discussion d'un ticket")
    public ResponseEntity<TicketMessageDTO> sendMessage(
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.sendUserMessage(id, request, auth.getName()));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ADMIN ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les tickets (admin)",
               description = "Retourne tous les tickets de support de tous les utilisateurs")
    public ResponseEntity<List<SupportTicketDTO>> getAllTickets() {
        return ResponseEntity.ok(adminService.getAllTickets());
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques des tickets (admin)",
               description = "Retourne les compteurs par statut")
    public ResponseEntity<TicketStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @PostMapping("/admin/{id}/messages")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Envoyer un message admin",
               description = "Envoie un message admin dans un ticket et notifie le propriétaire")
    public ResponseEntity<TicketMessageDTO> sendAdminMessage(
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(adminService.sendAdminMessage(id, request, auth.getName()));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Changer le statut d'un ticket (admin)",
               description = "Modifie le statut d'un ticket")
    public ResponseEntity<SupportTicketDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status
    ) {
        return ResponseEntity.ok(adminService.updateStatus(id, status));
    }

    @PatchMapping("/admin/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Changer la priorité d'un ticket (admin)",
               description = "Modifie la priorité d'un ticket")
    public ResponseEntity<SupportTicketDTO> updatePriority(
            @PathVariable Long id,
            @RequestParam TicketPriority priority
    ) {
        return ResponseEntity.ok(adminService.updatePriority(id, priority));
    }
}

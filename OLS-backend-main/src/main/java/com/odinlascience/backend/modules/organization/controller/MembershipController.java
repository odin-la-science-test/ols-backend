package com.odinlascience.backend.modules.organization.controller;

import com.odinlascience.backend.modules.organization.dto.AddMemberRequest;
import com.odinlascience.backend.modules.organization.dto.MembershipDTO;
import com.odinlascience.backend.modules.organization.dto.UpdateMemberRoleRequest;
import com.odinlascience.backend.modules.organization.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{orgId}/members")
@Tag(name = "Organization Members", description = "Gestion des membres d'une organisation")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService service;

    @PostMapping
    @Operation(summary = "Ajouter un membre", description = "Invite un utilisateur dans l'organisation (OWNER ou MANAGER requis)")
    public ResponseEntity<MembershipDTO> addMember(
            @PathVariable Long orgId,
            @Valid @RequestBody AddMemberRequest request,
            Authentication auth) {
        return ResponseEntity.ok(service.addMember(orgId, request, auth.getName()));
    }

    @GetMapping
    @Operation(summary = "Lister les membres", description = "Liste tous les membres de l'organisation (necessite d'etre membre)")
    public ResponseEntity<List<MembershipDTO>> getMembers(@PathVariable Long orgId, Authentication auth) {
        return ResponseEntity.ok(service.getMembers(orgId, auth.getName()));
    }

    @PutMapping("/{membershipId}/role")
    @Operation(summary = "Changer le role d'un membre", description = "Modifie le role d'un membre (OWNER requis pour promouvoir en MANAGER/OWNER)")
    public ResponseEntity<MembershipDTO> updateRole(
            @PathVariable Long orgId,
            @PathVariable Long membershipId,
            @Valid @RequestBody UpdateMemberRoleRequest request,
            Authentication auth) {
        return ResponseEntity.ok(service.updateRole(orgId, membershipId, request, auth.getName()));
    }

    @DeleteMapping("/{membershipId}")
    @Operation(summary = "Retirer un membre", description = "Retire un membre de l'organisation (OWNER ou MANAGER requis)")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long orgId,
            @PathVariable Long membershipId,
            Authentication auth) {
        service.removeMember(orgId, membershipId, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accept")
    @Operation(summary = "Accepter une invitation", description = "Accepte l'invitation a rejoindre l'organisation")
    public ResponseEntity<MembershipDTO> acceptInvitation(@PathVariable Long orgId, Authentication auth) {
        return ResponseEntity.ok(service.acceptInvitation(orgId, auth.getName()));
    }

    @DeleteMapping("/leave")
    @Operation(summary = "Quitter l'organisation", description = "Quitte l'organisation (impossible si dernier OWNER)")
    public ResponseEntity<Void> leaveOrganization(@PathVariable Long orgId, Authentication auth) {
        service.leaveOrganization(orgId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

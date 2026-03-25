package com.odinlascience.backend.modules.contacts.controller;

import com.odinlascience.backend.modules.contacts.dto.ContactDTO;
import com.odinlascience.backend.modules.contacts.dto.CreateContactRequest;
import com.odinlascience.backend.modules.contacts.dto.UpdateContactRequest;
import com.odinlascience.backend.modules.contacts.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Carnet de contacts professionnels et collaborateurs")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService service;

    // ─── Créer un contact ───

    @PostMapping
    @Operation(summary = "Créer un contact",
               description = "Ajoute un nouveau contact au carnet de l'utilisateur")
    public ResponseEntity<ContactDTO> create(
            @Valid @RequestBody CreateContactRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.create(request, auth.getName()));
    }

    // ─── Lister mes contacts ───

    @GetMapping
    @Operation(summary = "Lister mes contacts",
               description = "Retourne tous les contacts de l'utilisateur connecté (favoris en premier, puis alphabétique)")
    public ResponseEntity<List<ContactDTO>> getMyContacts(Authentication auth) {
        return ResponseEntity.ok(service.getMyContacts(auth.getName()));
    }

    // ─── Détail d'un contact ───

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un contact",
               description = "Récupère les détails d'un contact par son ID")
    public ResponseEntity<ContactDTO> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.getById(id, auth.getName()));
    }

    // ─── Mettre à jour un contact ───

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un contact",
               description = "Met à jour les champs fournis d'un contact existant")
    public ResponseEntity<ContactDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContactRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.update(id, request, auth.getName()));
    }

    // ─── Supprimer un contact ───

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un contact",
               description = "Supprime un contact du carnet")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    // ─── Toggle favori ───

    @PatchMapping("/{id}/favorite")
    @Operation(summary = "Favori / Défavoriser un contact",
               description = "Bascule l'état favori d'un contact")
    public ResponseEntity<ContactDTO> toggleFavorite(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.toggleFavorite(id, auth.getName()));
    }

    // ─── Recherche ───

    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes contacts",
               description = "Recherche par nom, email, organisation ou fonction dans les contacts de l'utilisateur")
    public ResponseEntity<List<ContactDTO>> search(
            @RequestParam("query") String query,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.search(query, auth.getName()));
    }
}

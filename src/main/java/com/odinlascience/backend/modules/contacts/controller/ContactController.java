package com.odinlascience.backend.modules.contacts.controller;

import com.odinlascience.backend.modules.common.controller.AbstractOwnedCrudController;
import com.odinlascience.backend.modules.contacts.dto.ContactDTO;
import com.odinlascience.backend.modules.contacts.dto.CreateContactRequest;
import com.odinlascience.backend.modules.contacts.dto.UpdateContactRequest;
import com.odinlascience.backend.modules.contacts.service.ContactService;
import com.odinlascience.backend.modules.common.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Carnet de contacts professionnels et collaborateurs")
public class ContactController
        extends AbstractOwnedCrudController<ContactDTO, CreateContactRequest, UpdateContactRequest, ContactService> {

    public ContactController(ContactService service, ExportService exportService) {
        super(service, exportService);
    }

    // ─── Swagger overrides ───

    @Override
    @PostMapping
    @Operation(summary = "Créer un contact",
               description = "Ajoute un nouveau contact au carnet de l'utilisateur")
    public ResponseEntity<ContactDTO> create(@RequestBody CreateContactRequest request, Authentication auth) {
        return super.create(request, auth);
    }

    @Override
    @GetMapping
    @Operation(summary = "Lister mes contacts",
               description = "Retourne tous les contacts de l'utilisateur connecté (favoris en premier, puis alphabétique)")
    public ResponseEntity<List<ContactDTO>> getMyItems(Authentication auth) {
        return super.getMyItems(auth);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un contact",
               description = "Récupère les détails d'un contact par son ID")
    public ResponseEntity<ContactDTO> getById(@PathVariable Long id, Authentication auth) {
        return super.getById(id, auth);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un contact",
               description = "Met à jour les champs fournis d'un contact existant")
    public ResponseEntity<ContactDTO> update(@PathVariable Long id, @RequestBody UpdateContactRequest request,
                                             Authentication auth) {
        return super.update(id, request, auth);
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un contact",
               description = "Supprime un contact du carnet")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        return super.delete(id, auth);
    }

    @Override
    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes contacts",
               description = "Recherche par nom, email, organisation ou fonction dans les contacts de l'utilisateur")
    public ResponseEntity<List<ContactDTO>> search(@RequestParam("query") String query, Authentication auth) {
        return super.search(query, auth);
    }

    // ─── Restaurer ───

    @Override
    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurer un contact supprimé",
               description = "Annule la suppression (soft delete) d'un contact")
    public ResponseEntity<ContactDTO> restore(@PathVariable Long id, Authentication auth) {
        return super.restore(id, auth);
    }

    // ─── Toggle favori ───

    @PatchMapping("/{id}/favorite")
    @Operation(summary = "Favori / Défavoriser un contact",
               description = "Bascule l'état favori d'un contact")
    public ResponseEntity<ContactDTO> toggleFavorite(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.toggleFavorite(id, auth.getName()));
    }
}

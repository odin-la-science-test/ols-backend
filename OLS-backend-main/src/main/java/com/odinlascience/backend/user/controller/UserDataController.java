package com.odinlascience.backend.user.controller;

import com.odinlascience.backend.user.dto.DeleteAccountRequest;
import com.odinlascience.backend.user.dto.UserDataExportDTO;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.service.UserDataDeletionService;
import com.odinlascience.backend.user.service.UserDataExportService;
import com.odinlascience.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller RGPD : export des donnees personnelles et suppression de compte.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Donnees utilisateur (RGPD)", description = "Export et suppression des donnees personnelles")
public class UserDataController {

    private final UserDataExportService exportService;
    private final UserDataDeletionService deletionService;
    private final UserService userService;

    @GetMapping("/data-export")
    @Operation(summary = "Exporter ses donnees personnelles",
               description = "Retourne un JSON structuré contenant toutes les donnees personnelles de l'utilisateur (RGPD - droit d'acces)")
    public ResponseEntity<UserDataExportDTO> exportData(Authentication auth) {
        User user = resolveUser(auth);
        return ResponseEntity.ok(exportService.exportUserData(user));
    }

    @DeleteMapping("/account")
    @Operation(summary = "Supprimer son compte",
               description = "Anonymise le profil et supprime les donnees personnelles (RGPD - droit a l'effacement). Necessite une confirmation par email.")
    public ResponseEntity<Void> deleteAccount(Authentication auth,
                                               @Valid @RequestBody DeleteAccountRequest request) {
        User user = resolveUser(auth);
        deletionService.deleteAccount(user, request.getConfirmEmail());
        return ResponseEntity.noContent().build();
    }

    private User resolveUser(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }
}

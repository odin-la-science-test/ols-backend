package com.odinlascience.backend.user.controller;

import com.odinlascience.backend.exception.dto.ErrorResponseDTO;
import com.odinlascience.backend.user.dto.ChangePasswordRequest;
import com.odinlascience.backend.user.dto.UpdateAvatarRequest;
import com.odinlascience.backend.user.dto.UpdateUserPreferencesRequest;
import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.dto.UserPreferencesDTO;
import com.odinlascience.backend.user.service.UserPreferencesService;
import com.odinlascience.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import com.odinlascience.backend.user.service.UserContextService;
import com.odinlascience.backend.user.mapper.UserMapper;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs")
@Validated
public class UserController {

    private final UserService service;
    private final UserContextService userContextService;
    private final UserMapper userMapper;
    private final UserPreferencesService preferencesService;

    @GetMapping
    @Operation(summary = "Lister les utilisateurs", description = "Retourne la liste de tous les utilisateurs (DTO)")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des utilisateurs", description = "Recherche par nom, prenom ou email")
    public ResponseEntity<List<UserDTO>> search(@RequestParam("query") @NotBlank @Size(max = 200) String query) {
        return ResponseEntity.ok(service.searchUsers(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur", description = "Retourne un utilisateur par son identifiant")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Utilisateur courant", description = "Retourne les informations de l'utilisateur authentifié")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest httpRequest) {
        return userContextService.getCurrentUser()
                .map(userMapper::toDTO)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ErrorResponseDTO.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("Unauthorized")
                        .message("Utilisateur non authentifié")
                        .path(httpRequest.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
                ));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Changer le mot de passe", description = "Change le mot de passe de l'utilisateur authentifie (comptes locaux uniquement)")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        service.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/avatar")
    @Operation(summary = "Changer l'avatar", description = "Met a jour l'avatar de l'utilisateur authentifié")
    public ResponseEntity<UserDTO> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        return ResponseEntity.ok(service.updateAvatar(request.getAvatarId()));
    }

    @GetMapping("/me/preferences")
    @Operation(summary = "Preferences utilisateur", description = "Retourne les preferences de l'utilisateur authentifié")
    public ResponseEntity<UserPreferencesDTO> getPreferences() {
        UserPreferencesDTO prefs = preferencesService.getPreferences();
        if (prefs == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prefs);
    }

    @PutMapping("/me/preferences")
    @Operation(summary = "Mettre a jour les preferences", description = "Sauvegarde les preferences de l'utilisateur authentifié")
    public ResponseEntity<UserPreferencesDTO> updatePreferences(@Valid @RequestBody UpdateUserPreferencesRequest request) {
        return ResponseEntity.ok(preferencesService.updatePreferences(request));
    }
}
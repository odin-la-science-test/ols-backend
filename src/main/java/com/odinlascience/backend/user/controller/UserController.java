package com.odinlascience.backend.user.controller;

import com.odinlascience.backend.exception.dto.ErrorResponseDTO;
import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import com.odinlascience.backend.user.service.UserContextService;
import com.odinlascience.backend.user.mapper.UserMapper;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs")
public class UserController {

    private final UserService service;
    private final UserContextService userContextService;
    private final UserMapper userMapper;

    @GetMapping
    @Operation(summary = "Lister les utilisateurs", description = "Retourne la liste de tous les utilisateurs (DTO)")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
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
}
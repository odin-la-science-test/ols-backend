package com.odinlascience.backend.auth.controller;

import com.odinlascience.backend.auth.dto.SessionDTO;
import com.odinlascience.backend.auth.service.SessionService;
import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Gestion des sessions actives")
public class SessionController {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Lister les sessions actives", description = "Retourne toutes les sessions actives de l'utilisateur connecte")
    public ResponseEntity<List<SessionDTO>> getActiveSessions(Authentication auth, HttpServletRequest request) {
        User user = getUser(auth);
        UUID currentSessionId = (UUID) request.getAttribute("sessionId");
        return ResponseEntity.ok(sessionService.getActiveSessions(user.getId(), currentSessionId));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Revoquer une session", description = "Revoque une session specifique de l'utilisateur connecte")
    public ResponseEntity<Void> revokeSession(@PathVariable UUID sessionId, Authentication auth) {
        User user = getUser(auth);
        sessionService.revokeSession(sessionId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Revoquer toutes les autres sessions", description = "Revoque toutes les sessions sauf la session courante")
    public ResponseEntity<Void> revokeAllOtherSessions(Authentication auth, HttpServletRequest request) {
        User user = getUser(auth);
        UUID currentSessionId = (UUID) request.getAttribute("sessionId");
        if (currentSessionId != null) {
            sessionService.revokeAllOtherSessions(user.getId(), currentSessionId);
        }
        return ResponseEntity.noContent().build();
    }

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
    }
}

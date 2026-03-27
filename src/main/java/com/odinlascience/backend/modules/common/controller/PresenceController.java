package com.odinlascience.backend.modules.common.controller;

import com.odinlascience.backend.modules.common.service.PresenceService;
import com.odinlascience.backend.modules.notifications.service.SseEmitterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
@Tag(name = "Presence", description = "Suivi de la presence des utilisateurs en ligne")
public class PresenceController {

    private final PresenceService presenceService;
    private final SseEmitterService sseEmitterService;

    @GetMapping("/online")
    @Operation(summary = "Liste des utilisateurs en ligne",
            description = "Retourne les emails des utilisateurs actuellement connectes")
    public ResponseEntity<Set<String>> getOnlineUsers() {
        return ResponseEntity.ok(presenceService.getOnlineUsers());
    }

    @GetMapping("/count")
    @Operation(summary = "Nombre d'utilisateurs en ligne",
            description = "Retourne le nombre d'utilisateurs actuellement connectes")
    public ResponseEntity<Integer> getOnlineCount() {
        return ResponseEntity.ok(presenceService.getOnlineCount());
    }

    @GetMapping("/connected")
    @Operation(summary = "IDs des utilisateurs connectes au flux SSE",
            description = "Retourne les IDs des utilisateurs ayant une connexion SSE active (fallback pour le chargement initial)")
    public ResponseEntity<Set<Long>> getConnectedUserIds() {
        return ResponseEntity.ok(sseEmitterService.getConnectedUserIds());
    }

    @PostMapping("/heartbeat")
    @Operation(summary = "Signal de presence",
            description = "Envoie un heartbeat pour maintenir le statut en ligne")
    public ResponseEntity<Void> heartbeat(Authentication auth) {
        presenceService.heartbeat(auth.getName());
        return ResponseEntity.ok().build();
    }
}

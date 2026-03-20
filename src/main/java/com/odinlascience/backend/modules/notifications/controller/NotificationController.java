package com.odinlascience.backend.modules.notifications.controller;

import com.odinlascience.backend.modules.notifications.dto.NotificationDTO;
import com.odinlascience.backend.modules.notifications.dto.UnreadCountDTO;
import com.odinlascience.backend.modules.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications utilisateur")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    // ─── Mes notifications ───

    @GetMapping
    @Operation(summary = "Lister mes notifications",
               description = "Retourne toutes les notifications de l'utilisateur connecté, les plus récentes en premier")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(Authentication auth) {
        return ResponseEntity.ok(service.getMyNotifications(auth.getName()));
    }

    // ─── Compteur non lues ───

    @GetMapping("/unread-count")
    @Operation(summary = "Nombre de notifications non lues",
               description = "Retourne le nombre de notifications non lues pour l'utilisateur connecté")
    public ResponseEntity<UnreadCountDTO> getUnreadCount(Authentication auth) {
        return ResponseEntity.ok(service.getUnreadCount(auth.getName()));
    }

    // ─── Marquer comme lue ───

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<NotificationDTO> markAsRead(
            @PathVariable Long id,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.markAsRead(id, auth.getName()));
    }

    // ─── Marquer toutes comme lues ───

    @PatchMapping("/read-all")
    @Operation(summary = "Marquer toutes les notifications comme lues")
    public ResponseEntity<Void> markAllAsRead(Authentication auth) {
        service.markAllAsRead(auth.getName());
        return ResponseEntity.ok().build();
    }

    // ─── Supprimer une notification ───

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une notification")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

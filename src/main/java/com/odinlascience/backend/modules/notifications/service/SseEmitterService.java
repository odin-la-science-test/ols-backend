package com.odinlascience.backend.modules.notifications.service;

import com.odinlascience.backend.modules.notifications.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gere les connexions SSE actives pour le push temps reel des notifications.
 * <p>
 * Une connexion par utilisateur. Le client se reconnecte automatiquement
 * via {@code @microsoft/fetch-event-source} en cas de timeout ou d'erreur.
 * <p>
 * Diffuse egalement les mises a jour de presence (event {@code presence})
 * a tous les utilisateurs connectes lors des connexions/deconnexions.
 */
@Slf4j
@Service
public class SseEmitterService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Inscrit un utilisateur au flux SSE.
     * Si une connexion existe deja, elle est remplacee.
     * Diffuse la presence mise a jour a tous les connectes.
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter existing = emitters.get(userId);
        if (existing != null) {
            existing.complete();
            emitters.remove(userId);
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.debug("SSE connection completed: userId={}", userId);
            broadcastPresence();
        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.debug("SSE connection timed out: userId={}", userId);
            broadcastPresence();
        });
        emitter.onError(e -> {
            emitters.remove(userId);
            log.debug("SSE connection error: userId={}", userId);
            broadcastPresence();
        });

        emitters.put(userId, emitter);

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(userId);
            log.warn("Failed to send initial SSE event: userId={}", userId);
        }

        log.info("SSE connection established: userId={}, activeConnections={}", userId, emitters.size());
        broadcastPresence();
        return emitter;
    }

    /**
     * Pousse une notification en temps reel vers un utilisateur connecte.
     * Si l'utilisateur n'est pas connecte, l'operation est silencieusement ignoree
     * (la notification est deja persistee en BDD).
     */
    public void pushNotification(Long userId, NotificationDTO dto) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event().name("notification").data(dto));
        } catch (IOException e) {
            emitters.remove(userId);
            log.debug("Failed to push SSE notification, removing emitter: userId={}", userId);
        }
    }

    /**
     * Retourne les IDs des utilisateurs actuellement connectes au flux SSE.
     */
    public Set<Long> getConnectedUserIds() {
        return Set.copyOf(emitters.keySet());
    }

    /**
     * Deconnecte un utilisateur du flux SSE.
     */
    public void disconnect(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
        broadcastPresence();
    }

    /**
     * Diffuse la liste des IDs connectes a tous les emitters (fire-and-forget).
     * Les emitters en erreur sont retires silencieusement.
     */
    private void broadcastPresence() {
        Set<Long> connectedIds = Set.copyOf(emitters.keySet());
        emitters.forEach((uid, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("presence").data(connectedIds));
            } catch (IOException e) {
                emitters.remove(uid);
                log.debug("Failed to broadcast presence, removing emitter: userId={}", uid);
            }
        });
    }
}

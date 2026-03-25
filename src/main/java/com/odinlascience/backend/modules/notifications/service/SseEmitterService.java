package com.odinlascience.backend.modules.notifications.service;

import com.odinlascience.backend.modules.notifications.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gere les connexions SSE actives pour le push temps reel des notifications.
 * <p>
 * Une connexion par utilisateur. Le client se reconnecte automatiquement
 * via {@code @microsoft/fetch-event-source} en cas de timeout ou d'erreur.
 */
@Slf4j
@Service
public class SseEmitterService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Inscrit un utilisateur au flux SSE.
     * Si une connexion existe deja, elle est remplacee.
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
        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.debug("SSE connection timed out: userId={}", userId);
        });
        emitter.onError(e -> {
            emitters.remove(userId);
            log.debug("SSE connection error: userId={}", userId);
        });

        emitters.put(userId, emitter);

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(userId);
            log.warn("Failed to send initial SSE event: userId={}", userId);
        }

        log.info("SSE connection established: userId={}, activeConnections={}", userId, emitters.size());
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
     * Deconnecte un utilisateur du flux SSE.
     */
    public void disconnect(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
    }
}

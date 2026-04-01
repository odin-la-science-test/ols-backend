package com.odinlascience.backend.modules.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Suivi en memoire de la presence des utilisateurs connectes.
 * Les entrees plus vieilles que 2 minutes sont considerees obsoletes
 * et nettoyees a chaque acces.
 */
@Service
@Slf4j
public class PresenceService {

    private static final long STALE_THRESHOLD_MINUTES = 2;

    private final ConcurrentMap<String, Instant> onlineUsers = new ConcurrentHashMap<>();

    public void userConnected(String email) {
        onlineUsers.put(email, Instant.now());
        log.debug("Utilisateur connecte : {}", email);
    }

    public void userDisconnected(String email) {
        onlineUsers.remove(email);
        log.debug("Utilisateur deconnecte : {}", email);
    }

    /**
     * Rafraichit le timestamp de presence (heartbeat).
     */
    public void heartbeat(String email) {
        onlineUsers.put(email, Instant.now());
    }

    public Set<String> getOnlineUsers() {
        cleanupStaleEntries();
        return Set.copyOf(onlineUsers.keySet());
    }

    public int getOnlineCount() {
        cleanupStaleEntries();
        return onlineUsers.size();
    }

    public boolean isOnline(String email) {
        cleanupStaleEntries();
        return onlineUsers.containsKey(email);
    }

    private void cleanupStaleEntries() {
        Instant threshold = Instant.now().minus(STALE_THRESHOLD_MINUTES, ChronoUnit.MINUTES);
        onlineUsers.entrySet().removeIf(entry -> entry.getValue().isBefore(threshold));
    }
}

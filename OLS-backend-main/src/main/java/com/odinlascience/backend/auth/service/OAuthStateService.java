package com.odinlascience.backend.auth.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthStateService {

    private static final long STATE_TTL_SECONDS = 600;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final Map<String, Instant> states = new ConcurrentHashMap<>();

    public String generateState() {
        cleanExpired();
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        states.put(state, Instant.now().plusSeconds(STATE_TTL_SECONDS));
        return state;
    }

    public boolean validateAndConsume(String state) {
        if (state == null || state.isBlank()) {
            return false;
        }
        Instant expiresAt = states.remove(state);
        return expiresAt != null && Instant.now().isBefore(expiresAt);
    }

    private void cleanExpired() {
        Instant now = Instant.now();
        states.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
}

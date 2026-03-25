package com.odinlascience.backend.auth.config;

import com.odinlascience.backend.auth.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCleanupScheduler {

    private final SessionService sessionService;

    @Scheduled(fixedRate = 3_600_000)
    public void cleanupExpiredSessions() {
        sessionService.cleanupExpiredSessions();
    }
}

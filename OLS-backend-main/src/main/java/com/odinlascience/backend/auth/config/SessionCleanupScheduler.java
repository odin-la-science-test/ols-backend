package com.odinlascience.backend.auth.config;

import com.odinlascience.backend.auth.service.SessionService;
import com.odinlascience.backend.config.SchedulerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCleanupScheduler {

    private final SessionService sessionService;
    private final SchedulerRegistry schedulerRegistry;

    @PostConstruct
    public void init() {
        schedulerRegistry.register(
                "session-cleanup",
                "Nettoyage des sessions expirees",
                "fixedRate=3600000 (1h)"
        );
    }

    @Scheduled(fixedRate = 3_600_000)
    public void cleanupExpiredSessions() {
        sessionService.cleanupExpiredSessions();
        schedulerRegistry.recordExecution("session-cleanup");
    }
}

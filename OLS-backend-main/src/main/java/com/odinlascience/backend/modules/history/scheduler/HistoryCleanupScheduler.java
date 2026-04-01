package com.odinlascience.backend.modules.history.scheduler;

import com.odinlascience.backend.config.SchedulerRegistry;
import com.odinlascience.backend.modules.history.service.HistoryEntryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Nettoyage quotidien des entries d'historique > 90 jours.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryCleanupScheduler {

    private static final int RETENTION_DAYS = 90;

    private final HistoryEntryService historyEntryService;
    private final SchedulerRegistry schedulerRegistry;

    @PostConstruct
    public void init() {
        schedulerRegistry.register(
                "history-cleanup",
                "Nettoyage quotidien des entries d'historique > " + RETENTION_DAYS + " jours",
                "cron=0 30 3 * * * (3h30 du matin)"
        );
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void cleanupOldEntries() {
        Instant cutoff = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
        int deleted = historyEntryService.purgeOlderThan(cutoff);

        if (deleted > 0) {
            log.info("History cleanup: {} entries supprimees (> {} jours)", deleted, RETENTION_DAYS);
        }
        schedulerRegistry.recordExecution("history-cleanup");
    }
}

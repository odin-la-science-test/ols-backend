package com.odinlascience.backend.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Registre des taches planifiees pour le monitoring.
 * Permet d'enregistrer et de suivre l'execution des taches @Scheduled.
 */
@Component
@Slf4j
public class SchedulerRegistry {

    private final Map<String, ScheduledTaskInfo> tasks = new ConcurrentHashMap<>();

    /**
     * Enregistre une tache planifiee dans le registre.
     */
    public void register(String name, String description, String schedule) {
        tasks.put(name, ScheduledTaskInfo.builder()
                .name(name)
                .description(description)
                .schedule(schedule)
                .executionCount(0)
                .build());
        log.info("Tache planifiee enregistree : {} ({})", name, schedule);
    }

    /**
     * Enregistre une execution de la tache (met a jour lastExecution et le compteur).
     */
    public void recordExecution(String name) {
        ScheduledTaskInfo info = tasks.get(name);
        if (info != null) {
            info.setLastExecution(Instant.now());
            info.setExecutionCount(info.getExecutionCount() + 1);
        }
    }

    /**
     * Retourne toutes les taches enregistrees.
     */
    public Collection<ScheduledTaskInfo> getAll() {
        return tasks.values();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduledTaskInfo {
        private String name;
        private String description;
        private String schedule;
        private Instant lastExecution;
        private int executionCount;
    }
}

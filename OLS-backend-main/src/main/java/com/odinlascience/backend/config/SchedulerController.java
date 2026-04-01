package com.odinlascience.backend.config;

import com.odinlascience.backend.config.SchedulerRegistry.ScheduledTaskInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Endpoint admin pour le monitoring des taches planifiees.
 */
@RestController
@RequestMapping("/api/admin/scheduler")
@Tag(name = "Scheduler", description = "Monitoring des taches planifiees")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerRegistry schedulerRegistry;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Liste des taches planifiees",
               description = "Retourne toutes les taches planifiees enregistrees avec leur statut")
    public ResponseEntity<Collection<ScheduledTaskInfo>> getAll() {
        return ResponseEntity.ok(schedulerRegistry.getAll());
    }
}

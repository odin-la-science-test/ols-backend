package com.odinlascience.backend.modules.history.listener;

import com.odinlascience.backend.modules.common.event.CrudActionEvent;
import com.odinlascience.backend.modules.history.context.HistoryContext;
import com.odinlascience.backend.modules.history.service.HistoryEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Ecoute les CrudActionEvent publies par AbstractOwnedCrudService
 * et enregistre les entries d'historique automatiquement.
 * Utilise @EventListener (synchrone, meme transaction) pour garantir
 * que l'entree est persistee atomiquement avec l'action CRUD.
 * Ignore les requetes marquees X-History-Skip (undo/redo).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryRecordingListener {

    private final HistoryEntryService historyEntryService;

    @EventListener
    public void onCrudAction(CrudActionEvent event) {
        if (HistoryContext.shouldSkip()) {
            log.debug("History skip: {} {} {} (X-History-Skip header present)",
                    event.moduleSlug(), event.actionType(), event.entityId());
            return;
        }

        try {
            historyEntryService.record(event);
        } catch (Exception e) {
            log.warn("Failed to record history entry: {} {} {}",
                    event.moduleSlug(), event.actionType(), event.entityId(), e);
        }
    }
}

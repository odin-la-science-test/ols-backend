package com.odinlascience.backend.modules.history.service;

import com.odinlascience.backend.modules.common.event.CrudActionEvent;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.history.dto.HistoryEntryDTO;
import com.odinlascience.backend.modules.history.mapper.HistoryEntryMapper;
import com.odinlascience.backend.modules.history.model.HistoryActionType;
import com.odinlascience.backend.modules.history.model.HistoryEntry;
import com.odinlascience.backend.modules.history.repository.HistoryEntryRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryEntryService {

    private final HistoryEntryRepository repository;
    private final HistoryEntryMapper mapper;
    private final UserHelper userHelper;

    /**
     * Enregistre une entree d'historique a partir d'un CrudActionEvent.
     */
    @Transactional
    public void record(CrudActionEvent event) {
        User owner = userHelper.findByEmail(event.userEmail());
        String labelKey = "history." + event.moduleSlug() + "." + event.labelKeySuffix();

        HistoryEntry entry = HistoryEntry.builder()
                .moduleSlug(event.moduleSlug())
                .actionType(HistoryActionType.valueOf(event.actionType()))
                .entityId(event.entityId())
                .labelKey(labelKey)
                .icon(event.icon())
                .previousData(event.previousData())
                .newData(event.newData())
                .owner(owner)
                .build();

        repository.save(entry);
        log.debug("History entry recorded: {} {} {} (entity={})",
                event.moduleSlug(), event.actionType(), event.entityId(), labelKey);
    }

    /**
     * Retourne les entries d'un module pour un utilisateur, triees ASC.
     */
    @Transactional(readOnly = true)
    public List<HistoryEntryDTO> getByModule(String moduleSlug, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.findByOwnerIdAndModuleSlugOrderByCreatedAtAsc(owner.getId(), moduleSlug)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Supprime toutes les entries d'un scope.
     */
    @Transactional
    public void clearModule(String moduleSlug, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        repository.deleteByOwnerIdAndModuleSlug(owner.getId(), moduleSlug);
        log.info("History cleared: module={}, owner={}", moduleSlug, userEmail);
    }

    /**
     * Coupe le redo stack : supprime les entries creees apres l'entree donnee.
     */
    @Transactional
    public void truncateAfter(Long entryId, String moduleSlug, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        HistoryEntry entry = repository.findById(entryId).orElse(null);
        if (entry == null || !entry.getOwner().getId().equals(owner.getId())) return;

        repository.truncateAfter(owner.getId(), moduleSlug, entry.getCreatedAt());
        log.debug("History truncated after entry {}: module={}, owner={}", entryId, moduleSlug, userEmail);
    }

    /**
     * Purge les entries plus anciennes que la date limite.
     * Appele par le scheduled task.
     */
    @Transactional
    public int purgeOlderThan(Instant cutoff) {
        int deleted = repository.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("History purged: {} entries older than {}", deleted, cutoff);
        }
        return deleted;
    }
}

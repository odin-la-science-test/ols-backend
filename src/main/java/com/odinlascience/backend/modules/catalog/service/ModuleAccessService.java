package com.odinlascience.backend.modules.catalog.service;

import com.odinlascience.backend.modules.catalog.model.AppModule;
import com.odinlascience.backend.modules.common.event.ModuleAccessGrantedEvent;
import com.odinlascience.backend.modules.catalog.model.UserModuleAccess;
import com.odinlascience.backend.modules.catalog.repository.AppModuleRepository;
import com.odinlascience.backend.modules.catalog.repository.UserModuleAccessRepository;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleAccessService {

    private final UserQuerySPI userQuerySPI;
    private final UserModuleAccessRepository accessRepository;
    private final AppModuleRepository appModuleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public boolean isModuleLocked(AppModule module) {
        Optional<User> currentUserOpt = userQuerySPI.getCurrentUser();

        if (currentUserOpt.isEmpty()) {
            return true;
        }

        User currentUser = currentUserOpt.get();

        if (currentUser.getRole() == RoleType.ADMIN) {
            return false;
        }

        if (module.getPrice() == null || module.getPrice() <= 0) {
            return false;
        }

        return !accessRepository.existsByUserIdAndModuleIdAndHasAccessTrue(currentUser.getId(), module.getId());
    }

    public boolean hasAccess(String moduleKey) {
        Optional<User> currentUserOpt = userQuerySPI.getCurrentUser();

        if (currentUserOpt.isEmpty()) {
            return false;
        }

        User currentUser = currentUserOpt.get();

        if (currentUser.getRole() == RoleType.ADMIN) {
            return true;
        }

        return accessRepository.existsByUserIdAndModuleModuleKeyAndHasAccessTrue(currentUser.getId(), moduleKey);
    }

    @Transactional
    public UserModuleAccess grantAccess(User user, AppModule module, LocalDateTime expiresAt) {
        Optional<UserModuleAccess> existingAccess = accessRepository
            .findByUserIdAndModuleModuleKey(user.getId(), module.getModuleKey());

        UserModuleAccess saved;
        if (existingAccess.isPresent()) {
            UserModuleAccess access = existingAccess.get();
            access.setHasAccess(true);
            access.setExpiresAt(expiresAt);
            saved = accessRepository.save(access);
        } else {
            UserModuleAccess access = UserModuleAccess.builder()
                .user(user)
                .module(module)
                .hasAccess(true)
                .purchasedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();
            saved = accessRepository.save(access);
        }

        eventPublisher.publishEvent(new ModuleAccessGrantedEvent(
                user.getEmail(),
                module.getModuleKey(),
                module.getTitle(),
                module.getRoutePath()
        ));
        log.info("Module access granted: user={}, module={}", user.getEmail(), module.getModuleKey());

        return saved;
    }

    @Transactional
    public UserModuleAccess grantPermanentAccess(User user, AppModule module) {
        return grantAccess(user, module, null);
    }

    @Transactional
    public void revokeAccess(User user, AppModule module) {
        accessRepository.findByUserIdAndModuleModuleKey(user.getId(), module.getModuleKey())
            .ifPresent(access -> {
                access.setHasAccess(false);
                accessRepository.save(access);
            });
    }

    public List<UserModuleAccess> getCurrentUserAccessibleModules() {
        return userQuerySPI.getCurrentUser()
            .map(user -> accessRepository.findByUserIdAndHasAccessTrue(user.getId()))
            .orElse(List.of());
    }

    /**
     * Retourne les cles des modules auxquels l'utilisateur a acces.
     * Pour l'instant, retourne tous les modules actifs du catalogue
     * (l'infrastructure est en place, la logique de permission sera affinee plus tard).
     */
    @Transactional(readOnly = true)
    public List<String> getAccessibleModuleKeys(String userEmail) {
        log.debug("Recuperation des modules accessibles pour l'utilisateur: {}", userEmail);
        return appModuleRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getActive()))
                .map(AppModule::getModuleKey)
                .toList();
    }
}
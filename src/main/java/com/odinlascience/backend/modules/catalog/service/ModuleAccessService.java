package com.odinlascience.backend.modules.catalog.service;

import com.odinlascience.backend.modules.catalog.model.AppModule;
import com.odinlascience.backend.modules.catalog.model.UserModuleAccess;
import com.odinlascience.backend.modules.catalog.repository.UserModuleAccessRepository;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.service.UserContextService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleAccessService {

    private final UserContextService userContextService;
    private final UserModuleAccessRepository accessRepository;

    public boolean isModuleLocked(AppModule module) {
        Optional<User> currentUserOpt = userContextService.getCurrentUser();

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
        Optional<User> currentUserOpt = userContextService.getCurrentUser();

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

        if (existingAccess.isPresent()) {
            UserModuleAccess access = existingAccess.get();
            access.setHasAccess(true);
            access.setExpiresAt(expiresAt);
            return accessRepository.save(access);
        }

        UserModuleAccess access = UserModuleAccess.builder()
            .user(user)
            .module(module)
            .hasAccess(true)
            .purchasedAt(LocalDateTime.now())
            .expiresAt(expiresAt)
            .build();

        return accessRepository.save(access);
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
        return userContextService.getCurrentUser()
            .map(user -> accessRepository.findByUserIdAndHasAccessTrue(user.getId()))
            .orElse(List.of());
    }
}
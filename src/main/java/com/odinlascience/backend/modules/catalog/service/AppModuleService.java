package com.odinlascience.backend.modules.catalog.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.catalog.dto.AppModuleDTO;
import com.odinlascience.backend.modules.catalog.enums.ModuleType;
import com.odinlascience.backend.modules.catalog.mapper.AppModuleMapper;
import com.odinlascience.backend.modules.catalog.model.AppModule;
import com.odinlascience.backend.modules.catalog.repository.AppModuleRepository;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.user.enums.RoleType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppModuleService {

    private final AppModuleRepository repository;
    private final AppModuleMapper mapper;
    private final ModuleAccessService accessService;
    private final UserQuerySPI userQuerySPI;

    private boolean isCurrentUserAdmin() {
        return userQuerySPI.getCurrentUser()
                .map(u -> u.getRole() == RoleType.ADMIN)
                .orElse(false);
    }

    public List<AppModuleDTO> getAllModules() {
        boolean admin = isCurrentUserAdmin();
        return repository.findAll().stream()
                .filter(m -> admin || !Boolean.TRUE.equals(m.getAdminOnly()))
                .map(this::mapWithLockStatus)
                .toList();
    }

    public List<AppModuleDTO> getModulesByType(ModuleType type) {
        boolean admin = isCurrentUserAdmin();
        return repository.findByType(type).stream()
                .filter(m -> admin || !Boolean.TRUE.equals(m.getAdminOnly()))
                .map(this::mapWithLockStatus)
                .toList();
    }

    public AppModuleDTO getModuleByKey(String moduleKey) {
        AppModule module = repository.findByModuleKey(moduleKey)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable avec la clé : " + moduleKey));
        return mapWithLockStatus(module);
    }

    private AppModuleDTO mapWithLockStatus(AppModule module) {
        boolean locked = accessService.isModuleLocked(module);
        return mapper.toDTO(module, locked);
    }
}
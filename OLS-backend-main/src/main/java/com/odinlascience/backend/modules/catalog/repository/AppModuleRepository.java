package com.odinlascience.backend.modules.catalog.repository;

import com.odinlascience.backend.modules.catalog.enums.ModuleType;
import com.odinlascience.backend.modules.catalog.model.AppModule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppModuleRepository extends JpaRepository<AppModule, Long> {
    Optional<AppModule> findByModuleKey(String moduleKey);

    List<AppModule> findByType(ModuleType type);
}
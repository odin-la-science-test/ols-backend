package com.odinlascience.backend.modules.catalog.repository;

import com.odinlascience.backend.modules.catalog.model.UserModuleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserModuleAccessRepository extends JpaRepository<UserModuleAccess, Long> {

    Optional<UserModuleAccess> findByUserIdAndModuleId(Long userId, Long moduleId);

    Optional<UserModuleAccess> findByUserIdAndModuleModuleKey(Long userId, String moduleKey);

    List<UserModuleAccess> findByUserIdAndHasAccessTrue(Long userId);

    boolean existsByUserIdAndModuleIdAndHasAccessTrue(Long userId, Long moduleId);

    boolean existsByUserIdAndModuleModuleKeyAndHasAccessTrue(Long userId, String moduleKey);
}

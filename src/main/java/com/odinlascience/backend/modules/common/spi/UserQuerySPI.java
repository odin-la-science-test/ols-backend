package com.odinlascience.backend.modules.common.spi;

import com.odinlascience.backend.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * SPI pour les requetes utilisateur depuis les modules.
 * Evite le couplage direct vers UserRepository.
 * L'implementation vit dans le package {@code user.service}.
 */
public interface UserQuerySPI {

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    List<User> search(String query);

    Optional<User> getCurrentUser();
}

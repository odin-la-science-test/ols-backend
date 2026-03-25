package com.odinlascience.backend.modules.common.spi;

import java.util.List;
import java.util.Optional;

/**
 * SPI pour les requetes d'organisation depuis les autres modules.
 * Evite le couplage direct vers les repositories du module organization.
 * L'implementation vit dans {@code modules.organization.service}.
 */
public interface OrganizationQuerySPI {

    List<Long> getOrganizationIdsForUser(String userEmail);

    boolean isUserMemberOf(String userEmail, Long organizationId);

    Optional<String> getUserRoleInOrg(String userEmail, Long organizationId);

    boolean isSupervisorOf(String supervisorEmail, String superviseeEmail, Long organizationId);
}

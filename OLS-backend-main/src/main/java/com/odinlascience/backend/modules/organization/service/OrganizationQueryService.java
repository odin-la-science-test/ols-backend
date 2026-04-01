package com.odinlascience.backend.modules.organization.service;

import com.odinlascience.backend.modules.common.spi.OrganizationQuerySPI;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.modules.organization.repository.SupervisionRelationshipRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationQueryService implements OrganizationQuerySPI {

    private final OrganizationMembershipRepository membershipRepository;
    private final SupervisionRelationshipRepository supervisionRepository;
    private final UserQuerySPI userQuerySPI;

    @Override
    @Transactional(readOnly = true)
    public List<Long> getOrganizationIdsForUser(String userEmail) {
        return userQuerySPI.findByEmail(userEmail)
                .map(user -> membershipRepository.findByUserIdAndStatus(user.getId(), MembershipStatus.ACTIVE).stream()
                        .map(m -> m.getOrganization().getId())
                        .toList())
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserMemberOf(String userEmail, Long organizationId) {
        return userQuerySPI.findByEmail(userEmail)
                .map(user -> membershipRepository.findByOrganizationIdAndUserId(organizationId, user.getId())
                        .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                        .isPresent())
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getUserRoleInOrg(String userEmail, Long organizationId) {
        return userQuerySPI.findByEmail(userEmail)
                .flatMap(user -> membershipRepository.findByOrganizationIdAndUserId(organizationId, user.getId()))
                .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                .map(m -> m.getRole().name());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSupervisorOf(String supervisorEmail, String superviseeEmail, Long organizationId) {
        Optional<User> supervisor = userQuerySPI.findByEmail(supervisorEmail);
        Optional<User> supervisee = userQuerySPI.findByEmail(superviseeEmail);

        if (supervisor.isEmpty() || supervisee.isEmpty()) return false;

        return supervisionRepository.findByOrganizationIdAndSupervisorIdAndSuperviseeId(
                organizationId, supervisor.get().getId(), supervisee.get().getId()
        ).isPresent();
    }
}

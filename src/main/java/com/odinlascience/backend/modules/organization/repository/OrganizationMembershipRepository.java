package com.odinlascience.backend.modules.organization.repository;

import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, Long> {

    List<OrganizationMembership> findByOrganizationIdOrderByRoleAscUserLastNameAsc(Long orgId);

    List<OrganizationMembership> findByUserIdAndStatus(Long userId, MembershipStatus status);

    Optional<OrganizationMembership> findByOrganizationIdAndUserId(Long orgId, Long userId);

    boolean existsByOrganizationIdAndUserId(Long orgId, Long userId);

    int countByOrganizationIdAndStatus(Long orgId, MembershipStatus status);

    int countByOrganizationIdAndRole(Long orgId, OrganizationRole role);

    void deleteByOrganizationId(Long orgId);
}

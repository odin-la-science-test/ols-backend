package com.odinlascience.backend.modules.organization.repository;

import com.odinlascience.backend.modules.organization.model.SupervisionRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupervisionRelationshipRepository extends JpaRepository<SupervisionRelationship, Long> {

    List<SupervisionRelationship> findByOrganizationId(Long orgId);

    List<SupervisionRelationship> findByOrganizationIdAndSupervisorId(Long orgId, Long supervisorId);

    List<SupervisionRelationship> findByOrganizationIdAndSuperviseeId(Long orgId, Long superviseeId);

    Optional<SupervisionRelationship> findByOrganizationIdAndSupervisorIdAndSuperviseeId(
            Long orgId, Long supervisorId, Long superviseeId);

    void deleteByOrganizationId(Long orgId);
}

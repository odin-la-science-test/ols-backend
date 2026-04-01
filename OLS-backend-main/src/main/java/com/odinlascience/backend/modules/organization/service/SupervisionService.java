package com.odinlascience.backend.modules.organization.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.modules.organization.dto.CreateSupervisionRequest;
import com.odinlascience.backend.modules.organization.dto.SupervisionDTO;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import com.odinlascience.backend.modules.organization.mapper.SupervisionMapper;
import com.odinlascience.backend.modules.organization.model.Organization;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import com.odinlascience.backend.modules.organization.model.SupervisionRelationship;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.modules.organization.repository.SupervisionRelationshipRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupervisionService {

    private final SupervisionRelationshipRepository supervisionRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final SupervisionMapper mapper;
    private final UserQuerySPI userQuerySPI;
    private final UserHelper userHelper;
    private final OrganizationService organizationService;

    @Transactional
    public SupervisionDTO create(Long orgId, CreateSupervisionRequest request, String userEmail) {
        OrganizationMembership callerMembership = organizationService.findMembership(orgId, userEmail);
        organizationService.verifyRole(callerMembership, OrganizationRole.OWNER, OrganizationRole.MANAGER);

        User supervisor = userQuerySPI.findById(request.getSupervisorId())
                .orElseThrow(() -> new ResourceNotFoundException("Superviseur introuvable"));
        User supervisee = userQuerySPI.findById(request.getSuperviseeId())
                .orElseThrow(() -> new ResourceNotFoundException("Supervise introuvable"));

        verifyActiveMember(orgId, supervisor.getId());
        verifyActiveMember(orgId, supervisee.getId());

        if (supervisionRepository.findByOrganizationIdAndSupervisorIdAndSuperviseeId(
                orgId, supervisor.getId(), supervisee.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette relation de supervision existe deja");
        }

        Organization org = organizationService.findById(orgId);

        SupervisionRelationship relationship = SupervisionRelationship.builder()
                .organization(org)
                .supervisor(supervisor)
                .supervisee(supervisee)
                .createdAt(Instant.now())
                .build();

        SupervisionRelationship saved = supervisionRepository.save(relationship);
        log.info("Supervision created: org={}, supervisor={}, supervisee={}", orgId, supervisor.getEmail(), supervisee.getEmail());
        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<SupervisionDTO> getByOrganization(Long orgId, String userEmail) {
        organizationService.findMembership(orgId, userEmail);
        return supervisionRepository.findByOrganizationId(orgId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SupervisionDTO> getMySupervisees(Long orgId, String userEmail) {
        User user = userHelper.findByEmail(userEmail);
        organizationService.findMembership(orgId, userEmail);
        return supervisionRepository.findByOrganizationIdAndSupervisorId(orgId, user.getId()).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SupervisionDTO> getMySupervisors(Long orgId, String userEmail) {
        User user = userHelper.findByEmail(userEmail);
        organizationService.findMembership(orgId, userEmail);
        return supervisionRepository.findByOrganizationIdAndSuperviseeId(orgId, user.getId()).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional
    public void delete(Long orgId, Long supervisionId, String userEmail) {
        OrganizationMembership callerMembership = organizationService.findMembership(orgId, userEmail);
        organizationService.verifyRole(callerMembership, OrganizationRole.OWNER, OrganizationRole.MANAGER);

        SupervisionRelationship relationship = supervisionRepository.findById(supervisionId)
                .filter(r -> r.getOrganization().getId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Relation de supervision introuvable"));

        supervisionRepository.delete(relationship);
        log.info("Supervision deleted: org={}, id={}", orgId, supervisionId);
    }

    private void verifyActiveMember(Long orgId, Long userId) {
        membershipRepository.findByOrganizationIdAndUserId(orgId, userId)
                .filter(m -> m.getStatus() == com.odinlascience.backend.modules.organization.enums.MembershipStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "L'utilisateur " + userId + " n'est pas un membre actif de l'organisation"));
    }
}

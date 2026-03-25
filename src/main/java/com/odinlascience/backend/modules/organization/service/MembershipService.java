package com.odinlascience.backend.modules.organization.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.event.MemberAddedEvent;
import com.odinlascience.backend.modules.common.event.MemberRemovedEvent;
import com.odinlascience.backend.modules.common.event.MemberRoleChangedEvent;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.modules.organization.dto.AddMemberRequest;
import com.odinlascience.backend.modules.organization.dto.MembershipDTO;
import com.odinlascience.backend.modules.organization.dto.UpdateMemberRoleRequest;
import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import com.odinlascience.backend.modules.organization.mapper.MembershipMapper;
import com.odinlascience.backend.modules.organization.model.Organization;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipService {

    private final OrganizationMembershipRepository membershipRepository;
    private final MembershipMapper mapper;
    private final UserHelper userHelper;
    private final UserQuerySPI userQuerySPI;
    private final OrganizationService organizationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MembershipDTO addMember(Long orgId, AddMemberRequest request, String userEmail) {
        OrganizationMembership callerMembership = organizationService.findMembership(orgId, userEmail);
        organizationService.verifyRole(callerMembership, OrganizationRole.OWNER, OrganizationRole.MANAGER);

        if (request.getRole() == OrganizationRole.OWNER) {
            organizationService.verifyRole(callerMembership, OrganizationRole.OWNER);
        }

        User targetUser = userQuerySPI.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + request.getEmail()));

        if (membershipRepository.existsByOrganizationIdAndUserId(orgId, targetUser.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet utilisateur est deja membre de l'organisation");
        }

        Organization org = organizationService.findById(orgId);

        OrganizationMembership membership = OrganizationMembership.builder()
                .organization(org)
                .user(targetUser)
                .role(request.getRole())
                .status(MembershipStatus.INVITED)
                .joinedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        OrganizationMembership saved = membershipRepository.save(membership);
        User caller = userHelper.findByEmail(userEmail);
        log.info("Member added: org={}, user={}, role={}, addedBy={}", orgId, request.getEmail(), request.getRole(), userEmail);

        eventPublisher.publishEvent(new MemberAddedEvent(
                org.getName(), orgId, targetUser.getEmail(), targetUser.getFullName(),
                request.getRole().name(), userEmail, caller.getFullName(),
                "/lab/organization"
        ));

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MembershipDTO> getMembers(Long orgId, String userEmail) {
        organizationService.findMembership(orgId, userEmail);
        return membershipRepository.findByOrganizationIdOrderByRoleAscUserLastNameAsc(orgId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional
    public MembershipDTO updateRole(Long orgId, Long membershipId, UpdateMemberRoleRequest request, String userEmail) {
        OrganizationMembership callerMembership = organizationService.findMembership(orgId, userEmail);
        organizationService.verifyRole(callerMembership, OrganizationRole.OWNER, OrganizationRole.MANAGER);

        if (request.getRole() == OrganizationRole.OWNER || request.getRole() == OrganizationRole.MANAGER) {
            organizationService.verifyRole(callerMembership, OrganizationRole.OWNER);
        }

        OrganizationMembership target = membershipRepository.findById(membershipId)
                .filter(m -> m.getOrganization().getId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Membre introuvable avec l'ID : " + membershipId));

        if (target.getRole() == OrganizationRole.OWNER && request.getRole() != OrganizationRole.OWNER) {
            verifyNotLastOwner(orgId);
        }

        String oldRole = target.getRole().name();
        target.setRole(request.getRole());
        target.setUpdatedAt(Instant.now());
        OrganizationMembership saved = membershipRepository.save(target);
        log.info("Member role changed: org={}, user={}, {} -> {}", orgId, target.getUser().getEmail(), oldRole, request.getRole());

        eventPublisher.publishEvent(new MemberRoleChangedEvent(
                target.getOrganization().getName(), orgId, target.getUser().getEmail(),
                oldRole, request.getRole().name(), userEmail, "/lab/organization"
        ));

        return mapper.toDTO(saved);
    }

    @Transactional
    public void removeMember(Long orgId, Long membershipId, String userEmail) {
        OrganizationMembership callerMembership = organizationService.findMembership(orgId, userEmail);
        organizationService.verifyRole(callerMembership, OrganizationRole.OWNER, OrganizationRole.MANAGER);

        OrganizationMembership target = membershipRepository.findById(membershipId)
                .filter(m -> m.getOrganization().getId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Membre introuvable avec l'ID : " + membershipId));

        if (target.getRole() == OrganizationRole.OWNER) {
            verifyNotLastOwner(orgId);
        }

        membershipRepository.delete(target);
        log.info("Member removed: org={}, user={}, removedBy={}", orgId, target.getUser().getEmail(), userEmail);

        eventPublisher.publishEvent(new MemberRemovedEvent(
                target.getOrganization().getName(), orgId,
                target.getUser().getEmail(), userEmail, "/lab/organization"
        ));
    }

    @Transactional
    public MembershipDTO acceptInvitation(Long orgId, String userEmail) {
        User user = userHelper.findByEmail(userEmail);
        OrganizationMembership membership = membershipRepository.findByOrganizationIdAndUserId(orgId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation introuvable"));

        if (membership.getStatus() != MembershipStatus.INVITED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pas d'invitation en attente");
        }

        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setUpdatedAt(Instant.now());
        OrganizationMembership saved = membershipRepository.save(membership);
        log.info("Invitation accepted: org={}, user={}", orgId, userEmail);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void leaveOrganization(Long orgId, String userEmail) {
        OrganizationMembership membership = organizationService.findMembership(orgId, userEmail);

        if (membership.getRole() == OrganizationRole.OWNER) {
            verifyNotLastOwner(orgId);
        }

        membershipRepository.delete(membership);
        log.info("Member left: org={}, user={}", orgId, userEmail);
    }

    private void verifyNotLastOwner(Long orgId) {
        int ownerCount = membershipRepository.countByOrganizationIdAndRole(orgId, OrganizationRole.OWNER);
        if (ownerCount <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible : dernier proprietaire de l'organisation");
        }
    }
}

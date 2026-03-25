package com.odinlascience.backend.modules.organization.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.organization.dto.CreateOrganizationRequest;
import com.odinlascience.backend.modules.organization.dto.OrganizationDTO;
import com.odinlascience.backend.modules.organization.dto.UpdateOrganizationRequest;
import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import com.odinlascience.backend.modules.organization.mapper.OrganizationMapper;
import com.odinlascience.backend.modules.organization.model.Organization;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.modules.organization.repository.OrganizationRepository;
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
public class OrganizationService {

    private final OrganizationRepository orgRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final SupervisionRelationshipRepository supervisionRepository;
    private final OrganizationMapper mapper;
    private final UserHelper userHelper;

    @Transactional
    public OrganizationDTO create(CreateOrganizationRequest request, String adminEmail) {
        User admin = userHelper.findByEmail(adminEmail);
        User owner = userHelper.findByEmail(request.getOwnerEmail());

        Organization org = Organization.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .website(request.getWebsite())
                .createdBy(admin)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Organization saved = orgRepository.save(org);

        OrganizationMembership ownership = OrganizationMembership.builder()
                .organization(saved)
                .user(owner)
                .role(OrganizationRole.OWNER)
                .status(MembershipStatus.ACTIVE)
                .joinedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        membershipRepository.save(ownership);

        log.info("Organization created: id={}, name='{}', owner={}, by admin={}", saved.getId(), saved.getName(), request.getOwnerEmail(), adminEmail);
        return mapper.toDTO(saved, 1);
    }

    @Transactional(readOnly = true)
    public List<OrganizationDTO> getAllOrganizations() {
        List<Organization> orgs = orgRepository.findAll();
        return orgs.stream()
                .map(o -> mapper.toDTO(o, membershipRepository.countByOrganizationIdAndStatus(o.getId(), MembershipStatus.ACTIVE)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrganizationDTO> getMyOrganizations(String userEmail) {
        User user = userHelper.findByEmail(userEmail);
        List<Organization> orgs = orgRepository.findByActiveMembership(user.getId());
        return orgs.stream()
                .map(o -> mapper.toDTO(o, membershipRepository.countByOrganizationIdAndStatus(o.getId(), MembershipStatus.ACTIVE)))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrganizationDTO getById(Long id, String userEmail) {
        Organization org = findOrgWithMembership(id, userEmail);
        int count = membershipRepository.countByOrganizationIdAndStatus(id, MembershipStatus.ACTIVE);
        return mapper.toDTO(org, count);
    }

    @Transactional
    public OrganizationDTO update(Long id, UpdateOrganizationRequest request, String userEmail) {
        Organization org = findOrgWithRole(id, userEmail, OrganizationRole.OWNER, OrganizationRole.MANAGER);

        if (request.getName() != null) org.setName(request.getName());
        if (request.getDescription() != null) org.setDescription(request.getDescription());
        if (request.getType() != null) org.setType(request.getType());
        if (request.getWebsite() != null) org.setWebsite(request.getWebsite());

        org.setUpdatedAt(Instant.now());
        Organization saved = orgRepository.save(org);
        int count = membershipRepository.countByOrganizationIdAndStatus(id, MembershipStatus.ACTIVE);
        log.info("Organization updated: id={}, user={}", id, userEmail);
        return mapper.toDTO(saved, count);
    }

    @Transactional
    public void delete(Long id, String adminEmail) {
        findById(id);
        supervisionRepository.deleteByOrganizationId(id);
        membershipRepository.deleteByOrganizationId(id);
        orgRepository.deleteById(id);
        log.info("Organization deleted: id={}, admin={}", id, adminEmail);
    }

    @Transactional(readOnly = true)
    public List<OrganizationDTO> search(String query, String userEmail) {
        User user = userHelper.findByEmail(userEmail);
        List<Organization> orgs = orgRepository.searchByActiveMembership(user.getId(), query);
        return orgs.stream()
                .map(o -> mapper.toDTO(o, membershipRepository.countByOrganizationIdAndStatus(o.getId(), MembershipStatus.ACTIVE)))
                .toList();
    }

    // ─── Helpers partages avec les autres services du module ───

    Organization findById(Long id) {
        return orgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation introuvable avec l'ID : " + id));
    }

    OrganizationMembership findMembership(Long orgId, String userEmail) {
        User user = userHelper.findByEmail(userEmail);
        return membershipRepository.findByOrganizationIdAndUserId(orgId, user.getId())
                .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation introuvable avec l'ID : " + orgId));
    }

    void verifyRole(OrganizationMembership membership, OrganizationRole... requiredRoles) {
        for (OrganizationRole required : requiredRoles) {
            if (membership.getRole() == required) return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Droits insuffisants pour cette action");
    }

    private Organization findOrgWithMembership(Long orgId, String userEmail) {
        Organization org = findById(orgId);
        findMembership(orgId, userEmail);
        return org;
    }

    private Organization findOrgWithRole(Long orgId, String userEmail, OrganizationRole... roles) {
        Organization org = findById(orgId);
        OrganizationMembership membership = findMembership(orgId, userEmail);
        verifyRole(membership, roles);
        return org;
    }
}

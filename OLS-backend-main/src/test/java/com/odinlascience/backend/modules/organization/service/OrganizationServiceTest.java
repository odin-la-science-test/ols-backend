package com.odinlascience.backend.modules.organization.service;

import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.organization.dto.CreateOrganizationRequest;
import com.odinlascience.backend.modules.organization.dto.OrganizationDTO;
import com.odinlascience.backend.modules.organization.dto.UpdateOrganizationRequest;
import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import com.odinlascience.backend.modules.organization.enums.OrganizationType;
import com.odinlascience.backend.modules.organization.mapper.OrganizationMapper;
import com.odinlascience.backend.modules.organization.model.Organization;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.modules.organization.repository.OrganizationRepository;
import com.odinlascience.backend.modules.organization.repository.SupervisionRelationshipRepository;
import com.odinlascience.backend.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock private OrganizationRepository orgRepository;
    @Mock private OrganizationMembershipRepository membershipRepository;
    @Mock private SupervisionRelationshipRepository supervisionRepository;
    @Mock private OrganizationMapper mapper;
    @Mock private UserHelper userHelper;

    private OrganizationService service;

    private User user;
    private Organization org;
    private OrganizationDTO dto;
    private static final String EMAIL = "user@ols.fr";

    @BeforeEach
    void setUp() {
        service = new OrganizationService(orgRepository, membershipRepository,
                supervisionRepository, mapper, userHelper);

        user = User.builder().id(1L).email(EMAIL).firstName("Jean").lastName("Dupont").build();
        org = Organization.builder().id(10L).name("Labo A").type(OrganizationType.LABORATORY)
                .createdBy(user).build();
        dto = OrganizationDTO.builder().id(10L).name("Labo A").type(OrganizationType.LABORATORY)
                .memberCount(1).createdByName("Jean Dupont").build();
    }

    @Test
    void create_CreatesOrgAndOwnerMembership_ReturnsDTO() {
        String ownerEmail = "owner@example.com";
        User owner = User.builder().id(2L).email(ownerEmail).firstName("Owner").lastName("User").build();
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("Labo A").type(OrganizationType.LABORATORY).ownerEmail(ownerEmail).build();

        when(userHelper.findByEmail(EMAIL)).thenReturn(user);
        when(userHelper.findByEmail(ownerEmail)).thenReturn(owner);
        when(orgRepository.save(any(Organization.class))).thenReturn(org);
        when(mapper.toDTO(org, 1)).thenReturn(dto);

        OrganizationDTO result = service.create(request, EMAIL);

        assertNotNull(result);
        assertEquals("Labo A", result.getName());
        verify(orgRepository).save(any(Organization.class));
        verify(membershipRepository).save(any(OrganizationMembership.class));
    }

    @Test
    void getMyOrganizations_ReturnsOrgsForUser() {
        when(userHelper.findByEmail(EMAIL)).thenReturn(user);
        when(orgRepository.findByActiveMembership(1L)).thenReturn(List.of(org));
        when(membershipRepository.countByOrganizationIdAndStatus(10L, MembershipStatus.ACTIVE)).thenReturn(1);
        when(mapper.toDTO(org, 1)).thenReturn(dto);

        List<OrganizationDTO> result = service.getMyOrganizations(EMAIL);

        assertEquals(1, result.size());
        assertEquals("Labo A", result.get(0).getName());
    }

    @Test
    void getById_ReturnsDTOWhenUserIsMember() {
        stubMembership(OrganizationRole.MEMBER);
        when(membershipRepository.countByOrganizationIdAndStatus(10L, MembershipStatus.ACTIVE)).thenReturn(3);
        when(mapper.toDTO(org, 3)).thenReturn(dto);

        OrganizationDTO result = service.getById(10L, EMAIL);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void getById_ThrowsWhenUserIsNotMember() {
        when(orgRepository.findById(10L)).thenReturn(Optional.of(org));
        when(userHelper.findByEmail(EMAIL)).thenReturn(user);
        when(membershipRepository.findByOrganizationIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> service.getById(10L, EMAIL));
    }

    @Test
    void update_UpdatesWhenUserIsOwner() {
        stubMembership(OrganizationRole.OWNER);
        UpdateOrganizationRequest request = UpdateOrganizationRequest.builder().name("Labo B").build();
        Organization updated = Organization.builder().id(10L).name("Labo B")
                .type(OrganizationType.LABORATORY).createdBy(user).build();
        OrganizationDTO updatedDTO = OrganizationDTO.builder().id(10L).name("Labo B").build();

        when(orgRepository.save(any(Organization.class))).thenReturn(updated);
        when(membershipRepository.countByOrganizationIdAndStatus(10L, MembershipStatus.ACTIVE)).thenReturn(1);
        when(mapper.toDTO(updated, 1)).thenReturn(updatedDTO);

        OrganizationDTO result = service.update(10L, request, EMAIL);

        assertEquals("Labo B", result.getName());
        verify(orgRepository).save(any(Organization.class));
    }

    @Test
    void update_ThrowsForbiddenWhenUserIsMember() {
        stubMembership(OrganizationRole.MEMBER);

        UpdateOrganizationRequest request = UpdateOrganizationRequest.builder().name("Nope").build();

        assertThrows(ResponseStatusException.class, () -> service.update(10L, request, EMAIL));
    }

    @Test
    void delete_DeletesOrgAndCascades() {
        when(orgRepository.findById(10L)).thenReturn(java.util.Optional.of(org));
        service.delete(10L, EMAIL);

        verify(supervisionRepository).deleteByOrganizationId(10L);
        verify(membershipRepository).deleteByOrganizationId(10L);
        verify(orgRepository).deleteById(10L);
    }

    @Test
    void search_ReturnsMatchingOrgs() {
        when(userHelper.findByEmail(EMAIL)).thenReturn(user);
        when(orgRepository.searchByActiveMembership(1L, "Labo")).thenReturn(List.of(org));
        when(membershipRepository.countByOrganizationIdAndStatus(10L, MembershipStatus.ACTIVE)).thenReturn(1);
        when(mapper.toDTO(org, 1)).thenReturn(dto);

        List<OrganizationDTO> result = service.search("Labo", EMAIL);

        assertEquals(1, result.size());
        assertEquals("Labo A", result.get(0).getName());
    }

    // ─── Helpers ────────────────────────────────────────────────

    private OrganizationMembership stubMembership(OrganizationRole role) {
        OrganizationMembership membership = OrganizationMembership.builder()
                .id(100L).organization(org).user(user).role(role)
                .status(MembershipStatus.ACTIVE).build();

        when(orgRepository.findById(10L)).thenReturn(Optional.of(org));
        when(userHelper.findByEmail(EMAIL)).thenReturn(user);
        when(membershipRepository.findByOrganizationIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(membership));
        return membership;
    }
}

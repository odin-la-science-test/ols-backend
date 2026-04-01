package com.odinlascience.backend.modules.organization.service;

import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.modules.organization.dto.CreateSupervisionRequest;
import com.odinlascience.backend.modules.organization.dto.SupervisionDTO;
import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import com.odinlascience.backend.modules.organization.mapper.SupervisionMapper;
import com.odinlascience.backend.modules.organization.model.Organization;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import com.odinlascience.backend.modules.organization.model.SupervisionRelationship;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.modules.organization.repository.SupervisionRelationshipRepository;
import com.odinlascience.backend.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupervisionServiceTest {

    @Mock private SupervisionRelationshipRepository supervisionRepository;
    @Mock private OrganizationMembershipRepository membershipRepository;
    @Mock private SupervisionMapper mapper;
    @Mock private UserQuerySPI userQuerySPI;
    @Mock private UserHelper userHelper;
    @Mock private OrganizationService organizationService;

    @InjectMocks private SupervisionService supervisionService;

    private static final Long ORG_ID = 1L;
    private static final String CALLER_EMAIL = "manager@example.com";

    private User supervisor;
    private User supervisee;
    private Organization organization;
    private OrganizationMembership callerMembership;
    private SupervisionRelationship relationship;
    private SupervisionDTO dto;

    @BeforeEach
    void setUp() {
        supervisor = User.builder().id(10L).email("supervisor@example.com")
                .firstName("Super").lastName("Visor").build();
        supervisee = User.builder().id(20L).email("supervisee@example.com")
                .firstName("Super").lastName("Visee").build();
        organization = Organization.builder().id(ORG_ID).name("Labo Test").build();

        callerMembership = OrganizationMembership.builder()
                .id(1L).organization(organization)
                .user(User.builder().id(5L).email(CALLER_EMAIL).build())
                .role(OrganizationRole.MANAGER).status(MembershipStatus.ACTIVE)
                .build();

        relationship = SupervisionRelationship.builder()
                .id(100L).organization(organization)
                .supervisor(supervisor).supervisee(supervisee)
                .createdAt(Instant.now()).build();

        dto = SupervisionDTO.builder()
                .id(100L).organizationId(ORG_ID)
                .supervisorId(10L).supervisorName("Super Visor")
                .superviseeId(20L).superviseeName("Super Visee")
                .createdAt(relationship.getCreatedAt()).build();
    }

    @Test
    void create_success() {
        CreateSupervisionRequest request = new CreateSupervisionRequest(10L, 20L);
        lenient().when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        lenient().doNothing().when(organizationService).verifyRole(eq(callerMembership), any());
        when(userQuerySPI.findById(10L)).thenReturn(Optional.of(supervisor));
        when(userQuerySPI.findById(20L)).thenReturn(Optional.of(supervisee));

        OrganizationMembership activeMembership = OrganizationMembership.builder()
                .status(MembershipStatus.ACTIVE).build();
        when(membershipRepository.findByOrganizationIdAndUserId(ORG_ID, 10L))
                .thenReturn(Optional.of(activeMembership));
        when(membershipRepository.findByOrganizationIdAndUserId(ORG_ID, 20L))
                .thenReturn(Optional.of(activeMembership));

        when(supervisionRepository.findByOrganizationIdAndSupervisorIdAndSuperviseeId(ORG_ID, 10L, 20L))
                .thenReturn(Optional.empty());
        when(organizationService.findById(ORG_ID)).thenReturn(organization);
        when(supervisionRepository.save(any())).thenReturn(relationship);
        when(mapper.toDTO(relationship)).thenReturn(dto);

        SupervisionDTO result = supervisionService.create(ORG_ID, request, CALLER_EMAIL);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getSupervisorId()).isEqualTo(10L);
        verify(supervisionRepository).save(any());
    }

    @Test
    void create_throwsConflict_whenAlreadyExists() {
        CreateSupervisionRequest request = new CreateSupervisionRequest(10L, 20L);
        lenient().when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        lenient().doNothing().when(organizationService).verifyRole(eq(callerMembership), any());
        when(userQuerySPI.findById(10L)).thenReturn(Optional.of(supervisor));
        when(userQuerySPI.findById(20L)).thenReturn(Optional.of(supervisee));

        OrganizationMembership activeMembership = OrganizationMembership.builder()
                .status(MembershipStatus.ACTIVE).build();
        when(membershipRepository.findByOrganizationIdAndUserId(ORG_ID, 10L))
                .thenReturn(Optional.of(activeMembership));
        when(membershipRepository.findByOrganizationIdAndUserId(ORG_ID, 20L))
                .thenReturn(Optional.of(activeMembership));
        when(supervisionRepository.findByOrganizationIdAndSupervisorIdAndSuperviseeId(ORG_ID, 10L, 20L))
                .thenReturn(Optional.of(relationship));

        assertThatThrownBy(() -> supervisionService.create(ORG_ID, request, CALLER_EMAIL))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("existe deja");
    }

    @Test
    void create_throwsBadRequest_whenUserNotActiveMember() {
        CreateSupervisionRequest request = new CreateSupervisionRequest(10L, 20L);
        lenient().when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        lenient().doNothing().when(organizationService).verifyRole(eq(callerMembership), any());
        when(userQuerySPI.findById(10L)).thenReturn(Optional.of(supervisor));
        when(userQuerySPI.findById(20L)).thenReturn(Optional.of(supervisee));

        when(membershipRepository.findByOrganizationIdAndUserId(ORG_ID, 10L))
                .thenReturn(Optional.of(OrganizationMembership.builder()
                        .status(MembershipStatus.INVITED).build()));

        assertThatThrownBy(() -> supervisionService.create(ORG_ID, request, CALLER_EMAIL))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("n'est pas un membre actif");
    }

    @Test
    void getByOrganization_returnsSupervisions() {
        lenient().when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(supervisionRepository.findByOrganizationId(ORG_ID)).thenReturn(List.of(relationship));
        when(mapper.toDTO(relationship)).thenReturn(dto);

        List<SupervisionDTO> result = supervisionService.getByOrganization(ORG_ID, CALLER_EMAIL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
    }

    @Test
    void getMySupervisees_returnsSupervisions() {
        User caller = User.builder().id(10L).email(CALLER_EMAIL).build();
        when(userHelper.findByEmail(CALLER_EMAIL)).thenReturn(caller);
        lenient().when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(supervisionRepository.findByOrganizationIdAndSupervisorId(ORG_ID, 10L))
                .thenReturn(List.of(relationship));
        when(mapper.toDTO(relationship)).thenReturn(dto);

        List<SupervisionDTO> result = supervisionService.getMySupervisees(ORG_ID, CALLER_EMAIL);

        assertThat(result).hasSize(1);
    }

    @Test
    void getMySupervisors_returnsSupervisions() {
        User caller = User.builder().id(20L).email(CALLER_EMAIL).build();
        when(userHelper.findByEmail(CALLER_EMAIL)).thenReturn(caller);
        lenient().when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(supervisionRepository.findByOrganizationIdAndSuperviseeId(ORG_ID, 20L))
                .thenReturn(List.of(relationship));
        when(mapper.toDTO(relationship)).thenReturn(dto);

        List<SupervisionDTO> result = supervisionService.getMySupervisors(ORG_ID, CALLER_EMAIL);

        assertThat(result).hasSize(1);
    }

    @Test
    void delete_success() {
        lenient().when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        lenient().doNothing().when(organizationService).verifyRole(eq(callerMembership), any());
        when(supervisionRepository.findById(100L)).thenReturn(Optional.of(relationship));

        supervisionService.delete(ORG_ID, 100L, CALLER_EMAIL);

        verify(supervisionRepository).delete(relationship);
    }

    @Test
    void delete_throwsForbidden_whenCallerIsMember() {
        OrganizationMembership memberMembership = OrganizationMembership.builder()
                .role(OrganizationRole.MEMBER).status(MembershipStatus.ACTIVE).build();
        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(memberMembership);
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Droits insuffisants"))
                .when(organizationService).verifyRole(eq(memberMembership), eq(OrganizationRole.OWNER), eq(OrganizationRole.MANAGER));

        assertThatThrownBy(() -> supervisionService.delete(ORG_ID, 100L, CALLER_EMAIL))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Droits insuffisants");
    }
}

package com.odinlascience.backend.modules.organization.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock private OrganizationMembershipRepository membershipRepository;
    @Mock private MembershipMapper mapper;
    @Mock private UserHelper userHelper;
    @Mock private UserQuerySPI userQuerySPI;
    @Mock private OrganizationService organizationService;
    @Mock private ApplicationEventPublisher eventPublisher;

    private MembershipService service;

    private Organization org;
    private User caller;
    private User target;
    private OrganizationMembership callerMembership;
    private MembershipDTO dummyDTO;

    private static final Long ORG_ID = 1L;
    private static final Long MEMBERSHIP_ID = 10L;
    private static final String CALLER_EMAIL = "caller@example.com";
    private static final String TARGET_EMAIL = "target@example.com";

    @BeforeEach
    void setUp() {
        service = new MembershipService(membershipRepository, mapper, userHelper,
                userQuerySPI, organizationService, eventPublisher);

        org = Organization.builder().id(ORG_ID).name("Test Org").build();
        caller = User.builder().id(1L).email(CALLER_EMAIL).firstName("Caller").lastName("User").build();
        target = User.builder().id(2L).email(TARGET_EMAIL).firstName("Target").lastName("User").build();

        callerMembership = OrganizationMembership.builder()
                .id(5L).organization(org).user(caller)
                .role(OrganizationRole.OWNER).status(MembershipStatus.ACTIVE).build();

        dummyDTO = MembershipDTO.builder().id(MEMBERSHIP_ID).build();
    }

    // ─── addMember ──────────────────────────────────────────────

    @Test
    void addMember_success() {
        AddMemberRequest request = new AddMemberRequest(TARGET_EMAIL, OrganizationRole.MEMBER);
        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(userQuerySPI.findByEmail(TARGET_EMAIL)).thenReturn(Optional.of(target));
        when(membershipRepository.existsByOrganizationIdAndUserId(ORG_ID, target.getId())).thenReturn(false);
        when(organizationService.findById(ORG_ID)).thenReturn(org);
        when(membershipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userHelper.findByEmail(CALLER_EMAIL)).thenReturn(caller);
        when(mapper.toDTO(any(OrganizationMembership.class))).thenReturn(dummyDTO);

        MembershipDTO result = service.addMember(ORG_ID, request, CALLER_EMAIL);

        assertNotNull(result);
        verify(membershipRepository).save(any(OrganizationMembership.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void addMember_conflict_whenAlreadyMember() {
        AddMemberRequest request = new AddMemberRequest(TARGET_EMAIL, OrganizationRole.MEMBER);
        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(userQuerySPI.findByEmail(TARGET_EMAIL)).thenReturn(Optional.of(target));
        when(membershipRepository.existsByOrganizationIdAndUserId(ORG_ID, target.getId())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.addMember(ORG_ID, request, CALLER_EMAIL));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void addMember_forbidden_whenCallerIsMember() {
        callerMembership.setRole(OrganizationRole.MEMBER);
        AddMemberRequest request = new AddMemberRequest(TARGET_EMAIL, OrganizationRole.MEMBER);
        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                .when(organizationService).verifyRole(callerMembership, OrganizationRole.OWNER, OrganizationRole.MANAGER);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.addMember(ORG_ID, request, CALLER_EMAIL));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    // ─── getMembers ─────────────────────────────────────────────

    @Test
    void getMembers_returnsMembers() {
        OrganizationMembership m1 = OrganizationMembership.builder().id(1L).build();
        OrganizationMembership m2 = OrganizationMembership.builder().id(2L).build();
        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(membershipRepository.findByOrganizationIdOrderByRoleAscUserLastNameAsc(ORG_ID))
                .thenReturn(List.of(m1, m2));
        when(mapper.toDTO(any(OrganizationMembership.class))).thenReturn(dummyDTO);

        List<MembershipDTO> result = service.getMembers(ORG_ID, CALLER_EMAIL);

        assertEquals(2, result.size());
    }

    // ─── updateRole ─────────────────────────────────────────────

    @Test
    void updateRole_success() {
        UpdateMemberRoleRequest request = new UpdateMemberRoleRequest(OrganizationRole.MEMBER);
        OrganizationMembership targetMembership = OrganizationMembership.builder()
                .id(MEMBERSHIP_ID).organization(org).user(target)
                .role(OrganizationRole.MEMBER).status(MembershipStatus.ACTIVE).build();

        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(membershipRepository.findById(MEMBERSHIP_ID)).thenReturn(Optional.of(targetMembership));
        when(membershipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDTO(any(OrganizationMembership.class))).thenReturn(dummyDTO);

        MembershipDTO result = service.updateRole(ORG_ID, MEMBERSHIP_ID, request, CALLER_EMAIL);

        assertNotNull(result);
        verify(membershipRepository).save(any());
    }

    @Test
    void updateRole_throws_whenDemotingLastOwner() {
        UpdateMemberRoleRequest request = new UpdateMemberRoleRequest(OrganizationRole.MEMBER);
        OrganizationMembership ownerMembership = OrganizationMembership.builder()
                .id(MEMBERSHIP_ID).organization(org).user(target)
                .role(OrganizationRole.OWNER).status(MembershipStatus.ACTIVE).build();

        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(membershipRepository.findById(MEMBERSHIP_ID)).thenReturn(Optional.of(ownerMembership));
        when(membershipRepository.countByOrganizationIdAndRole(ORG_ID, OrganizationRole.OWNER)).thenReturn(1);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.updateRole(ORG_ID, MEMBERSHIP_ID, request, CALLER_EMAIL));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // ─── removeMember ───────────────────────────────────────────

    @Test
    void removeMember_success() {
        OrganizationMembership targetMembership = OrganizationMembership.builder()
                .id(MEMBERSHIP_ID).organization(org).user(target)
                .role(OrganizationRole.MEMBER).status(MembershipStatus.ACTIVE).build();

        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(membershipRepository.findById(MEMBERSHIP_ID)).thenReturn(Optional.of(targetMembership));

        service.removeMember(ORG_ID, MEMBERSHIP_ID, CALLER_EMAIL);

        verify(membershipRepository).delete(targetMembership);
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void removeMember_throws_whenRemovingLastOwner() {
        OrganizationMembership ownerMembership = OrganizationMembership.builder()
                .id(MEMBERSHIP_ID).organization(org).user(target)
                .role(OrganizationRole.OWNER).status(MembershipStatus.ACTIVE).build();

        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(callerMembership);
        when(membershipRepository.findById(MEMBERSHIP_ID)).thenReturn(Optional.of(ownerMembership));
        when(membershipRepository.countByOrganizationIdAndRole(ORG_ID, OrganizationRole.OWNER)).thenReturn(1);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.removeMember(ORG_ID, MEMBERSHIP_ID, CALLER_EMAIL));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // ─── acceptInvitation ───────────────────────────────────────

    @Test
    void acceptInvitation_success() {
        OrganizationMembership invitedMembership = OrganizationMembership.builder()
                .id(MEMBERSHIP_ID).organization(org).user(target)
                .role(OrganizationRole.MEMBER).status(MembershipStatus.INVITED).build();

        when(userHelper.findByEmail(TARGET_EMAIL)).thenReturn(target);
        when(membershipRepository.findByOrganizationIdAndUserId(ORG_ID, target.getId()))
                .thenReturn(Optional.of(invitedMembership));
        when(membershipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDTO(any(OrganizationMembership.class))).thenReturn(dummyDTO);

        MembershipDTO result = service.acceptInvitation(ORG_ID, TARGET_EMAIL);

        assertNotNull(result);
        assertEquals(MembershipStatus.ACTIVE, invitedMembership.getStatus());
    }

    @Test
    void acceptInvitation_throws_whenNotInvited() {
        OrganizationMembership activeMembership = OrganizationMembership.builder()
                .id(MEMBERSHIP_ID).organization(org).user(target)
                .role(OrganizationRole.MEMBER).status(MembershipStatus.ACTIVE).build();

        when(userHelper.findByEmail(TARGET_EMAIL)).thenReturn(target);
        when(membershipRepository.findByOrganizationIdAndUserId(ORG_ID, target.getId()))
                .thenReturn(Optional.of(activeMembership));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.acceptInvitation(ORG_ID, TARGET_EMAIL));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // ─── leaveOrganization ──────────────────────────────────────

    @Test
    void leaveOrganization_success() {
        OrganizationMembership memberMembership = OrganizationMembership.builder()
                .id(MEMBERSHIP_ID).organization(org).user(caller)
                .role(OrganizationRole.MEMBER).status(MembershipStatus.ACTIVE).build();

        when(organizationService.findMembership(ORG_ID, CALLER_EMAIL)).thenReturn(memberMembership);

        service.leaveOrganization(ORG_ID, CALLER_EMAIL);

        verify(membershipRepository).delete(memberMembership);
    }
}

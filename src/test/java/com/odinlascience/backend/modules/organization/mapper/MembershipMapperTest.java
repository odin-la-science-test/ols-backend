package com.odinlascience.backend.modules.organization.mapper;

import com.odinlascience.backend.modules.organization.dto.MembershipDTO;
import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import com.odinlascience.backend.modules.organization.model.Organization;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import com.odinlascience.backend.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MembershipMapperTest {

    @InjectMocks
    private MembershipMapper mapper;

    @Test
    void toDTO_mapsAllFields() {
        Organization org = Organization.builder()
                .id(1L).name("Labo Pasteur").build();

        User user = User.builder()
                .id(10L).email("membre@example.com")
                .firstName("Louis").lastName("Pasteur")
                .build();

        Instant joinedAt = Instant.now();
        OrganizationMembership entity = OrganizationMembership.builder()
                .id(100L)
                .organization(org)
                .user(user)
                .role(OrganizationRole.MANAGER)
                .status(MembershipStatus.ACTIVE)
                .joinedAt(joinedAt)
                .build();

        MembershipDTO result = mapper.toDTO(entity);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getOrganizationId()).isEqualTo(1L);
        assertThat(result.getOrganizationName()).isEqualTo("Labo Pasteur");
        assertThat(result.getUserId()).isEqualTo(10L);
        assertThat(result.getUserEmail()).isEqualTo("membre@example.com");
        assertThat(result.getUserFullName()).isEqualTo("Louis Pasteur");
        assertThat(result.getRole()).isEqualTo(OrganizationRole.MANAGER);
        assertThat(result.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
        assertThat(result.getJoinedAt()).isEqualTo(joinedAt);
    }

    @Test
    void toDTO_handlesInvitedStatus() {
        Organization org = Organization.builder()
                .id(2L).name("Hopital").build();

        User user = User.builder()
                .id(20L).email("invite@example.com")
                .firstName("Marie").lastName("Curie")
                .build();

        OrganizationMembership entity = OrganizationMembership.builder()
                .id(200L)
                .organization(org)
                .user(user)
                .role(OrganizationRole.INTERN)
                .status(MembershipStatus.INVITED)
                .joinedAt(Instant.now())
                .build();

        MembershipDTO result = mapper.toDTO(entity);

        assertThat(result.getStatus()).isEqualTo(MembershipStatus.INVITED);
        assertThat(result.getRole()).isEqualTo(OrganizationRole.INTERN);
        assertThat(result.getUserFullName()).isEqualTo("Marie Curie");
    }
}

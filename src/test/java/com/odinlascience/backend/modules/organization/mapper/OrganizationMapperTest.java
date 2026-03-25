package com.odinlascience.backend.modules.organization.mapper;

import com.odinlascience.backend.modules.organization.dto.OrganizationDTO;
import com.odinlascience.backend.modules.organization.enums.OrganizationType;
import com.odinlascience.backend.modules.organization.model.Organization;
import com.odinlascience.backend.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrganizationMapperTest {

    @InjectMocks
    private OrganizationMapper mapper;

    @Test
    void toDTO_mapsAllFields() {
        User creator = User.builder()
                .id(1L).email("creator@example.com")
                .firstName("Jean").lastName("Dupont")
                .build();

        Instant now = Instant.now();
        Organization entity = Organization.builder()
                .id(10L)
                .name("Labo Pasteur")
                .description("Laboratoire de recherche")
                .type(OrganizationType.LABORATORY)
                .website("https://pasteur.fr")
                .createdBy(creator)
                .createdAt(now)
                .build();

        OrganizationDTO result = mapper.toDTO(entity, 5);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Labo Pasteur");
        assertThat(result.getDescription()).isEqualTo("Laboratoire de recherche");
        assertThat(result.getType()).isEqualTo(OrganizationType.LABORATORY);
        assertThat(result.getWebsite()).isEqualTo("https://pasteur.fr");
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.getMemberCount()).isEqualTo(5);
        assertThat(result.getCreatedByName()).isEqualTo("Jean Dupont");
    }

    @Test
    void toDTO_handlesNullWebsite() {
        User creator = User.builder()
                .id(1L).email("creator@example.com")
                .firstName("Marie").lastName("Curie")
                .build();

        Organization entity = Organization.builder()
                .id(20L)
                .name("Universite")
                .type(OrganizationType.UNIVERSITY)
                .createdBy(creator)
                .createdAt(Instant.now())
                .build();

        OrganizationDTO result = mapper.toDTO(entity, 0);

        assertThat(result.getWebsite()).isNull();
        assertThat(result.getDescription()).isNull();
        assertThat(result.getMemberCount()).isZero();
        assertThat(result.getCreatedByName()).isEqualTo("Marie Curie");
    }
}

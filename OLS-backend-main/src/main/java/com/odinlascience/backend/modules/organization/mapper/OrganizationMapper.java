package com.odinlascience.backend.modules.organization.mapper;

import com.odinlascience.backend.modules.organization.dto.OrganizationDTO;
import com.odinlascience.backend.modules.organization.model.Organization;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    public OrganizationDTO toDTO(Organization entity, int memberCount) {
        return OrganizationDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType())
                .website(entity.getWebsite())
                .createdAt(entity.getCreatedAt())
                .memberCount(memberCount)
                .createdByName(entity.getCreatedBy().getFullName())
                .build();
    }
}

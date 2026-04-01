package com.odinlascience.backend.modules.organization.mapper;

import com.odinlascience.backend.modules.organization.dto.MembershipDTO;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import org.springframework.stereotype.Component;

@Component
public class MembershipMapper {

    public MembershipDTO toDTO(OrganizationMembership entity) {
        return MembershipDTO.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganization().getId())
                .organizationName(entity.getOrganization().getName())
                .userId(entity.getUser().getId())
                .userEmail(entity.getUser().getEmail())
                .userFullName(entity.getUser().getFullName())
                .userAvatarId(entity.getUser().getAvatarId())
                .role(entity.getRole())
                .status(entity.getStatus())
                .joinedAt(entity.getJoinedAt())
                .build();
    }
}

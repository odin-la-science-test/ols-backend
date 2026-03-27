package com.odinlascience.backend.modules.organization.mapper;

import com.odinlascience.backend.modules.organization.dto.SupervisionDTO;
import com.odinlascience.backend.modules.organization.model.SupervisionRelationship;
import org.springframework.stereotype.Component;

@Component
public class SupervisionMapper {

    public SupervisionDTO toDTO(SupervisionRelationship entity) {
        return SupervisionDTO.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganization().getId())
                .supervisorId(entity.getSupervisor().getId())
                .supervisorName(entity.getSupervisor().getFullName())
                .supervisorAvatarId(entity.getSupervisor().getAvatarId())
                .superviseeId(entity.getSupervisee().getId())
                .superviseeName(entity.getSupervisee().getFullName())
                .superviseeAvatarId(entity.getSupervisee().getAvatarId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

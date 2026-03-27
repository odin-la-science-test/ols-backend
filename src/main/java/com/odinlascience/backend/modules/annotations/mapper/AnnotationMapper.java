package com.odinlascience.backend.modules.annotations.mapper;

import com.odinlascience.backend.modules.annotations.dto.AnnotationDTO;
import com.odinlascience.backend.modules.annotations.model.Annotation;
import org.springframework.stereotype.Component;

/**
 * Mapper Annotation -> AnnotationDTO.
 */
@Component
public class AnnotationMapper {

    public AnnotationDTO toDTO(Annotation entity) {
        if (entity == null) return null;

        String ownerName = entity.getOwner() != null ? entity.getOwner().getFullName() : "";

        return AnnotationDTO.builder()
                .id(entity.getId())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .content(entity.getContent())
                .color(entity.getColor())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .ownerName(ownerName)
                .build();
    }
}

package com.odinlascience.backend.modules.studycollections.mapper;

import com.odinlascience.backend.modules.studycollections.dto.StudyCollectionDTO;
import com.odinlascience.backend.modules.studycollections.dto.StudyCollectionItemDTO;
import com.odinlascience.backend.modules.studycollections.model.StudyCollection;
import com.odinlascience.backend.modules.studycollections.model.StudyCollectionItem;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper StudyCollection/StudyCollectionItem <-> DTOs.
 */
@Component
public class StudyCollectionMapper {

    public StudyCollectionDTO toDTO(StudyCollection entity) {
        if (entity == null) return null;

        String ownerName = entity.getOwner() != null ? entity.getOwner().getFullName() : "";

        List<StudyCollectionItemDTO> itemDTOs = entity.getItems() != null
                ? entity.getItems().stream().map(this::toItemDTO).toList()
                : List.of();

        return StudyCollectionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .ownerName(ownerName)
                .items(itemDTOs)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public StudyCollectionItemDTO toItemDTO(StudyCollectionItem entity) {
        if (entity == null) return null;

        return StudyCollectionItemDTO.builder()
                .id(entity.getId())
                .moduleId(entity.getModuleId())
                .entityId(entity.getEntityId())
                .notes(entity.getNotes())
                .addedAt(entity.getAddedAt())
                .build();
    }
}

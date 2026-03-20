package com.odinlascience.backend.modules.notes.mapper;

import com.odinlascience.backend.modules.notes.dto.NoteDTO;
import com.odinlascience.backend.modules.notes.model.Note;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mapper Note <-> NoteDTO.
 */
@Component
public class NoteMapper {

    public NoteDTO toDTO(Note entity) {
        if (entity == null) return null;

        String ownerName = "";
        if (entity.getOwner() != null) {
            ownerName = entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName();
        }

        return NoteDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .color(entity.getColor())
                .pinned(entity.getPinned())
                .tags(parseTags(entity.getTags()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .ownerName(ownerName.trim())
                .build();
    }

    /** Convertit une string CSV de tags en liste */
    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /** Convertit une liste de tags en string CSV pour stockage */
    public static String tagsToString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return String.join(",", tags);
    }
}

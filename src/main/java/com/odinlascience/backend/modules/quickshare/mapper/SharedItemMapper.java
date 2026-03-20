package com.odinlascience.backend.modules.quickshare.mapper;

import com.odinlascience.backend.modules.quickshare.dto.SharedFileDTO;
import com.odinlascience.backend.modules.quickshare.dto.SharedItemDTO;
import com.odinlascience.backend.modules.quickshare.model.SharedFile;
import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper SharedItem <-> SharedItemDTO.
 */
@Component
public class SharedItemMapper {

    public SharedItemDTO toDTO(SharedItem entity) {
        if (entity == null) return null;

        String ownerName = "";
        if (entity.getOwner() != null) {
            ownerName = entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName();
        }

        List<SharedFileDTO> fileDTOs = entity.getFiles() != null
                ? entity.getFiles().stream().map(this::toFileDTO).toList()
                : Collections.emptyList();

        return SharedItemDTO.builder()
                .id(entity.getId())
                .shareCode(entity.getShareCode())
                .title(entity.getTitle())
                .type(entity.getType())
                .textContent(entity.getTextContent())
                .files(fileDTOs)
                .downloadCount(entity.getDownloadCount())
                .maxDownloads(entity.getMaxDownloads())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .ownerName(ownerName.trim())
                .recipientEmail(entity.getRecipientEmail())
                .expired(entity.isExpired())
                .downloadLimitReached(entity.isDownloadLimitReached())
                .shareUrl("/api/quickshare/d/" + entity.getShareCode())
                .build();
    }

    private SharedFileDTO toFileDTO(SharedFile file) {
        return SharedFileDTO.builder()
                .id(file.getId())
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getFileSize())
                .build();
    }
}

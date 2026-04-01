package com.odinlascience.backend.modules.quickshare.dto;

import com.odinlascience.backend.modules.quickshare.enums.ShareType;
import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * DTO de réponse pour un élément partagé.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedItemDTO {

    private Long id;
    private String shareCode;
    private String title;
    private ShareType type;

    // TEXT
    private String textContent;

    // FILE(s)
    private List<SharedFileDTO> files;

    // Metadata
    private Integer downloadCount;
    private Integer maxDownloads;
    private Instant expiresAt;
    private Instant createdAt;

    // Owner info
    private String ownerName;

    // Recipient (direct share)
    private String recipientEmail;

    // Computed
    private boolean expired;
    private boolean downloadLimitReached;
    private String shareUrl;
}

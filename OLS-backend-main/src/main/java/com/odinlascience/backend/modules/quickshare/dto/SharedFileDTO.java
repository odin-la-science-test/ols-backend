package com.odinlascience.backend.modules.quickshare.dto;

import lombok.*;

/**
 * DTO pour un fichier attaché à un partage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedFileDTO {
    private Long id;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
}

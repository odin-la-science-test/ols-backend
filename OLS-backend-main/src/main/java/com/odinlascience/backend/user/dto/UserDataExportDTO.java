package com.odinlascience.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTO pour l'export RGPD des donnees utilisateur.
 * Contient toutes les donnees personnelles associees a un compte.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataExportDTO {

    private Instant exportDate;
    private ProfileData profile;
    private List<SessionData> sessions;
    private List<Map<String, Object>> contacts;
    private List<Map<String, Object>> notes;
    private List<Map<String, Object>> annotations;
    private List<Map<String, Object>> notifications;
    private List<Map<String, Object>> history;
    private List<Map<String, Object>> organizations;
    private List<Map<String, Object>> studyCollections;
    private List<Map<String, Object>> sharedItems;
    private List<Map<String, Object>> supportTickets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileData {
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private String authProvider;
        private boolean emailVerified;
        private String avatarId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionData {
        private String deviceInfo;
        private String ipAddress;
        private Instant lastActiveAt;
        private Instant createdAt;
    }
}

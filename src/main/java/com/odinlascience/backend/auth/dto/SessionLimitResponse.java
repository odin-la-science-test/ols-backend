package com.odinlascience.backend.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SessionLimitResponse {
    private String message;
    private List<SessionDTO> activeSessions;
    private int maxSessions;
}

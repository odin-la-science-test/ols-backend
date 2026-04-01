package com.odinlascience.backend.exception;

import com.odinlascience.backend.auth.dto.SessionDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class SessionLimitExceededException extends RuntimeException {

    private final List<SessionDTO> activeSessions;
    private final int maxSessions;

    public SessionLimitExceededException(List<SessionDTO> activeSessions, int maxSessions) {
        super("Limite de sessions atteinte (" + maxSessions + " max)");
        this.activeSessions = activeSessions;
        this.maxSessions = maxSessions;
    }
}

package com.odinlascience.backend.auth.mapper;

import com.odinlascience.backend.auth.dto.SessionDTO;
import com.odinlascience.backend.auth.model.UserSession;

import java.util.List;
import java.util.UUID;

public final class SessionMapper {

    private SessionMapper() {}

    public static SessionDTO toDTO(UserSession session, UUID currentSessionId) {
        return SessionDTO.builder()
                .id(session.getId())
                .deviceInfo(session.getDeviceInfo())
                .ipAddress(session.getIpAddress())
                .lastActiveAt(session.getLastActiveAt())
                .createdAt(session.getCreatedAt())
                .current(session.getId().equals(currentSessionId))
                .build();
    }

    public static List<SessionDTO> toDTOList(List<UserSession> sessions, UUID currentSessionId) {
        return sessions.stream()
                .map(s -> toDTO(s, currentSessionId))
                .toList();
    }
}

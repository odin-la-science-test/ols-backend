package com.odinlascience.backend.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class SessionDTO {
    private UUID id;
    private String deviceInfo;
    private String ipAddress;
    private Instant lastActiveAt;
    private Instant createdAt;
    private boolean current;
}

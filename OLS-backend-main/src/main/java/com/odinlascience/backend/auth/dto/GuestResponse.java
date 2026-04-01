package com.odinlascience.backend.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestResponse {
    private long expiresIn;
    private GuestUser user;

    @Data
    @Builder
    public static class GuestUser {
        private String id;
        private String role;
    }
}

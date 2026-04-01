package com.odinlascience.backend.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private long expiresIn;
}

package com.odinlascience.backend.auth.dto;

import com.odinlascience.backend.user.dto.UserDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private long expiresIn;
    private UserDTO user;
}

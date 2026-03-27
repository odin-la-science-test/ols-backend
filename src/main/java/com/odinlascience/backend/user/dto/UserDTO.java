package com.odinlascience.backend.user.dto;

import com.odinlascience.backend.user.enums.AuthProvider;
import com.odinlascience.backend.user.enums.RoleType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private RoleType role;
    private String avatarId;
    private AuthProvider authProvider;
    private boolean emailVerified;
}
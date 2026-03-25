package com.odinlascience.backend.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CasUserInfo {
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
}

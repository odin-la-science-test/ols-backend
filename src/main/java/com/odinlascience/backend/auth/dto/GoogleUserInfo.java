package com.odinlascience.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleUserInfo {
    private String sub;
    private String email;
    @JsonProperty("given_name")
    private String givenName;
    @JsonProperty("family_name")
    private String familyName;
    private String picture;
    @JsonProperty("email_verified")
    private boolean emailVerified;
}

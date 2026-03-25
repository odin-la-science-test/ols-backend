package com.odinlascience.backend.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuthProperties {

    private Google google = new Google();
    private Cas cas = new Cas();
    private String frontendUrl = "http://localhost:3000";

    @Data
    public static class Google {
        private String clientId = "PLACEHOLDER_GOOGLE_CLIENT_ID";
        private String clientSecret = "PLACEHOLDER_GOOGLE_CLIENT_SECRET";
        private String redirectUri = "http://localhost:8080/api/auth/oauth2/google/callback";
        private String[] scopes = {"openid", "email", "profile"};
    }

    @Data
    public static class Cas {
        private String serverUrl = "https://cas.univ-lille.fr/cas";
        private String serviceUrl = "http://localhost:8080/api/auth/cas/callback";
    }
}

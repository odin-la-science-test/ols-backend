package com.odinlascience.backend.security;

import com.odinlascience.backend.security.service.JwtService;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestSecuritySupport {

    private TestSecuritySupport() {}

    public static String bearer(JwtService jwtService) {
        return jwtService.generateAccessToken(TestSecurityConfig.createTestUser());
    }

    public static RequestPostProcessor authHeader(JwtService jwtService) {
        String token = bearer(jwtService);
        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }
}

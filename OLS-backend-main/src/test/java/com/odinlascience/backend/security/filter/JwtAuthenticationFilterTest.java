package com.odinlascience.backend.security.filter;

import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("filtertest@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Filter")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .build();
        testUser = userRepository.save(testUser);

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User.withUsername(testUser.getEmail())
                        .password(testUser.getPassword())
                        .roles("STUDENT")
                        .build();
        validToken = jwtService.generateAccessToken(userDetails);
    }

    @Test
    void request_WithValidToken_IsAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void request_WithNoToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_WithInvalidToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_WithMalformedHeader_Returns401() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "NotBearer " + validToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicEndpoint_WithNoToken_IsAccessible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void request_WithExpiredToken_Returns401() throws Exception {
        // Use a token that's structurally valid but will fail validation
        // This tests the case where extractSubject succeeds but isTokenValid returns false
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiYWNjZXNzIiwic3ViIjoiZmlsdGVydGVzdEBleGFtcGxlLmNvbSIsImlhdCI6MTYwMDAwMDAwMCwiZXhwIjoxNjAwMDAwMDAxfQ.invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_WithRefreshTokenInsteadOfAccess_IsStillAuthenticated() throws Exception {
        // Refresh token should also authenticate (filter doesn't check type)
        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User.withUsername(testUser.getEmail())
                        .password(testUser.getPassword())
                        .roles("STUDENT")
                        .build();
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk());
    }

    @Test
    void request_WithEmptyBearerToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }
}

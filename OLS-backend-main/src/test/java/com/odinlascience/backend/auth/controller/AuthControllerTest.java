package com.odinlascience.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odinlascience.backend.auth.dto.LoginRequest;
import com.odinlascience.backend.auth.dto.RegisterRequest;
import com.odinlascience.backend.auth.util.CookieUtils;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("authtest@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Auth")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void login_WithValidCredentials_SetsCookiesAndReturnsUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("authtest@example.com");
        request.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.user.email").value("authtest@example.com"))
                .andReturn();

        assertAccessTokenCookieSet(result);
        assertRefreshTokenCookieSet(result);
    }

    @Test
    void login_WithInvalidCredentials_Returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("authtest@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithInvalidEmail_Returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithBlankPassword_Returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("authtest@example.com");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithValidData_SetsCookiesAndReturnsUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
                .andReturn();

        assertAccessTokenCookieSet(result);
        assertRefreshTokenCookieSet(result);
    }

    @Test
    void register_WithExistingEmail_Returns409() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("authtest@example.com");
        request.setPassword("password123");
        request.setFirstName("Duplicate");
        request.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_WithShortPassword_Returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("short@example.com");
        request.setPassword("1234");
        request.setFirstName("Short");
        request.setLastName("Password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_WithValidRefreshCookie_SetsNewCookies() throws Exception {
        // D'abord login pour obtenir un refresh token cookie
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("authtest@example.com");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshTokenValue = extractCookieValue(loginResult, CookieUtils.REFRESH_TOKEN_COOKIE);
        assertThat(refreshTokenValue).isNotNull();

        // Ensuite refresh avec le cookie
        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie(CookieUtils.REFRESH_TOKEN_COOKIE, refreshTokenValue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andReturn();

        assertAccessTokenCookieSet(refreshResult);
        assertRefreshTokenCookieSet(refreshResult);
    }

    @Test
    void refresh_WithoutCookie_Returns401() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_WithInvalidCookie_Returns401() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie(CookieUtils.REFRESH_TOKEN_COOKIE, "invalid.token")))
                .andExpect(status().isUnauthorized());
    }

    // ==================== HELPERS ====================

    private void assertAccessTokenCookieSet(MvcResult result) {
        String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookieHeader).isNotNull();
        assertThat(result.getResponse().getHeaders("Set-Cookie"))
                .anyMatch(h -> h.contains(CookieUtils.ACCESS_TOKEN_COOKIE) && h.contains("HttpOnly"));
    }

    private void assertRefreshTokenCookieSet(MvcResult result) {
        assertThat(result.getResponse().getHeaders("Set-Cookie"))
                .anyMatch(h -> h.contains(CookieUtils.REFRESH_TOKEN_COOKIE) && h.contains("HttpOnly"));
    }

    private String extractCookieValue(MvcResult result, String cookieName) {
        return result.getResponse().getHeaders("Set-Cookie").stream()
                .filter(h -> h.startsWith(cookieName + "="))
                .map(h -> h.substring(cookieName.length() + 1, h.indexOf(";")))
                .findFirst()
                .orElse(null);
    }
}

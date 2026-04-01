package com.odinlascience.backend.auth.controller;

import com.odinlascience.backend.auth.dto.*;
import com.odinlascience.backend.auth.service.AuthService;
import com.odinlascience.backend.auth.util.CookieUtils;
import com.odinlascience.backend.exception.SessionLimitExceededException;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        lenient().when(httpServletRequest.getHeader("User-Agent")).thenReturn("Chrome sur Windows");
        lenient().when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void login_WhenSuccessful_ReturnsUserWithoutTokens() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse mockResponse = AuthResponse.builder()
                .expiresIn(3600)
                .user(UserDTO.builder().id(1L).email("test@example.com").build())
                .build();

        AuthService.LoginResult mockResult = new AuthService.LoginResult(
                "accessToken", "refreshToken", mockResponse);

        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockResult);

        ResponseEntity<AuthResponse> response = authController.login(request, httpServletRequest, httpServletResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void login_WhenSessionLimitReached_ThrowsSessionLimitExceededException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new SessionLimitExceededException(List.of(), 3));

        assertThatThrownBy(() -> authController.login(request, httpServletRequest, httpServletResponse))
                .isInstanceOf(SessionLimitExceededException.class);
    }

    @Test
    void refreshToken_WhenCookiePresent_ReturnsExpiresIn() {
        Cookie refreshCookie = new Cookie(CookieUtils.REFRESH_TOKEN_COOKIE, "validRefreshToken");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{refreshCookie});

        TokenResponse mockResponse = TokenResponse.builder()
                .expiresIn(3600)
                .build();

        AuthService.RefreshResult mockResult = new AuthService.RefreshResult(
                "newAccessToken", "newRefreshToken", mockResponse);

        when(authService.refreshToken("validRefreshToken")).thenReturn(mockResult);

        ResponseEntity<TokenResponse> response = authController.refreshToken(httpServletRequest, httpServletResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getExpiresIn()).isEqualTo(3600);
    }

    @Test
    void refreshToken_WhenNoCookie_Returns401() {
        when(httpServletRequest.getCookies()).thenReturn(null);

        ResponseEntity<TokenResponse> response = authController.refreshToken(httpServletRequest, httpServletResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshToken_WhenInvalid_ThrowsException() {
        Cookie refreshCookie = new Cookie(CookieUtils.REFRESH_TOKEN_COOKIE, "invalidToken");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{refreshCookie});

        when(authService.refreshToken("invalidToken"))
                .thenThrow(new IllegalArgumentException("Token invalide"));

        assertThatThrownBy(() -> authController.refreshToken(httpServletRequest, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void revokeSessionPublic_WhenSuccessful_Returns204() {
        RevokeSessionRequest request = new RevokeSessionRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setSessionId(java.util.UUID.randomUUID());

        doNothing().when(authService).revokeSessionWithCredentials(
                anyString(), anyString(), any(java.util.UUID.class));

        ResponseEntity<Void> response = authController.revokeSessionPublic(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}

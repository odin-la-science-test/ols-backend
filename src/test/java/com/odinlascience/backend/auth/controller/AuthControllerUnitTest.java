package com.odinlascience.backend.auth.controller;

import com.odinlascience.backend.auth.dto.*;
import com.odinlascience.backend.auth.service.AuthService;
import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.exception.SessionLimitExceededException;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        lenient().when(httpServletRequest.getHeader("User-Agent")).thenReturn("Chrome sur Windows");
        lenient().when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void login_WhenSuccessful_ReturnsAuthResponse() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse mockResponse = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .expiresIn(3600)
                .user(UserDTO.builder().id(1L).email("test@example.com").build())
                .build();

        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request, httpServletRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("accessToken");
    }

    @Test
    void login_WhenSessionLimitReached_ThrowsSessionLimitExceededException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new SessionLimitExceededException(List.of(), 3));

        // Act & Assert
        assertThatThrownBy(() -> authController.login(request, httpServletRequest))
                .isInstanceOf(SessionLimitExceededException.class);
    }

    @Test
    void refreshToken_WhenSuccessful_ReturnsTokenResponse() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("validRefreshToken");

        TokenResponse mockResponse = TokenResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .expiresIn(3600)
                .build();

        when(authService.refreshToken("validRefreshToken")).thenReturn(mockResponse);

        // Act
        ResponseEntity<TokenResponse> response = authController.refreshToken(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("newAccessToken");
    }

    @Test
    void refreshToken_WhenInvalid_ThrowsException() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalidToken");

        when(authService.refreshToken("invalidToken"))
                .thenThrow(new IllegalArgumentException("Token invalide"));

        // Act & Assert
        assertThatThrownBy(() -> authController.refreshToken(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void revokeSessionPublic_WhenSuccessful_Returns204() {
        // Arrange
        RevokeSessionRequest request = new RevokeSessionRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setSessionId(java.util.UUID.randomUUID());

        doNothing().when(authService).revokeSessionWithCredentials(
                anyString(), anyString(), any(java.util.UUID.class));

        // Act
        ResponseEntity<Void> response = authController.revokeSessionPublic(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}

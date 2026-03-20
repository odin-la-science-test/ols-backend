package com.odinlascience.backend.auth.controller;

import com.odinlascience.backend.auth.dto.LoginRequest;
import com.odinlascience.backend.auth.dto.RefreshTokenRequest;
import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.mapper.UserMapper;
import com.odinlascience.backend.user.model.User;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthController authController;

    private UserDetails mockUserDetails;
    private User mockUser;
    private UserDTO mockUserDTO;

    @BeforeEach
    void setUp() {
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .roles("USER")
                .build();

        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        mockUserDTO = UserDTO.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Test
    void login_WhenUserNotFoundInRepository_ThrowsException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateAccessToken(mockUserDetails)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(mockUserDetails)).thenReturn("refreshToken");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authController.login(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("test@example.com");
    }

    @Test
    void refreshToken_WhenSubjectIsNull_Returns401() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("someToken");

        when(httpServletRequest.getRequestURI()).thenReturn("/api/auth/refresh");
        when(jwtService.extractSubjectIgnoringExpiration("someToken")).thenReturn(null);

        // Act
        ResponseEntity<?> response = authController.refreshToken(request, httpServletRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshToken_WhenRefreshTokenInvalid_Returns401() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalidRefreshToken");

        when(httpServletRequest.getRequestURI()).thenReturn("/api/auth/refresh");
        when(jwtService.extractSubjectIgnoringExpiration("invalidRefreshToken")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(mockUserDetails);
        when(jwtService.isRefreshTokenValid("invalidRefreshToken", mockUserDetails)).thenReturn(false);

        // Act
        ResponseEntity<?> response = authController.refreshToken(request, httpServletRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshToken_WhenValid_ReturnsNewTokens() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("validRefreshToken");

        when(jwtService.extractSubjectIgnoringExpiration("validRefreshToken")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(mockUserDetails);
        when(jwtService.isRefreshTokenValid("validRefreshToken", mockUserDetails)).thenReturn(true);
        when(jwtService.generateAccessToken(mockUserDetails)).thenReturn("newAccessToken");
        when(jwtService.getAccessTokenExpirationInSeconds()).thenReturn(3600L);

        // Act
        ResponseEntity<?> response = authController.refreshToken(request, httpServletRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void refreshToken_WhenExceptionDuringExtraction_Returns401() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("malformedToken");

        when(httpServletRequest.getRequestURI()).thenReturn("/api/auth/refresh");
        when(jwtService.extractSubjectIgnoringExpiration("malformedToken"))
                .thenThrow(new RuntimeException("Malformed token"));

        // Act
        ResponseEntity<?> response = authController.refreshToken(request, httpServletRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

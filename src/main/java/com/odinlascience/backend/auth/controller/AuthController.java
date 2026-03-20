package com.odinlascience.backend.auth.controller;

import com.odinlascience.backend.auth.dto.AuthResponse;
import com.odinlascience.backend.auth.dto.GuestResponse;
import com.odinlascience.backend.auth.dto.LoginRequest;
import com.odinlascience.backend.auth.dto.RefreshTokenRequest;
import com.odinlascience.backend.auth.dto.TokenResponse;
import com.odinlascience.backend.exception.dto.ErrorResponseDTO;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.mapper.UserMapper;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.odinlascience.backend.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur", description = "Authentifie l'utilisateur et retourne un access token + refresh token + profil utilisateur")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String accessToken = jwtService.generateAccessToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'email : " + request.getEmail()));

        log.info("Login successful for user: {}", request.getEmail());
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .user(userMapper.toDTO(user))
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un compte", description = "Enregistre un nouvel utilisateur et retourne un access token + refresh token + profil utilisateur")
    public ResponseEntity<?> register(@Valid @RequestBody com.odinlascience.backend.auth.dto.RegisterRequest request, HttpServletRequest httpRequest) {
        log.info("Registration attempt for email: {}", request.getEmail());
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseDTO.builder()
                    .status(HttpStatus.CONFLICT.value())
                    .error("Conflict")
                    .message("Un compte avec cet email existe déjà")
                    .path(httpRequest.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }

        com.odinlascience.backend.user.model.User newUser = com.odinlascience.backend.user.model.User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User saved = userRepository.save(newUser);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getEmail());
        final String accessToken = jwtService.generateAccessToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        log.info("Registration successful for user: {}", saved.getEmail());
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .user(userMapper.toDTO(saved))
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token", description = "Utilise le refresh token pour obtenir un nouveau access token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.getRefreshToken();
        
        String subject;
        try {
            subject = jwtService.extractSubjectIgnoringExpiration(refreshToken);
        } catch (Exception e) {
            log.warn("Token refresh failed - invalid token format");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponseDTO.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error("Unauthorized")
                    .message("Format du token invalide")
                    .path(httpRequest.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
        
        if (subject == null) {
            log.warn("Token refresh failed - could not extract subject");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponseDTO.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error("Unauthorized")
                    .message("Token invalide")
                    .path(httpRequest.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        
        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            log.warn("Token refresh failed for user: {} - invalid refresh token", subject);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponseDTO.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error("Unauthorized")
                    .message("Refresh token invalide ou expiré")
                    .path(httpRequest.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        log.debug("Token refreshed for user: {}", subject);

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .build());
    }

    @PostMapping("/guest")
    @Operation(summary = "Connexion invité", description = "Génère un token anonyme pour accès limité en lecture seule. Aucun compte n'est créé en base.")
    public ResponseEntity<GuestResponse> loginAsGuest() {
        // Générer un ID unique pour ce guest
        String guestId = "guest_" + UUID.randomUUID().toString().substring(0, 8);
        
        log.info("Guest login - generating anonymous token for: {}", guestId);
        
        // Générer un token anonyme (pas de compte en DB)
        final String accessToken = jwtService.generateGuestToken(guestId);

        return ResponseEntity.ok(GuestResponse.builder()
                .accessToken(accessToken)
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .user(GuestResponse.GuestUser.builder()
                        .id(guestId)
                        .role("GUEST")
                        .build())
                .build());
    }
}
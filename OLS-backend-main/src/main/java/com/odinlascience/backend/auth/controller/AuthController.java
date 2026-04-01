package com.odinlascience.backend.auth.controller;

import com.odinlascience.backend.auth.dto.*;
import com.odinlascience.backend.auth.service.AuthService;
import com.odinlascience.backend.auth.service.EmailVerificationService;
import com.odinlascience.backend.auth.service.PasswordResetService;
import com.odinlascience.backend.auth.util.CookieUtils;
import com.odinlascience.backend.auth.util.DeviceInfoExtractor;
import com.odinlascience.backend.exception.dto.ErrorResponseDTO;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification")
@Validated
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur", description = "Authentifie l'utilisateur, set les tokens en cookies httpOnly et retourne le profil utilisateur. Retourne 409 si la limite de sessions est atteinte.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse httpResponse) {
        String deviceInfo = DeviceInfoExtractor.extract(httpRequest);
        String ipAddress = DeviceInfoExtractor.extractIpAddress(httpRequest);
        AuthService.LoginResult result = authService.login(request.getEmail(), request.getPassword(), deviceInfo, ipAddress);
        setAuthCookies(httpResponse, result.accessToken(), result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/register")
    @Operation(summary = "Creer un compte", description = "Enregistre un nouvel utilisateur, set les tokens en cookies httpOnly et retourne le profil utilisateur")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      HttpServletRequest httpRequest,
                                      HttpServletResponse httpResponse) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ErrorResponseDTO.builder()
                            .status(HttpStatus.CONFLICT.value())
                            .error("Conflict")
                            .message("Un compte avec cet email existe deja")
                            .path(httpRequest.getRequestURI())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
        String deviceInfo = DeviceInfoExtractor.extract(httpRequest);
        String ipAddress = DeviceInfoExtractor.extractIpAddress(httpRequest);
        AuthService.LoginResult result = authService.register(
                request.getEmail(), request.getPassword(),
                request.getFirstName(), request.getLastName(),
                deviceInfo, ipAddress);
        setAuthCookies(httpResponse, result.accessToken(), result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraichir le token", description = "Utilise le refresh token (cookie) pour obtenir de nouveaux tokens (rotation)")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest httpRequest,
                                                      HttpServletResponse httpResponse) {
        String refreshToken = CookieUtils.extractRefreshToken(httpRequest);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthService.RefreshResult result = authService.refreshToken(refreshToken);
        setAuthCookies(httpResponse, result.accessToken(), result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/logout")
    @Operation(summary = "Deconnecter la session courante", description = "Revoque la session associee au token courant et supprime les cookies")
    public ResponseEntity<Void> logout(Authentication auth, HttpServletRequest request,
                                       HttpServletResponse httpResponse) {
        UUID sessionId = (UUID) request.getAttribute("sessionId");
        if (sessionId != null && auth != null) {
            authService.logout(sessionId, auth.getName());
        }
        CookieUtils.clearAuthCookies(httpResponse, cookieSecure);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/revoke-session")
    @Operation(summary = "Revoquer une session (non authentifie)", description = "Permet de revoquer une session en fournissant email + mot de passe. Utilise quand la limite de sessions est atteinte.")
    public ResponseEntity<Void> revokeSessionPublic(@Valid @RequestBody RevokeSessionRequest request) {
        authService.revokeSessionWithCredentials(request.getEmail(), request.getPassword(), request.getSessionId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Demander une reinitialisation de mot de passe", description = "Envoie un email avec un lien de reinitialisation. Retourne toujours 200 (anti-enumeration).")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reinitialiser le mot de passe", description = "Reinitialise le mot de passe avec un token valide. Revoque toutes les sessions.")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verifier l'email", description = "Verifie l'adresse email via le token envoye par email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") @NotBlank String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Renvoyer l'email de verification", description = "Renvoie l'email de verification si l'adresse n'est pas encore verifiee")
    public ResponseEntity<Void> resendVerification(Authentication auth) {
        emailVerificationService.resendVerification(auth.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/guest")
    @Operation(summary = "Connexion invite", description = "Genere un token anonyme en cookie pour acces limite en lecture seule. Aucun compte n'est cree en base.")
    public ResponseEntity<GuestResponse> loginAsGuest(HttpServletResponse httpResponse) {
        String guestId = "guest_" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Guest login - generating anonymous token for: {}", guestId);
        final String accessToken = jwtService.generateGuestToken(guestId);
        CookieUtils.addAccessTokenCookie(httpResponse, accessToken,
                jwtService.getAccessTokenExpirationInSeconds(), cookieSecure);
        return ResponseEntity.ok(GuestResponse.builder()
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .user(GuestResponse.GuestUser.builder()
                        .id(guestId)
                        .role("GUEST")
                        .build())
                .build());
    }

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        CookieUtils.addAccessTokenCookie(response, accessToken,
                jwtService.getAccessTokenExpirationInSeconds(), cookieSecure);
        CookieUtils.addRefreshTokenCookie(response, refreshToken,
                jwtService.getRefreshTokenExpirationInSeconds(), cookieSecure);
    }
}

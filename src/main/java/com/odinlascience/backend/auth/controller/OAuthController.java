package com.odinlascience.backend.auth.controller;

import com.odinlascience.backend.auth.config.OAuthProperties;
import com.odinlascience.backend.auth.dto.AuthResponse;
import com.odinlascience.backend.auth.service.OAuthAuthService;
import com.odinlascience.backend.auth.util.DeviceInfoExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification OAuth")
public class OAuthController {

    private final OAuthAuthService oAuthAuthService;
    private final OAuthProperties oAuthProperties;

    // ==================== GOOGLE ====================

    @GetMapping("/oauth2/google")
    @Operation(summary = "Redirection Google Sign-In", description = "Redirige vers la page d'authentification Google")
    public RedirectView redirectToGoogle() {
        OAuthProperties.Google google = oAuthProperties.getGoogle();

        String state = generateState();

        String authUrl = UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", google.getClientId())
                .queryParam("redirect_uri", google.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", google.getScopes()))
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "select_account")
                .build()
                .toUriString();

        log.info("Redirection vers Google OAuth");
        return new RedirectView(authUrl);
    }

    @GetMapping("/oauth2/google/callback")
    @Operation(summary = "Callback Google Sign-In", description = "Recoit le code d'autorisation Google, authentifie l'utilisateur et redirige vers le frontend avec les tokens")
    public RedirectView googleCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request) {

        try {
            String deviceInfo = DeviceInfoExtractor.extract(request);
            String ipAddress = DeviceInfoExtractor.extractIpAddress(request);

            AuthResponse authResponse = oAuthAuthService.authenticateWithGoogle(code, deviceInfo, ipAddress);

            return redirectWithTokens(authResponse);

        } catch (Exception e) {
            log.error("Erreur lors du callback Google", e);
            return redirectWithError("google_error", e.getMessage());
        }
    }

    // ==================== CAS UNIVERSITE DE LILLE ====================

    @GetMapping("/cas/login")
    @Operation(summary = "Redirection CAS Universite", description = "Redirige vers la page d'authentification CAS de l'universite")
    public RedirectView redirectToCas() {
        OAuthProperties.Cas cas = oAuthProperties.getCas();

        String loginUrl = cas.getServerUrl() + "/login?service="
                + URLEncoder.encode(cas.getServiceUrl(), StandardCharsets.UTF_8);

        log.info("Redirection vers CAS Universite de Lille");
        return new RedirectView(loginUrl);
    }

    @GetMapping("/cas/callback")
    @Operation(summary = "Callback CAS Universite", description = "Recoit le ticket CAS, valide l'utilisateur et redirige vers le frontend avec les tokens")
    public RedirectView casCallback(
            @RequestParam("ticket") String ticket,
            HttpServletRequest request) {

        try {
            String deviceInfo = DeviceInfoExtractor.extract(request);
            String ipAddress = DeviceInfoExtractor.extractIpAddress(request);
            String serviceUrl = oAuthProperties.getCas().getServiceUrl();

            AuthResponse authResponse = oAuthAuthService.authenticateWithCas(ticket, serviceUrl, deviceInfo, ipAddress);

            return redirectWithTokens(authResponse);

        } catch (Exception e) {
            log.error("Erreur lors du callback CAS", e);
            return redirectWithError("cas_error", e.getMessage());
        }
    }

    // ==================== UTILITAIRES ====================

    private RedirectView redirectWithTokens(AuthResponse authResponse) {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(oAuthProperties.getFrontendUrl() + "/oauth-callback")
                .queryParam("accessToken", authResponse.getAccessToken())
                .queryParam("refreshToken", authResponse.getRefreshToken())
                .queryParam("expiresIn", authResponse.getExpiresIn())
                .build()
                .toUriString();

        RedirectView redirectView = new RedirectView(redirectUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    private RedirectView redirectWithError(String errorCode, String message) {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(oAuthProperties.getFrontendUrl() + "/login")
                .queryParam("error", errorCode)
                .queryParam("message", URLEncoder.encode(
                        message != null ? message : "Erreur d'authentification",
                        StandardCharsets.UTF_8))
                .build()
                .toUriString();

        RedirectView redirectView = new RedirectView(redirectUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    private String generateState() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

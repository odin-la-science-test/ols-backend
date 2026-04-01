package com.odinlascience.backend.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

/**
 * Utilitaires pour la gestion des cookies d'authentification httpOnly.
 */
public final class CookieUtils {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private static final String ACCESS_TOKEN_PATH = "/api";
    private static final String REFRESH_TOKEN_PATH = "/api/auth/refresh";

    private CookieUtils() {
    }

    public static void addAccessTokenCookie(HttpServletResponse response, String token,
                                            long maxAgeSeconds, boolean secure) {
        addCookie(response, ACCESS_TOKEN_COOKIE, token, ACCESS_TOKEN_PATH, maxAgeSeconds, secure);
    }

    public static void addRefreshTokenCookie(HttpServletResponse response, String token,
                                             long maxAgeSeconds, boolean secure) {
        addCookie(response, REFRESH_TOKEN_COOKIE, token, REFRESH_TOKEN_PATH, maxAgeSeconds, secure);
    }

    public static void clearAuthCookies(HttpServletResponse response, boolean secure) {
        addCookie(response, ACCESS_TOKEN_COOKIE, "", ACCESS_TOKEN_PATH, 0, secure);
        addCookie(response, REFRESH_TOKEN_COOKIE, "", REFRESH_TOKEN_PATH, 0, secure);
    }

    public static String extractAccessToken(HttpServletRequest request) {
        return extractCookie(request, ACCESS_TOKEN_COOKIE);
    }

    public static String extractRefreshToken(HttpServletRequest request) {
        return extractCookie(request, REFRESH_TOKEN_COOKIE);
    }

    private static String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static void addCookie(HttpServletResponse response, String name, String value,
                                  String path, long maxAgeSeconds, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path(path)
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}

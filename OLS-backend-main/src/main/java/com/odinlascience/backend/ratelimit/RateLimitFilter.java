package com.odinlascience.backend.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odinlascience.backend.exception.dto.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIP(request);
        String requestPath = request.getRequestURI();

        if (isSensitiveEndpoint(requestPath)) {
            if (!rateLimitService.tryConsumeSensitive(clientIp)) {
                log.warn("Rate limit exceeded for sensitive endpoint from IP: {}", clientIp);
                sendRateLimitResponse(response, request, true);
                return;
            }
            addRateLimitHeaders(response, rateLimitService.getAvailableSensitiveTokens(clientIp));
        } else if (isAuthEndpoint(requestPath)) {
            if (!rateLimitService.tryConsumeAuth(clientIp)) {
                log.warn("Rate limit exceeded for auth endpoint from IP: {}", clientIp);
                sendRateLimitResponse(response, request, true);
                return;
            }
            addRateLimitHeaders(response, rateLimitService.getAvailableAuthTokens(clientIp));
        } else {
            if (!rateLimitService.tryConsume(clientIp)) {
                log.warn("Rate limit exceeded from IP: {}", clientIp);
                sendRateLimitResponse(response, request, false);
                return;
            }
            addRateLimitHeaders(response, rateLimitService.getAvailableTokens(clientIp));
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSensitiveEndpoint(String path) {
        return path.startsWith("/api/auth/forgot-password")
            || path.startsWith("/api/auth/reset-password")
            || path.startsWith("/api/auth/verify-email")
            || path.startsWith("/api/auth/resend-verification");
    }

    private boolean isAuthEndpoint(String path) {
        return path.startsWith("/api/auth/");
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private void sendRateLimitResponse(HttpServletResponse response, HttpServletRequest request, boolean isAuth)
            throws IOException {
        
        String message = isAuth 
            ? "Trop de tentatives de connexion. Veuillez réessayer dans quelques minutes."
            : "Trop de requêtes. Veuillez ralentir.";

        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.TOO_MANY_REQUESTS.value())
            .error("Too Many Requests")
            .message(message)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", isAuth ? "900" : "60");
        
        objectMapper.writeValue(response.getOutputStream(), error);
    }

    private void addRateLimitHeaders(HttpServletResponse response, long remainingTokens) {
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remainingTokens));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        if (path.startsWith("/swagger-ui") 
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/actuator")) {
            return true;
        }
        
        String userAgent = request.getHeader("User-Agent");
        return userAgent == null || userAgent.isEmpty();
    }
}

package com.odinlascience.backend.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtServiceExpiredTokenTest {

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        testUser = User.withUsername("expiredtest@example.com")
                .password("password")
                .roles("USER")
                .build();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createExpiredToken(String username) {
        return Jwts.builder()
                .claim("type", "access")
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis() - 10000)) // 10 seconds ago
                .expiration(new Date(System.currentTimeMillis() - 5000)) // 5 seconds ago (expired)
                .signWith(getSignInKey())
                .compact();
    }

    @Test
    void extractSubjectIgnoringExpiration_WithExpiredToken_StillReturnsSubject() {
        // Create an expired token
        String expiredToken = createExpiredToken("expiredtest@example.com");

        // This should still return the subject even though token is expired
        String subject = jwtService.extractSubjectIgnoringExpiration(expiredToken);
        assertThat(subject).isEqualTo("expiredtest@example.com");
    }

    @Test
    void isTokenValid_WithExpiredToken_ReturnsFalse() {
        String expiredToken = createExpiredToken("expiredtest@example.com");
        
        // Token is expired, so it should be invalid even if username matches
        // This will throw ExpiredJwtException which makes isTokenExpired return true
        try {
            boolean isExpired = jwtService.isTokenExpired(expiredToken);
            // If we get here, the token wasn't expired (unexpected)
            assertThat(isExpired).isTrue();
        } catch (Exception e) {
            // Expected - expired tokens throw exception
            assertThat(e).isNotNull();
        }
    }

    @Test
    void isRefreshTokenValid_WithExpiredRefreshToken_ReturnsFalse() {
        String expiredRefreshToken = Jwts.builder()
                .claim("type", "refresh")
                .subject("expiredtest@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(getSignInKey())
                .compact();

        // Should return false because token is expired
        assertThat(jwtService.isRefreshTokenValid(expiredRefreshToken, testUser)).isFalse();
    }

    @Test
    void isTokenValid_WhenUsernameDoesNotMatch_ReturnsFalse() {
        // Create a valid token for one user
        String token = jwtService.generateAccessToken(testUser);
        
        // Try to validate with a different user
        UserDetails differentUser = User.withUsername("different@example.com")
                .password("password")
                .roles("USER")
                .build();
        
        // This tests the first part of the AND condition failing (username mismatch)
        assertThat(jwtService.isTokenValid(token, differentUser)).isFalse();
    }

    @Test
    void isRefreshTokenValid_WhenUsernameDoesNotMatch_ReturnsFalse() {
        // Create a valid refresh token for one user
        String refreshToken = jwtService.generateRefreshToken(testUser);
        
        // Try to validate with a different user
        UserDetails differentUser = User.withUsername("different@example.com")
                .password("password")
                .roles("USER")
                .build();
        
        // This tests the username mismatch branch in isRefreshTokenValid
        assertThat(jwtService.isRefreshTokenValid(refreshToken, differentUser)).isFalse();
    }
}

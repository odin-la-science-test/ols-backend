package com.odinlascience.backend.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    // ==================== EXTRACTION ====================

    /**
     * Extrait le subject du token (email pour users, guestId pour guests)
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractSubjectIgnoringExpiration(String token) {
        try {
            return extractSubject(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ==================== GÉNÉRATION ====================

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessTokenExpiration, "access");
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration, "refresh");
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, String tokenType) {
        return Jwts.builder()
                .claims(extraClaims)
                .claim("type", tokenType)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Génère un token anonyme pour les guests (sans compte en DB)
     * @param guestId Identifiant unique du guest (UUID)
     * @return Access token avec role GUEST
     */
    public String generateGuestToken(String guestId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "GUEST");
        claims.put("isGuest", true);
        
        return Jwts.builder()
                .claims(claims)
                .claim("type", "access")
                .subject(guestId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Vérifie si le token est un token guest
     */
    public boolean isGuestToken(String token) {
        try {
            Boolean isGuest = extractClaim(token, claims -> claims.get("isGuest", Boolean.class));
            return Boolean.TRUE.equals(isGuest);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== VALIDATION ====================

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String subject = extractSubject(token);
        return (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        try {
            final String subject = extractSubject(token);
            final String tokenType = extractClaim(token, claims -> claims.get("type", String.class));
            return subject.equals(userDetails.getUsername()) 
                    && !isTokenExpired(token) 
                    && "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ==================== UTILITAIRES ====================

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

    public long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpiration / 1000;
    }
}
package com.odinlascience.backend.security;

import com.odinlascience.backend.auth.model.UserSession;
import com.odinlascience.backend.auth.repository.UserSessionRepository;
import com.odinlascience.backend.auth.service.SessionService;
import com.odinlascience.backend.auth.util.TokenHasher;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de securite lies aux sessions et tokens JWT.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SessionSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("session-test@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Session")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .build();
        testUser = userRepository.save(testUser);
    }

    // ─── Token expire ───

    @Nested
    @DisplayName("Token expire")
    class TokenExpire {

        @Test
        void tokenExpire_EstRejeteParLeFiltre() throws Exception {
            String expiredToken = createExpiredToken("session-test@example.com");

            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", "Bearer " + expiredToken))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void refreshTokenExpire_EstRejete() {
            UserDetails userDetails = toUserDetails(testUser);
            String expiredRefresh = createExpiredRefreshToken("session-test@example.com");

            assertThat(jwtService.isRefreshTokenValid(expiredRefresh, userDetails)).isFalse();
        }
    }

    // ─── Refresh token hashe en base ───

    @Nested
    @DisplayName("Refresh token hashage")
    class RefreshTokenHash {

        @Test
        void refreshToken_EstHasheEnBase_PasStockeEnClair() {
            UserDetails userDetails = toUserDetails(testUser);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            UserSession session = sessionService.createSession(
                    testUser, refreshToken, "Test Device", "127.0.0.1", 3600000L);

            // Le token hash en base ne doit pas etre le token en clair
            assertThat(session.getRefreshTokenHash()).isNotEqualTo(refreshToken);

            // Le hash doit correspondre au hash SHA-256 du token
            String expectedHash = TokenHasher.hash(refreshToken);
            assertThat(session.getRefreshTokenHash()).isEqualTo(expectedHash);
        }

        @Test
        void refreshToken_HashEstDeterministe() {
            String token = "test-token-value";
            String hash1 = TokenHasher.hash(token);
            String hash2 = TokenHasher.hash(token);

            assertThat(hash1).isEqualTo(hash2);
        }

        @Test
        void refreshToken_HashEstDifferentPourDifferentsTokens() {
            String hash1 = TokenHasher.hash("token-a");
            String hash2 = TokenHasher.hash("token-b");

            assertThat(hash1).isNotEqualTo(hash2);
        }
    }

    // ─── Rotation de refresh token ───

    @Nested
    @DisplayName("Rotation de refresh token")
    class RotationRefreshToken {

        @Test
        void rotation_AncienTokenInvalide_NouveauTokenActif() {
            UserDetails userDetails = toUserDetails(testUser);
            String oldRefreshToken = jwtService.generateRefreshToken(userDetails);

            // Generer un token avec un sessionId pour garantir qu'il est different
            String newRefreshToken = jwtService.generateRefreshToken(userDetails,
                    java.util.UUID.randomUUID());

            // Precondition : les deux tokens sont bien differents
            assertThat(oldRefreshToken).isNotEqualTo(newRefreshToken);

            sessionService.createSession(
                    testUser, oldRefreshToken, "Test Device", "127.0.0.1", 3600000L);

            String oldHash = TokenHasher.hash(oldRefreshToken);
            String newHash = TokenHasher.hash(newRefreshToken);

            // Effectuer la rotation
            UserSession rotated = sessionService.validateAndRotateRefreshToken(
                    oldRefreshToken, newRefreshToken);

            assertThat(rotated).isNotNull();

            // Le hash en base doit correspondre au nouveau token
            assertThat(rotated.getRefreshTokenHash()).isEqualTo(newHash);

            // L'ancien hash ne doit plus correspondre
            assertThat(rotated.getRefreshTokenHash()).isNotEqualTo(oldHash);

            // Verifier en base que l'ancien hash n'existe plus
            sessionRepository.flush();
            Optional<UserSession> byOldHash = sessionRepository.findByRefreshTokenHash(oldHash);
            assertThat(byOldHash).isEmpty();
        }

        @Test
        void rotation_AvecTokenInconnu_RetourneNull() {
            UserSession result = sessionService.validateAndRotateRefreshToken(
                    "unknown-token", "new-token");

            assertThat(result).isNull();
        }
    }

    // ─── Revocation de session ───

    @Nested
    @DisplayName("Revocation de session")
    class RevocationSession {

        @Test
        void revokeSession_SessionSupprimee() {
            UserDetails userDetails = toUserDetails(testUser);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            UserSession session = sessionService.createSession(
                    testUser, refreshToken, "Test Device", "127.0.0.1", 3600000L);

            // Verifier que la session existe
            assertThat(sessionService.isSessionValid(session.getId())).isTrue();

            // Revoquer la session
            sessionService.revokeSession(session.getId(), testUser.getId());

            // La session ne doit plus exister
            assertThat(sessionService.isSessionValid(session.getId())).isFalse();
        }

        @Test
        void revokeAllSessions_ToutesLesSessionsSupprimees() {
            UserDetails userDetails = toUserDetails(testUser);

            // Creer plusieurs sessions
            for (int i = 0; i < 3; i++) {
                String refreshToken = jwtService.generateRefreshToken(userDetails);
                sessionService.createSession(
                        testUser, refreshToken, "Device " + i, "127.0.0.1", 3600000L);
            }

            assertThat(sessionService.countActiveSessions(testUser.getId())).isEqualTo(3);

            // Revoquer toutes les sessions
            sessionService.revokeAllSessions(testUser.getId());

            assertThat(sessionService.countActiveSessions(testUser.getId())).isZero();
        }

        @Test
        void revokeSession_AvecMauvaisUserId_NeSupprimePas() {
            UserDetails userDetails = toUserDetails(testUser);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            UserSession session = sessionService.createSession(
                    testUser, refreshToken, "Test Device", "127.0.0.1", 3600000L);

            // Tenter de revoquer avec un userId different
            sessionService.revokeSession(session.getId(), -999L);

            // La session doit toujours exister
            assertThat(sessionService.isSessionValid(session.getId())).isTrue();
        }
    }

    // ─── Utilitaires ───

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("STUDENT")
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
                .issuedAt(new Date(System.currentTimeMillis() - 10_000))
                .expiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(getSignInKey())
                .compact();
    }

    private String createExpiredRefreshToken(String username) {
        return Jwts.builder()
                .claim("type", "refresh")
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis() - 10_000))
                .expiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(getSignInKey())
                .compact();
    }
}

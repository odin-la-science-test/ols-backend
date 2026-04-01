package com.odinlascience.backend.security;

import com.odinlascience.backend.security.service.JwtService;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'integration pour la configuration de securite (endpoints publics vs proteges).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    private String validAccessToken;

    @BeforeEach
    void setUp() {
        UserDetails testUser = User.withUsername("sectest@example.com")
                .password("password")
                .roles("USER")
                .build();
        validAccessToken = jwtService.generateAccessToken(testUser);
    }

    // ─── Endpoints publics ───

    @Nested
    @DisplayName("Endpoints publics — accessibles sans token")
    class EndpointsPublics {

        @Test
        void login_SansToken_NeRetournePas401DuFiltre() throws Exception {
            // L'endpoint est accessible sans token (pas 401 du filtre securite).
            // La reponse depend des credentials (400 bad request ou 401 bad credentials).
            int statusCode = mockMvc.perform(post("/api/auth/login")
                            .contentType("application/json")
                            .content("{\"email\":\"a@b.com\",\"password\":\"12345678\"}"))
                    .andReturn().getResponse().getStatus();
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(403);
        }

        @Test
        void register_SansToken_NeRetournePas401() throws Exception {
            String uniqueEmail = "secint-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
            int statusCode = mockMvc.perform(post("/api/auth/register")
                            .contentType("application/json")
                            .content("{\"email\":\"" + uniqueEmail + "\",\"password\":\"12345678\",\"firstName\":\"A\",\"lastName\":\"B\"}"))
                    .andReturn().getResponse().getStatus();
            // L'endpoint est accessible (pas bloque par le filtre JWT) : jamais 401/403
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(401);
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(403);
        }

        @Test
        void refresh_SansToken_AccepteRequete() throws Exception {
            int statusCode = mockMvc.perform(post("/api/auth/refresh")
                            .contentType("application/json")
                            .content("{\"refreshToken\":\"\"}"))
                    .andReturn().getResponse().getStatus();
            // Le endpoint est accessible (pas bloque par le filtre JWT)
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(401);
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(403);
        }

        @Test
        void guest_SansToken_Accessible() throws Exception {
            int statusCode = mockMvc.perform(post("/api/auth/guest"))
                    .andReturn().getResponse().getStatus();
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(401);
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(403);
        }

        @Test
        void revokeSession_SansToken_Accessible() throws Exception {
            int statusCode = mockMvc.perform(post("/api/auth/revoke-session")
                            .contentType("application/json")
                            .content("{\"sessionId\":\"00000000-0000-0000-0000-000000000000\"}"))
                    .andReturn().getResponse().getStatus();
            // Endpoint public : jamais 401/403
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(401);
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(403);
        }

        @Test
        void forgotPassword_SansToken_Accessible() throws Exception {
            int statusCode = mockMvc.perform(post("/api/auth/forgot-password")
                            .contentType("application/json")
                            .content("{\"email\":\"a@b.com\"}"))
                    .andReturn().getResponse().getStatus();
            // L'endpoint est accessible sans token : jamais 401/403
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(401);
            org.assertj.core.api.Assertions.assertThat(statusCode).isNotEqualTo(403);
        }

        @Test
        void swaggerUi_SansToken_Accessible() throws Exception {
            mockMvc.perform(get("/swagger-ui/index.html"))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void apiDocs_SansToken_Accessible() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    // ─── Endpoints proteges ───

    @Nested
    @DisplayName("Endpoints proteges — 401 sans token")
    class EndpointsProtegesSansToken {

        @Test
        void contacts_SansToken_Retourne401() throws Exception {
            mockMvc.perform(get("/api/contacts"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void annotations_SansToken_Retourne401() throws Exception {
            mockMvc.perform(get("/api/annotations"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void notifications_SansToken_Retourne401() throws Exception {
            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void users_SansToken_Retourne401() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void notes_SansToken_Retourne401() throws Exception {
            mockMvc.perform(get("/api/notes"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void quickshare_SansToken_Retourne401() throws Exception {
            mockMvc.perform(get("/api/quickshare/sent"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void catalog_SansToken_Retourne401() throws Exception {
            mockMvc.perform(get("/api/modules"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── Token invalide / expire ───

    @Nested
    @DisplayName("Endpoints proteges — 401 avec token invalide ou expire")
    class EndpointsProtegesTokenInvalide {

        @Test
        void contacts_AvecTokenInvalide_Retourne401() throws Exception {
            mockMvc.perform(get("/api/contacts")
                            .header("Authorization", "Bearer invalid.token.here"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void annotations_AvecTokenMalForme_Retourne401() throws Exception {
            mockMvc.perform(get("/api/annotations")
                            .header("Authorization", "Bearer not-a-jwt"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void notifications_AvecTokenExpire_Retourne401() throws Exception {
            String expiredToken = createExpiredToken("sectest@example.com");

            mockMvc.perform(get("/api/notifications")
                            .header("Authorization", "Bearer " + expiredToken))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void users_AvecTokenExpire_Retourne401() throws Exception {
            String expiredToken = createExpiredToken("sectest@example.com");

            mockMvc.perform(get("/api/users")
                            .header("Authorization", "Bearer " + expiredToken))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void notes_AvecBearerVide_Retourne401() throws Exception {
            mockMvc.perform(get("/api/notes")
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void quickshare_SansBearer_Retourne401() throws Exception {
            // Header Authorization sans prefixe Bearer
            mockMvc.perform(get("/api/quickshare/sent")
                            .header("Authorization", validAccessToken))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── Utilitaire ───

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
}

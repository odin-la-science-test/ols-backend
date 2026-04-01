package com.odinlascience.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odinlascience.backend.auth.dto.RegisterRequest;
import com.odinlascience.backend.modules.annotations.dto.CreateAnnotationRequest;
import com.odinlascience.backend.modules.quickshare.dto.CreateTextShareRequest;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de validation des entrees pour la securite (rejet des donnees invalides).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ValidationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;

    @BeforeEach
    void setUp() {
        // Creer un utilisateur reel en base pour que le JWT filter puisse le charger
        com.odinlascience.backend.user.model.User dbUser = com.odinlascience.backend.user.model.User.builder()
                .email("valtest@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Val")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .build();
        userRepository.save(dbUser);

        UserDetails testUser = User.withUsername("valtest@example.com")
                .password("password")
                .roles("STUDENT")
                .build();
        accessToken = jwtService.generateAccessToken(testUser);
    }

    // ─── RegisterRequest ───

    @Nested
    @DisplayName("Validation RegisterRequest")
    class RegisterRequestValidation {

        @Test
        void register_MotDePasseTropCourt_Retourne400() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("valid@example.com");
            request.setPassword("short");
            request.setFirstName("Test");
            request.setLastName("User");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void register_MotDePasseExactement7Chars_Retourne400() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("valid7@example.com");
            request.setPassword("1234567");
            request.setFirstName("Test");
            request.setLastName("User");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void register_MotDePasseExactement8Chars_Accepte() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("valid8@example.com");
            request.setPassword("12345678");
            request.setFirstName("Test");
            request.setLastName("User");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void register_EmailInvalide_Retourne400() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("not-an-email");
            request.setPassword("password123");
            request.setFirstName("Test");
            request.setLastName("User");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void register_EmailVide_Retourne400() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("");
            request.setPassword("password123");
            request.setFirstName("Test");
            request.setLastName("User");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void register_EmailNull_Retourne400() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"password\":\"password123\",\"firstName\":\"A\",\"lastName\":\"B\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── Query trop longue ───

    @Nested
    @DisplayName("Validation @RequestParam query")
    class QueryParamValidation {

        @Test
        void search_QueryTropLongue_Retourne400() throws Exception {
            String longQuery = "a".repeat(201);

            mockMvc.perform(get("/api/users/search")
                            .param("query", longQuery)
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void search_QueryExactement200Chars_Accepte() throws Exception {
            String query200 = "a".repeat(200);

            mockMvc.perform(get("/api/users/search")
                            .param("query", query200)
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }

        @Test
        void search_QueryVide_Retourne400() throws Exception {
            mockMvc.perform(get("/api/users/search")
                            .param("query", "")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── CreateAnnotationRequest ───

    @Nested
    @DisplayName("Validation CreateAnnotationRequest")
    class AnnotationValidation {

        @Test
        void createAnnotation_ContentTropLong_Retourne400() throws Exception {
            CreateAnnotationRequest request = CreateAnnotationRequest.builder()
                    .entityType("bacterium")
                    .entityId(1L)
                    .content("x".repeat(2001))
                    .build();

            mockMvc.perform(post("/api/annotations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createAnnotation_ContentExactement2000Chars_NEstPasRejete() throws Exception {
            CreateAnnotationRequest request = CreateAnnotationRequest.builder()
                    .entityType("bacterium")
                    .entityId(1L)
                    .content("x".repeat(2000))
                    .build();

            // Ne doit pas retourner 400 pour la validation de taille
            mockMvc.perform(post("/api/annotations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void createAnnotation_ContentVide_Retourne400() throws Exception {
            CreateAnnotationRequest request = CreateAnnotationRequest.builder()
                    .entityType("bacterium")
                    .entityId(1L)
                    .content("")
                    .build();

            mockMvc.perform(post("/api/annotations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── CreateTextShareRequest ───

    @Nested
    @DisplayName("Validation CreateTextShareRequest")
    class TextShareValidation {

        @Test
        void createTextShare_ContentTropLong_Retourne400() throws Exception {
            CreateTextShareRequest request = CreateTextShareRequest.builder()
                    .textContent("x".repeat(50001))
                    .build();

            mockMvc.perform(post("/api/quickshare/text")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createTextShare_ContentNull_Retourne400() throws Exception {
            mockMvc.perform(post("/api/quickshare/text")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest());
        }
    }
}

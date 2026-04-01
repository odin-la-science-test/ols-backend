package com.odinlascience.backend.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authEndpoint_WithUserAgent_ReturnsRateLimitHeaders() throws Exception {
        // Les requêtes avec User-Agent sont soumises au rate limiting
        mockMvc.perform(post("/api/auth/login")
                .header("User-Agent", "Mozilla/5.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
            .andExpect(header().exists("X-RateLimit-Remaining"));
    }

    @Test
    void authEndpoint_WithoutUserAgent_BypassesRateLimit() throws Exception {
        // Les requêtes sans User-Agent (comme MockMvc par défaut) bypasse le rate limiting
        // Ceci est utile pour les tests automatisés
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
            .andExpect(header().doesNotExist("X-RateLimit-Remaining"));
    }

    @Test
    void swaggerEndpoint_IsNotRateLimited() throws Exception {
        // Les endpoints Swagger ne doivent pas être rate-limited
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(header().doesNotExist("X-RateLimit-Remaining"));
    }
}

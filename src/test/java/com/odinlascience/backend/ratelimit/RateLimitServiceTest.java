package com.odinlascience.backend.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitServiceTest {

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService();
        // Configurer les valeurs pour les tests
        ReflectionTestUtils.setField(rateLimitService, "requestsPerMinute", 10);
        ReflectionTestUtils.setField(rateLimitService, "authRequestsPerMinute", 5);
        ReflectionTestUtils.setField(rateLimitService, "authBlockDurationMinutes", 15);
    }

    @Test
    void tryConsume_WhenUnderLimit_ReturnsTrue() {
        String ip = "192.168.1.1";
        
        // Les premières requêtes doivent passer
        for (int i = 0; i < 5; i++) {
            assertThat(rateLimitService.tryConsume(ip)).isTrue();
        }
    }

    @Test
    void tryConsume_WhenOverLimit_ReturnsFalse() {
        String ip = "192.168.1.2";
        
        // Consommer tous les tokens
        for (int i = 0; i < 10; i++) {
            rateLimitService.tryConsume(ip);
        }
        
        // La prochaine requête doit échouer
        assertThat(rateLimitService.tryConsume(ip)).isFalse();
    }

    @Test
    void tryConsumeAuth_WhenUnderLimit_ReturnsTrue() {
        String ip = "192.168.1.3";
        
        // Les premières tentatives d'auth doivent passer
        for (int i = 0; i < 3; i++) {
            assertThat(rateLimitService.tryConsumeAuth(ip)).isTrue();
        }
    }

    @Test
    void tryConsumeAuth_WhenOverLimit_ReturnsFalse() {
        String ip = "192.168.1.4";
        
        // Consommer tous les tokens auth
        for (int i = 0; i < 5; i++) {
            rateLimitService.tryConsumeAuth(ip);
        }
        
        // La prochaine tentative doit échouer
        assertThat(rateLimitService.tryConsumeAuth(ip)).isFalse();
    }

    @Test
    void differentIPs_HaveSeparateBuckets() {
        String ip1 = "192.168.1.10";
        String ip2 = "192.168.1.11";
        
        // Consommer tous les tokens de ip1
        for (int i = 0; i < 10; i++) {
            rateLimitService.tryConsume(ip1);
        }
        
        // ip1 doit être bloqué, mais ip2 doit encore pouvoir faire des requêtes
        assertThat(rateLimitService.tryConsume(ip1)).isFalse();
        assertThat(rateLimitService.tryConsume(ip2)).isTrue();
    }

    @Test
    void getAvailableTokens_ReturnsCorrectCount() {
        String ip = "192.168.1.20";
        
        long initialTokens = rateLimitService.getAvailableTokens(ip);
        assertThat(initialTokens).isEqualTo(10);
        
        rateLimitService.tryConsume(ip);
        
        long afterOneRequest = rateLimitService.getAvailableTokens(ip);
        assertThat(afterOneRequest).isEqualTo(9);
    }

    @Test
    void getAvailableAuthTokens_ReturnsCorrectCount() {
        String ip = "192.168.1.21";
        
        long initialTokens = rateLimitService.getAvailableAuthTokens(ip);
        assertThat(initialTokens).isEqualTo(5);
        
        rateLimitService.tryConsumeAuth(ip);
        
        long afterOneRequest = rateLimitService.getAvailableAuthTokens(ip);
        assertThat(afterOneRequest).isEqualTo(4);
    }

    @Test
    void authBucket_HasStricterLimitThanStandard() {
        String ip = "192.168.1.30";
        
        long standardLimit = rateLimitService.getAvailableTokens(ip);
        long authLimit = rateLimitService.getAvailableAuthTokens(ip);
        
        assertThat(authLimit).isLessThan(standardLimit);
    }
}

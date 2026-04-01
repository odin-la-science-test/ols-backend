package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.repository.EmailVerificationTokenRepository;
import com.odinlascience.backend.auth.repository.PasswordResetTokenRepository;
import com.odinlascience.backend.config.SchedulerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Nettoyage quotidien des tokens expires (reset password + verification email).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final SchedulerRegistry schedulerRegistry;

    @PostConstruct
    public void init() {
        schedulerRegistry.register(
                "token-cleanup",
                "Nettoyage quotidien des tokens expires (reset password + verification email)",
                "cron=0 0 3 * * * (3h du matin)"
        );
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int resetDeleted = passwordResetTokenRepository.deleteExpired(now);
        int verificationDeleted = emailVerificationTokenRepository.deleteExpired(now);

        if (resetDeleted > 0 || verificationDeleted > 0) {
            log.info("Tokens expires supprimes : {} reset, {} verification", resetDeleted, verificationDeleted);
        }
        schedulerRegistry.recordExecution("token-cleanup");
    }
}

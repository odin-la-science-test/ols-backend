package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.model.PasswordResetToken;
import com.odinlascience.backend.auth.repository.PasswordResetTokenRepository;
import com.odinlascience.backend.email.EmailService;
import com.odinlascience.backend.user.enums.AuthProvider;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SessionService sessionService;

    @Value("${oauth2.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Demande de reinitialisation. Toujours 200 OK (anti-enumeration).
     */
    @Transactional
    public void requestPasswordReset(String email) {
        var userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || userOpt.get().getAuthProvider() != AuthProvider.LOCAL) {
            log.debug("Reset demande pour email inconnu ou OAuth : {}", email);
            return;
        }

        User user = userOpt.get();

        tokenRepository.deleteByUserAndUsedFalse(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
        tokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        emailService.sendHtmlEmail(user.getEmail(),
                "Reinitialisation de votre mot de passe",
                "password-reset",
                Map.of("firstName", user.getFirstName(), "resetUrl", resetUrl));

        log.info("Token de reset genere pour {}", email);
    }

    /**
     * Reinitialise le mot de passe avec un token valide.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalide"));

        if (resetToken.isUsed() || resetToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expire ou deja utilise");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        sessionService.revokeAllSessions(user.getId());

        emailService.sendHtmlEmail(user.getEmail(),
                "Mot de passe modifie",
                "password-changed",
                Map.of("firstName", user.getFirstName()));

        log.info("Mot de passe reinitialise pour {}", user.getEmail());
    }
}

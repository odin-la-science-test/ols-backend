package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.model.EmailVerificationToken;
import com.odinlascience.backend.auth.repository.EmailVerificationTokenRepository;
import com.odinlascience.backend.email.EmailService;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${oauth2.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Envoie un email de verification a l'utilisateur.
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        tokenRepository.deleteByUserAndUsedFalse(user);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        tokenRepository.save(verificationToken);

        String verifyUrl = frontendUrl + "/verify-email?token=" + token;
        emailService.sendHtmlEmail(user.getEmail(),
                "Confirmez votre adresse email",
                "email-verification",
                Map.of("firstName", user.getFirstName(), "verifyUrl", verifyUrl));

        log.info("Email de verification envoye a {}", user.getEmail());
    }

    /**
     * Verifie l'email avec le token. Envoie un email de bienvenue apres verification.
     */
    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalide"));

        if (verificationToken.isUsed() || verificationToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expire ou deja utilise");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        emailService.sendHtmlEmail(user.getEmail(),
                "Bienvenue sur Odin La Science !",
                "welcome",
                Map.of("firstName", user.getFirstName(), "appUrl", frontendUrl));

        log.info("Email verifie pour {}", user.getEmail());
    }

    /**
     * Renvoie l'email de verification (si pas encore verifie).
     */
    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email deja verifie");
        }

        sendVerificationEmail(user);
    }
}

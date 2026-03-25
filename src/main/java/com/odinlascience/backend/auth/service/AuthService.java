package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.dto.AuthResponse;
import com.odinlascience.backend.auth.dto.SessionDTO;
import com.odinlascience.backend.auth.dto.TokenResponse;
import com.odinlascience.backend.auth.model.UserSession;
import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.exception.SessionLimitExceededException;
import com.odinlascience.backend.modules.common.event.NewLoginEvent;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.mapper.UserMapper;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponse login(String email, String password, String deviceInfo, String ipAddress) {
        log.info("Login attempt for user: {}", email);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'email : " + email));

        long activeCount = sessionService.countActiveSessions(user.getId());
        if (activeCount >= SessionService.MAX_SESSIONS) {
            List<SessionDTO> sessions = sessionService.getActiveSessions(user.getId(), null);
            throw new SessionLimitExceededException(sessions, SessionService.MAX_SESSIONS);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        UserSession session = sessionService.createSession(
                user, refreshToken, deviceInfo, ipAddress, jwtService.getRefreshTokenExpirationMs());
        String accessToken = jwtService.generateAccessToken(userDetails, session.getId());

        eventPublisher.publishEvent(new NewLoginEvent(email, deviceInfo, ipAddress, "/profile"));

        log.info("Login successful for user: {}", email);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .user(userMapper.toDTO(user))
                .build();
    }

    @Transactional
    public AuthResponse register(String email, String password, String firstName, String lastName,
                                 String deviceInfo, String ipAddress) {
        log.info("Registration attempt for email: {}", email);

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .build();

        User saved = userRepository.save(newUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getEmail());
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        UserSession session = sessionService.createSession(
                saved, refreshToken, deviceInfo, ipAddress, jwtService.getRefreshTokenExpirationMs());
        String accessToken = jwtService.generateAccessToken(userDetails, session.getId());

        log.info("Registration successful for user: {}", saved.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .user(userMapper.toDTO(saved))
                .build();
    }

    @Transactional
    public TokenResponse refreshToken(String oldRefreshToken) {
        String subject = jwtService.extractSubjectIgnoringExpiration(oldRefreshToken);
        if (subject == null) {
            throw new IllegalArgumentException("Token invalide");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        if (!jwtService.isRefreshTokenValid(oldRefreshToken, userDetails)) {
            throw new IllegalArgumentException("Refresh token invalide ou expire");
        }

        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        UserSession session = sessionService.validateAndRotateRefreshToken(oldRefreshToken, newRefreshToken);
        if (session == null) {
            throw new IllegalArgumentException("Session invalide ou replay detecte");
        }

        String newAccessToken = jwtService.generateAccessToken(userDetails, session.getId());
        log.debug("Token refreshed for user: {}", subject);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .build();
    }

    @Transactional
    public void logout(UUID sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        sessionService.revokeSession(sessionId, user.getId());
        log.info("Logout for user: {} session: {}", userEmail, sessionId);
    }

    @Transactional
    public void revokeSessionWithCredentials(String email, String password, UUID sessionId) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        sessionService.revokeSession(sessionId, user.getId());
        log.info("Session revoquee via credentials pour user: {} session: {}", email, sessionId);
    }
}

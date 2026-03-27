package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.dto.SessionDTO;
import com.odinlascience.backend.auth.mapper.SessionMapper;
import com.odinlascience.backend.auth.model.UserSession;
import com.odinlascience.backend.auth.repository.UserSessionRepository;
import com.odinlascience.backend.auth.util.TokenHasher;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    public static final int MAX_SESSIONS = 3;

    private final UserSessionRepository sessionRepository;

    @Transactional
    public UserSession createSession(User user, String refreshToken, String deviceInfo, String ipAddress, long refreshTokenExpirationMs) {
        UserSession session = UserSession.builder()
                .user(user)
                .refreshTokenHash(TokenHasher.hash(refreshToken))
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();
        UserSession saved = sessionRepository.save(session);
        log.info("Session creee pour user={} session={} device={}", user.getEmail(), saved.getId(), deviceInfo);
        return saved;
    }

    @Transactional(readOnly = true)
    public long countActiveSessions(Long userId) {
        return sessionRepository.countByUserIdAndExpiresAtAfter(userId, Instant.now());
    }

    @Transactional(readOnly = true)
    public List<SessionDTO> getActiveSessions(Long userId, UUID currentSessionId) {
        List<UserSession> sessions = sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return SessionMapper.toDTOList(sessions, currentSessionId);
    }

    @Transactional(readOnly = true)
    public boolean isSessionValid(UUID sessionId) {
        return sessionRepository.existsById(sessionId);
    }

    @Transactional
    public UserSession validateAndRotateRefreshToken(String oldRefreshToken, String newRefreshToken) {
        String oldHash = TokenHasher.hash(oldRefreshToken);
        return sessionRepository.findByRefreshTokenHash(oldHash).map(session -> {
            session.setRefreshTokenHash(TokenHasher.hash(newRefreshToken));
            session.setLastActiveAt(Instant.now());
            sessionRepository.save(session);
            return session;
        }).orElse(null);
    }

    @Transactional
    public void revokeSession(UUID sessionId, Long userId) {
        sessionRepository.findByIdAndUserId(sessionId, userId).ifPresent(session -> {
            sessionRepository.delete(session);
            log.info("Session revoquee: session={} user={}", sessionId, userId);
        });
    }

    @Transactional
    public void revokeAllOtherSessions(Long userId, UUID currentSessionId) {
        sessionRepository.deleteByUserIdAndIdNot(userId, currentSessionId);
        log.info("Toutes les autres sessions revoquees pour user={} sauf session={}", userId, currentSessionId);
    }

    @Transactional
    public void revokeAllSessions(Long userId) {
        sessionRepository.deleteByUserId(userId);
        log.info("Toutes les sessions revoquees pour user={}", userId);
    }

    @Transactional
    public void updateLastActive(UUID sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setLastActiveAt(Instant.now());
            sessionRepository.save(session);
        });
    }

    @Transactional
    public int cleanupExpiredSessions() {
        int deleted = sessionRepository.deleteExpiredSessions(Instant.now());
        if (deleted > 0) {
            log.info("{} sessions expirees supprimees", deleted);
        }
        return deleted;
    }
}

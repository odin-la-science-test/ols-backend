package com.odinlascience.backend.auth.repository;

import com.odinlascience.backend.auth.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    List<UserSession> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndExpiresAtAfter(Long userId, Instant now);

    Optional<UserSession> findByIdAndUserId(UUID id, Long userId);

    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.user.id = :userId AND s.id <> :currentSessionId")
    void deleteByUserIdAndIdNot(Long userId, UUID currentSessionId);

    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.user.id = :userId")
    void deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :now")
    int deleteExpiredSessions(Instant now);

    boolean existsById(UUID id);

    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);
}

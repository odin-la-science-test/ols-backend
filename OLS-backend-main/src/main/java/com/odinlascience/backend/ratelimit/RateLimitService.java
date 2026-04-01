package com.odinlascience.backend.ratelimit;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> sensitiveBuckets = new ConcurrentHashMap<>();

    @Value("${rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${rate-limit.auth.requests-per-minute:10}")
    private int authRequestsPerMinute;

    @Value("${rate-limit.auth.block-duration-minutes:15}")
    private int authBlockDurationMinutes;

    // ==================== STANDARD ====================

    private Bucket createStandardBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(requestsPerMinute).refillGreedy(requestsPerMinute, Duration.ofMinutes(1)))
            .build();
    }

    public boolean tryConsume(String ipAddress) {
        return buckets.computeIfAbsent(ipAddress, k -> createStandardBucket()).tryConsume(1);
    }

    public long getAvailableTokens(String ipAddress) {
        return buckets.computeIfAbsent(ipAddress, k -> createStandardBucket()).getAvailableTokens();
    }

    // ==================== AUTH ====================

    private Bucket createAuthBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(authRequestsPerMinute).refillIntervally(authRequestsPerMinute, Duration.ofMinutes(1)))
            .addLimit(limit -> limit.capacity(5).refillIntervally(5, Duration.ofSeconds(10)))
            .build();
    }

    public boolean tryConsumeAuth(String ipAddress) {
        return authBuckets.computeIfAbsent(ipAddress, k -> createAuthBucket()).tryConsume(1);
    }

    public long getAvailableAuthTokens(String ipAddress) {
        return authBuckets.computeIfAbsent(ipAddress, k -> createAuthBucket()).getAvailableTokens();
    }

    // ==================== SENSITIVE (reset password, verification email) ====================

    private Bucket createSensitiveBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(3).refillIntervally(3, Duration.ofMinutes(1)))
            .addLimit(limit -> limit.capacity(10).refillIntervally(10, Duration.ofHours(1)))
            .build();
    }

    public boolean tryConsumeSensitive(String ipAddress) {
        return sensitiveBuckets.computeIfAbsent(ipAddress, k -> createSensitiveBucket()).tryConsume(1);
    }

    public long getAvailableSensitiveTokens(String ipAddress) {
        return sensitiveBuckets.computeIfAbsent(ipAddress, k -> createSensitiveBucket()).getAvailableTokens();
    }
}

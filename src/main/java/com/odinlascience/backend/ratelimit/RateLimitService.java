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

    @Value("${rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${rate-limit.auth.requests-per-minute:10}")
    private int authRequestsPerMinute;

    @Value("${rate-limit.auth.block-duration-minutes:15}")
    private int authBlockDurationMinutes;

    private Bucket createStandardBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(requestsPerMinute).refillGreedy(requestsPerMinute, Duration.ofMinutes(1)))
            .build();
    }

    private Bucket createAuthBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(authRequestsPerMinute).refillIntervally(authRequestsPerMinute, Duration.ofMinutes(1)))
            .addLimit(limit -> limit.capacity(5).refillIntervally(5, Duration.ofSeconds(10)))
            .build();
    }

    public Bucket resolveBucket(String ipAddress) {
        return buckets.computeIfAbsent(ipAddress, k -> createStandardBucket());
    }

    public Bucket resolveAuthBucket(String ipAddress) {
        return authBuckets.computeIfAbsent(ipAddress, k -> createAuthBucket());
    }

    public boolean tryConsume(String ipAddress) {
        return resolveBucket(ipAddress).tryConsume(1);
    }

    public boolean tryConsumeAuth(String ipAddress) {
        return resolveAuthBucket(ipAddress).tryConsume(1);
    }

    public long getAvailableTokens(String ipAddress) {
        return resolveBucket(ipAddress).getAvailableTokens();
    }

    public long getAvailableAuthTokens(String ipAddress) {
        return resolveAuthBucket(ipAddress).getAvailableTokens();
    }
}

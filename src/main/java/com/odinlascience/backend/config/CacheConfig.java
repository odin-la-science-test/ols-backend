package com.odinlascience.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuration du cache pour les donnees de reference (bacteries, champignons).
 * Utilise Redis si disponible, fallback sur ConcurrentMapCache sinon.
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String BACTERIA_CACHE = "bacteria";
    public static final String FUNGI_CACHE = "fungi";
    public static final String BACTERIA_SEARCH_CACHE = "bacteria-search";
    public static final String FUNGI_SEARCH_CACHE = "fungi-search";

    @Value("${cache.ttl.reference-data:3600}")
    private long referenceDataTtlSeconds;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        try {
            connectionFactory.getConnection().close();
            log.info("Redis disponible — cache Redis active (TTL={}s)", referenceDataTtlSeconds);

            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(referenceDataTtlSeconds))
                    .disableCachingNullValues()
                    .serializeKeysWith(
                            RedisSerializationContext.SerializationPair
                                    .fromSerializer(new StringRedisSerializer())
                    );

            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(defaultConfig)
                    .build();
        } catch (Exception e) {
            log.warn("Redis indisponible — fallback sur cache en memoire : {}", e.getMessage());
            return new ConcurrentMapCacheManager(
                    BACTERIA_CACHE, FUNGI_CACHE, BACTERIA_SEARCH_CACHE, FUNGI_SEARCH_CACHE
            );
        }
    }
}

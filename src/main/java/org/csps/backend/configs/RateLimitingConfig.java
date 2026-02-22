package org.csps.backend.configs;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import lombok.Data;

/**
 * Configuration for rate limiting to prevent DDoS attacks
 * Uses Bucket4j library for token bucket algorithm
 */
@Configuration
@ConfigurationProperties(prefix = "rate-limiting")
@Data
public class RateLimitingConfig {
    
    private int requestsPerMinute = 100;
    private int requestsPerHour = 5000;
    private boolean enabled = true;
    private String[] excludedPaths = {"/api/auth/login", "/api/auth/register", "/health"};
    
    /* cache for storing rate limit buckets per client */
    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();
    
    /* get or create bucket for a specific client (IP) */
    public Bucket resolveBucket(String clientId) {
        return cache.computeIfAbsent(clientId, key -> createNewBucket());
    }
    
    /* create new token bucket with minute limit */
    private Bucket createNewBucket() {
        /* simple bandwidth: requestsPerMinute tokens per minute */
        Bandwidth limit = Bandwidth.simple(requestsPerMinute, Duration.ofMinutes(1));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }
    
    /* check if path should be excluded from rate limiting */
    public boolean isPathExcluded(String path) {
        for (String excludedPath : excludedPaths) {
            if (path.startsWith(excludedPath)) {
                return true;
            }
        }
        return false;
    }
}

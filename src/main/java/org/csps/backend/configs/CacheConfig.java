package org.csps.backend.configs;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;

/**
 * Caffeine cache configuration for high-performance in-memory caching.
 * Caches frequently accessed entities like Merch, Events, and Students.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "merch",
            "allMerchs",
            "merchSummaries",
            "event",
            "allEvents",
            "student",
            "allStudents"
        );
        
        // cache configuration: max 1000 entries, expire after 15 minutes of write, enable stats
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .recordStats());
        
        return cacheManager;
    }
}

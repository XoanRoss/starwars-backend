package org.xoanross.starwars.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(@Value("${cache.caffeine.names}") String[] cacheNames,
                                     @Value("${cache.caffeine.expire-after-write}") Duration expireAfterWrite,
                                     @Value("${cache.caffeine.maximum-size}") long maximumSize) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(cacheNames);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .maximumSize(maximumSize)
                .removalListener((o, o2, removalCause) -> log.debug("Cache entry removed: {} - reason: {}", o, removalCause)));
        return cacheManager;
    }
}

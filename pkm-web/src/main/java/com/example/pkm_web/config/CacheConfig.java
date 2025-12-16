package com.example.pkm_web.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存配置类
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置缓存管理器
     * 使用ConcurrentMapCache作为内存缓存
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<org.springframework.cache.Cache> caches = new ArrayList<>();
        caches.add(new ConcurrentMapCache("pkmCache"));
        caches.add(new ConcurrentMapCache("noteCache"));
        caches.add(new ConcurrentMapCache("categoryCache"));
        caches.add(new ConcurrentMapCache("tagCache"));
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}

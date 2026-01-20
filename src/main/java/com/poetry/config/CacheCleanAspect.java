package com.poetry.config;

import com.poetry.common.CacheClean;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheCleanAspect {

    private static final Logger log = LoggerFactory.getLogger(CacheCleanAspect.class);

    @Autowired
    private CacheManager cacheManager;

    @AfterReturning("@annotation(cacheClean)")
    public void cleanCache(JoinPoint joinPoint, CacheClean cacheClean) {
        for (String cacheName : cacheClean.cacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cache '{}' cleared after method: {}", cacheName, joinPoint.getSignature().getName());
            }
        }
    }
}

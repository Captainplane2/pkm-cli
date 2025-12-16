package com.example.pkm_web.aspect;

import com.example.pkm_web.annotation.CacheEvict;
import com.example.pkm_web.annotation.Cacheable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

/**
 * 缓存切面，处理@Cacheable和@CacheEvict注解
 */
@Aspect
@Component
@Slf4j
public class CacheAspect {

    @Autowired
    private CacheManager cacheManager;

    // SpEL表达式解析器
    private final ExpressionParser parser = new SpelExpressionParser();
    // 参数名称发现器
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 处理@Cacheable注解的切面逻辑
     */
    @Around("@annotation(cacheable)")
    public Object handleCacheable(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        // 生成缓存键
        String cacheKey = generateCacheKey(joinPoint, cacheable.key());
        // 获取缓存
        Cache cache = cacheManager.getCache("pkmCache");
        
        if (cache == null) {
            log.warn("缓存管理器中未找到名为'pkmCache'的缓存");
            return joinPoint.proceed();
        }

        // 尝试从缓存获取
        Cache.ValueWrapper cachedValue = cache.get(cacheKey);
        if (cachedValue != null) {
            log.debug("缓存命中: key={}", cacheKey);
            return cachedValue.get();
        }

        log.debug("缓存未命中: key={}", cacheKey);
        // 执行目标方法
        Object result = joinPoint.proceed();
        // 将结果存入缓存
        if (result != null) {
            cache.put(cacheKey, result);
            log.debug("缓存设置成功: key={}, ttl={}{}", 
                     cacheKey, cacheable.ttl(), cacheable.unit());
        }
        return result;
    }

    /**
     * 处理@CacheEvict注解的切面逻辑
     */
    @After("@annotation(cacheEvict)")
    public void handleCacheEvict(JoinPoint joinPoint, CacheEvict cacheEvict) {
        Cache cache = cacheManager.getCache("pkmCache");
        
        if (cache == null) {
            log.warn("缓存管理器中未找到名为'pkmCache'的缓存");
            return;
        }

        if (cacheEvict.allEntries()) {
            // 清除所有缓存
            cache.clear();
            log.debug("清除所有缓存");
        } else if (!cacheEvict.pattern().isEmpty()) {
            // 根据模式清除缓存
            clearCacheByPattern(cacheEvict.pattern());
        } else if (!cacheEvict.key().isEmpty()) {
            // 清除指定键的缓存
            String cacheKey = generateCacheKey(joinPoint, cacheEvict.key());
            cache.evict(cacheKey);
            log.debug("清除缓存: key={}", cacheKey);
        }
    }

    /**
     * 生成缓存键（支持SpEL表达式）
     */
    private String generateCacheKey(JoinPoint joinPoint, String keyPattern) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(),
                signature.getMethod(),
                joinPoint.getArgs(),
                parameterNameDiscoverer
        );

        // 设置方法信息到上下文
        context.setVariable("methodName", signature.getMethod().getName());
        context.setVariable("className", signature.getDeclaringType().getSimpleName());

        // 解析SpEL表达式
        String evaluatedKey = parser.parseExpression(keyPattern).getValue(context, String.class);
        return "pkm:" + evaluatedKey;
    }

    /**
     * 根据模式清除缓存
     */
    private void clearCacheByPattern(String pattern) {
        // 简单实现，实际项目中可以根据需要扩展
        log.debug("根据模式清除缓存: pattern={}", pattern);
        // 注意：这里只是记录日志，实际清除逻辑需要根据具体的缓存实现来完成
        // 如果使用Redis等缓存，可以使用Redis的keys命令匹配并删除
    }
}

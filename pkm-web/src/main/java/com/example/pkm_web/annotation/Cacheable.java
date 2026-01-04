package com.example.pkm_web.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义缓存注解，用于标记需要缓存结果的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {
    /**
     * 缓存键（支持SpEL表达式）
     */
    String key();

    /**
     * 过期时间（默认300秒）
     */
    long ttl() default 300;

    /**
     * 时间单位（默认秒）
     */
    TimeUnit unit() default TimeUnit.SECONDS;
}

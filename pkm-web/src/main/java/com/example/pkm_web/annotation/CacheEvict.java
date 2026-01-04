package com.example.pkm_web.annotation;

import java.lang.annotation.*;

/**
 * 自定义清除缓存注解，用于标记需要清除缓存的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {
    /**
     * 要清除的缓存键（支持SpEL表达式）
     */
    String key() default "";

    /**
     * 是否清除所有缓存条目
     */
    boolean allEntries() default false;

    /**
     * 缓存键模式（支持通配符）
     */
    String pattern() default "";
}

package com.example.pkm_web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 性能监控注解,用于标记需要进行性能监控的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceMonitor {
    /**
     * 方法描述
     */
    String value() default "";
    
    /**
     * 性能阈值（毫秒），超过该值将记录警告日志
     */
    long threshold() default 1000;
    
    /**
     * 是否记录详细参数
     */
    boolean recordParams() default true;
    
    /**
     * 是否记录返回值
     */
    boolean recordResult() default false;
}
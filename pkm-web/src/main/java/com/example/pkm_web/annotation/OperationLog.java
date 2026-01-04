package com.example.pkm_web.annotation;

import java.lang.annotation.*;

/**
 * 自定义操作日志注解，用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD) // 注解作用目标：方法
@Retention(RetentionPolicy.RUNTIME) // 注解保留策略：运行时
@Documented // 注解是否包含在 JavaDoc 中    
public @interface OperationLog {
    String value() default "";
    OperationType type() default OperationType.QUERY;
    enum OperationType {
        CREATE, UPDATE, DELETE, QUERY, EXPORT
    }
}

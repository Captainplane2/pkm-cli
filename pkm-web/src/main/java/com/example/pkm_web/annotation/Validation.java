package com.example.pkm_web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数验证注解
 * 用于标记需要进行参数验证的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {
    /**
     * 是否允许空值
     */
    boolean allowEmpty() default false;
    
    /**
     * 参数长度限制
     */
    int maxLength() default -1;
    
    /**
     * 参数正则表达式验证
     */
    String regex() default "";
    
    /**
     * 验证分组
     */
    Class<?>[] groups() default {};
}

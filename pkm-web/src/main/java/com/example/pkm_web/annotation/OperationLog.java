package com.example.pkm_web.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    String value() default "";
    OperationType type() default OperationType.QUERY;
    enum OperationType {
        CREATE, UPDATE, DELETE, QUERY, EXPORT
    }
}

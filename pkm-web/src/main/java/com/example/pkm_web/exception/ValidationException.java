package com.example.pkm_web.exception;

/**
 * 参数验证异常
 */
public class ValidationException extends PKMException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ValidationException(String field, String reason) {
        super("VALIDATION_ERROR",
                String.format("字段 '%s' 验证失败: %s", field, reason));
    }
}
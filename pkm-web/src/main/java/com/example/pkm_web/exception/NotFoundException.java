package com.example.pkm_web.exception;

/**
 * 资源未找到异常
 */
public class NotFoundException extends PKMException {
    public NotFoundException(String resourceName, String identifier) {
        super("NOT_FOUND",
                String.format("%s 未找到: %s", resourceName, identifier));
    }

    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
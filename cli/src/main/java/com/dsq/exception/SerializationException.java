package com.dsq.exception;

/**
 * 序列化异常
 */
public class SerializationException extends PKMException {
    public SerializationException(String operation, Throwable cause) {
        super("SERIALIZATION_ERROR",
                "序列化操作失败: " + operation + ", 原因: " + cause.getMessage(),
                cause);
    }

    public SerializationException(String operation, String reason) {
        super("SERIALIZATION_ERROR",
                "序列化操作失败: " + operation + ", 原因: " + reason);
    }
}
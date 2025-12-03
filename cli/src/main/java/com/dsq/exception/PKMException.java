package com.dsq.exception;

/**
 * PKM系统基础异常类
 */
public class PKMException extends Exception {
    private final String errorCode;

    //构造方法
    public PKMException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PKMException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("PKMException{errorCode='%s', message='%s'}", errorCode, getMessage());
    }
}
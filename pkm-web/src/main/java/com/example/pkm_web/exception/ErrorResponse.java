package com.example.pkm_web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 统一错误响应对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final String errorCode;
    private final String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final String path;
    private final String detail;

    public ErrorResponse(String errorCode, String message, String path, String detail) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.detail = detail;
    }

    // Getters
    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPath() { return path; }
    public String getDetail() { return detail; }
}
package com.dsq.exception;

/**
 * 文件操作专用异常
 */
public class FileOperationException extends PKMException {
    public FileOperationException(String operation, String filePath, Throwable cause) {
        super("FILE_OPERATION_FAILED",
                String.format("文件操作失败: %s [文件: %s], 原因: %s",
                        operation, filePath, cause.getMessage()),
                cause);
    }

    public FileOperationException(String operation, String filePath, String reason) {
        super("FILE_OPERATION_FAILED",
                String.format("文件操作失败: %s [文件: %s], 原因: %s",
                        operation, filePath, reason));
    }
}
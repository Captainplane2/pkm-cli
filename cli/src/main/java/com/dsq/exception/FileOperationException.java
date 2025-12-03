package com.dsq.exception;


/**
 * 文件操作异常类，继承自PKMException
 * 用于处理文件操作过程中可能出现的各种异常情况
 */
public class FileOperationException extends PKMException {
    /**
     * 构造函数，创建一个文件操作异常
     * @param operation 具体的文件操作类型，如读取、写入等
     * @param filePath 发生异常的文件路径
     * @param cause 导致异常的原始异常原因
     */
    public FileOperationException(String operation, String filePath, Throwable cause) {
        super("FILE_OPERATION_FAILED",
                String.format("文件操作失败: %s [文件: %s], 原因: %s",
                        operation, filePath, cause.getMessage()),
                cause);
    }

    /**
     * 构造函数，创建一个文件操作异常
     * @param operation 具体的文件操作类型，如读取、写入等
     * @param filePath 发生异常的文件路径
     * @param reason 异常的具体原因描述
     */
    public FileOperationException(String operation, String filePath, String reason) {
        super("FILE_OPERATION_FAILED",
                String.format("文件操作失败: %s [文件: %s], 原因: %s",
                        operation, filePath, reason));
    }
}
package com.dsq.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testPKMException() {
        PKMException exception = new PKMException("TEST_ERROR", "测试异常");
        assertEquals("TEST_ERROR", exception.getErrorCode());
        assertEquals("测试异常", exception.getMessage());
    }

    @Test
    void testFileOperationException() {
        Exception cause = new IOException("磁盘空间不足");
        FileOperationException exception = new FileOperationException("保存文件", "test.dat", cause);

        assertEquals("FILE_OPERATION_FAILED", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("保存文件"));
        assertTrue(exception.getMessage().contains("test.dat"));
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testSerializationException() {
        Exception cause = new ClassNotFoundException("类未找到");
        SerializationException exception = new SerializationException("反序列化", cause);

        assertEquals("SERIALIZATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("反序列化"));
        assertEquals(cause, exception.getCause());
    }
}
package com.example.pkm_web.aspect;

import com.example.pkm_web.exception.ValidationException;
import com.example.pkm_web.model.Note;
import com.example.pkm_web.service.NoteService;
import com.example.pkm_web.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 参数验证切面测试
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ValidationAspectTest {

    @Autowired
    private NoteService noteService;

    @MockBean
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        // 重置mock
        Mockito.reset(noteRepository);
    }

    /**
     * 测试参数验证切面 - 非空参数验证
     */
    @Test
    void testValidationAspect_RequiredParams() {
        // 测试空ID参数
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteService.getNoteById(null);
        });
        assertEquals("id", exception.getParamName());
        assertTrue(exception.getMessage().contains("不能为空"));

        // 测试空标题参数
        exception = assertThrows(ValidationException.class, () -> {
            noteService.createNote(null, "测试内容");
        });
        assertEquals("title", exception.getParamName());
        assertTrue(exception.getMessage().contains("不能为空"));
    }

    /**
     * 测试参数验证切面 - JSR-303模型验证
     */
    @Test
    void testValidationAspect_Jsr303Validation() {
        // 测试标题过长
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            // 创建一个超过200个字符的标题
            String longTitle = "a".repeat(250);
            noteService.createNote(longTitle, "测试内容");
        });
        assertTrue(exception.getMessage().contains("长度不能超过"));

        // 测试内容过长
        exception = assertThrows(ValidationException.class, () -> {
            // 创建一个超过10000个字符的内容
            String longContent = "a".repeat(11000);
            noteService.createNote("测试标题", longContent);
        });
        assertTrue(exception.getMessage().contains("长度不能超过"));
    }

    /**
     * 测试参数验证切面 - 验证通过的情况
     */
    @Test
    void testValidationAspect_ValidationPassed() {
        // 准备测试数据
        String title = "测试标题";
        String content = "测试内容";
        Note savedNote = new Note("1", title, content);

        // 模拟noteRepository的行为
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        // 调用带验证的方法，应该成功
        Note result = noteService.createNote(title, content);

        // 验证方法被调用
        verify(noteRepository, times(1)).save(any(Note.class));
        
        // 验证结果
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
    }

    /**
     * 测试参数验证切面 - 模型对象验证
     */
    @Test
    void testValidationAspect_ModelValidation() {
        // 准备测试数据
        Note existingNote = new Note("1", "现有笔记", "现有内容");
        Note invalidNote = new Note("1", "", "测试内容"); // 空标题

        // 模拟noteRepository的行为
        when(noteRepository.findById("1")).thenReturn(Optional.of(existingNote));

        // 测试空标题的模型对象
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteService.updateNote(invalidNote);
        });
        assertTrue(exception.getMessage().contains("标题不能为空"));
    }

    /**
     * 测试参数验证切面 - 标签非空验证
     */
    @Test
    void testValidationAspect_TagValidation() {
        // 准备测试数据
        Note existingNote = new Note("1", "测试笔记", "内容");

        // 模拟noteRepository的行为
        when(noteRepository.findById("1")).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(any(Note.class))).thenReturn(existingNote);

        // 测试空标签参数
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            noteService.addTagToNote("1", null);
        });
        assertEquals("tag", exception.getParamName());
        assertTrue(exception.getMessage().contains("不能为空"));

        exception = assertThrows(ValidationException.class, () -> {
            noteService.addTagToNote("1", "");
        });
        assertEquals("tag", exception.getParamName());
        assertTrue(exception.getMessage().contains("不能为空"));
    }
}
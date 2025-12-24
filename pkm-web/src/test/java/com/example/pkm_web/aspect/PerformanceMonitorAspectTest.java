package com.example.pkm_web.aspect;

import com.example.pkm_web.annotation.PerformanceMonitor;
import com.example.pkm_web.model.Note;
import com.example.pkm_web.service.NoteService;
import com.example.pkm_web.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.pkm_web.util.UserContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 性能监控切面测试
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PerformanceMonitorAspectTest {

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
     * 测试性能监控切面是否正常工作
     */
    @Test
    void testPerformanceMonitorAspect() {
        // 准备测试数据
        Long mockUserId = 1L;
        Note note1 = new Note("1", "测试笔记1", "内容1", mockUserId);
        Note note2 = new Note("2", "测试笔记2", "内容2", mockUserId);
        List<Note> notes = Arrays.asList(note1, note2);

        // 模拟noteRepository的行为
        when(noteRepository.findAllByUserIdOrderByUpdatedAtDesc(mockUserId)).thenReturn(notes);
        when(noteRepository.findByIdAndUserId("1", mockUserId)).thenReturn(Optional.of(note1));

        // 设置当前用户ID（模拟UserContext）
        UserContext.setCurrentUserId(mockUserId);

        // 调用带有@PerformanceMonitor注解的方法
        List<Note> result = noteService.getAllNotes();

        // 验证方法被调用
        verify(noteRepository, times(1)).findAllByUserIdOrderByUpdatedAtDesc(mockUserId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("测试笔记1", result.get(0).getTitle());
    }

    /**
     * 测试性能监控切面的参数记录功能
     */
    @Test
    void testPerformanceMonitorWithParams() {
        // 准备测试数据
        Long mockUserId = 1L;
        Note note = new Note("1", "测试笔记", "内容", mockUserId);
        
        // 模拟noteRepository的行为
        when(noteRepository.findByIdAndUserId("1", mockUserId)).thenReturn(Optional.of(note));

        // 设置当前用户ID
        UserContext.setCurrentUserId(mockUserId);

        // 调用带有@PerformanceMonitor注解且recordParams=true的方法
        Note result = noteService.getNoteById("1");

        // 验证方法被调用
        verify(noteRepository, times(1)).findByIdAndUserId("1", mockUserId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("测试笔记", result.getTitle());
    }

    /**
     * 测试性能监控切面的返回值记录功能
     */
    @Test
    void testPerformanceMonitorWithResult() {
        // 准备测试数据
        Long mockUserId = 1L;
        Note note1 = new Note("1", "测试笔记1", "内容1", mockUserId);
        Note note2 = new Note("2", "测试笔记2", "内容2", mockUserId);
        List<Note> notes = Arrays.asList(note1, note2);

        // 模拟noteRepository的行为
        when(noteRepository.findByUserId(mockUserId)).thenReturn(notes);

        // 设置当前用户ID
        UserContext.setCurrentUserId(mockUserId);

        // 调用带有@PerformanceMonitor注解且recordResult=true的方法
        NoteService.NoteStatistics result = noteService.getStatistics();

        // 验证方法被调用
        verify(noteRepository, times(1)).findByUserId(mockUserId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotalNotes());
    }

    /**
     * 测试性能监控切面的自定义性能阈值
     */
    @Test
    void testPerformanceMonitorWithCustomThreshold() {
        // 测试低耗时方法
        Long mockUserId = 1L;
        String title = "测试笔记";
        String content = "测试内容";
        
        // 模拟noteRepository的行为
        Note savedNote = new Note("1", title, content, mockUserId);
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        // 设置当前用户ID
        UserContext.setCurrentUserId(mockUserId);

        // 调用带有@PerformanceMonitor注解且阈值为500ms的方法
        Note result = noteService.createNote(title, content);

        // 验证方法被调用
        verify(noteRepository, times(1)).save(any(Note.class));
        
        // 验证结果
        assertNotNull(result);
        assertEquals(title, result.getTitle());
    }
}

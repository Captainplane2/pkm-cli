package com.dsq.repository;

import com.dsq.Note;
import com.dsq.ExportFormat;
import com.dsq.exception.FileOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteFileRepositoryTest {
    private NoteFileRepository repository;
    private final String testDataFile = "test_notes.dat";
    private final String exportFile = "test_export.txt";

    @BeforeEach
    void setUp() {
        repository = new NoteFileRepository(testDataFile);
        // 清理测试文件
        new File(testDataFile).delete();
        new File(exportFile).delete();
        new File(testDataFile + ".backup").delete();
    }

    @AfterEach
    void tearDown() {
        // 清理测试文件
        new File(testDataFile).delete();
        new File(exportFile).delete();
        new File(testDataFile + ".backup").delete();
    }

    @Test
    void testSaveAndLoadNotes() throws FileOperationException {
        // 创建测试笔记
        Note note1 = new Note("1", "Java学习", "今天学习了Java基础");
        note1.addTag("编程");
        note1.addTag("Java");

        Note note2 = new Note("2", "Spring框架", "Spring核心概念学习");
        note2.addTag("Java");
        note2.addTag("框架");

        List<Note> originalNotes = Arrays.asList(note1, note2);

        // 保存笔记
        repository.saveNotes(originalNotes);
        assertTrue(repository.dataFileExists());

        // 加载笔记
        List<Note> loadedNotes = repository.loadNotes();

        // 验证数据一致性
        assertEquals(2, loadedNotes.size());
        assertEquals("Java学习", loadedNotes.get(0).getTitle());
        assertEquals("Spring框架", loadedNotes.get(1).getTitle());
        assertTrue(loadedNotes.get(0).hasTag("Java"));
        assertTrue(loadedNotes.get(1).hasTag("框架"));
    }

    @Test
    void testLoadEmptyFile() throws FileOperationException {
        // 测试加载不存在的文件
        List<Note> notes = repository.loadNotes();
        assertTrue(notes.isEmpty());
    }

    @Test
    void testExportToText() throws FileOperationException {
        // 创建测试笔记
        Note note = new Note("1", "测试笔记", "这是测试内容\n包含多行文本");
        note.addTag("测试");
        note.addTag("示例");

        List<Note> notes = Arrays.asList(note);

        // 导出为文本格式
        repository.exportNotes(notes, exportFile, ExportFormat.TEXT);

        // 验证导出文件存在
        File exportedFile = new File(exportFile);
        assertTrue(exportedFile.exists());
        assertTrue(exportedFile.length() > 0);
    }

    @Test
    void testBackupMechanism() throws FileOperationException {
        // 创建初始笔记并保存（第一次保存，不会创建备份，因为原文件不存在）
        Note note1 = new Note("1", "初始笔记", "初始内容");
        List<Note> initialNotes = Arrays.asList(note1);
        repository.saveNotes(initialNotes);

        // 验证第一次保存时备份文件不存在（因为原文件之前不存在）
        File backupFile = new File(testDataFile + ".backup");
        assertFalse(backupFile.exists(), "第一次保存时不应该创建备份");

        // 第二次保存（此时原文件已存在，应该创建备份）
        Note note2 = new Note("2", "第二次笔记", "第二次内容");
        List<Note> secondNotes = Arrays.asList(note1, note2);
        repository.saveNotes(secondNotes);

        // 验证第二次保存时备份文件存在
        assertTrue(backupFile.exists(), "第二次保存时应该创建备份");

        // 加载验证数据正确性
        List<Note> loadedNotes = repository.loadNotes();
        assertEquals(2, loadedNotes.size());
        assertEquals("第二次笔记", loadedNotes.get(1).getTitle());
    }

    @Test
    void testSaveNullNotes() {
        // 测试保存空笔记列表
        assertThrows(FileOperationException.class, () -> {
            repository.saveNotes(null);
        });
    }
}
package com.dsq;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void testNoteCreation() {
        Note note = new Note("1", "测试标题", "测试内容");
        assertEquals("1", note.getId());
        assertEquals("测试标题", note.getTitle());
        assertTrue(note.getTags().isEmpty());
    }

    @Test
    void testTagManagement() {
        Note note = new Note("测试", "内容");

        note.addTag("重要");
        note.addTag("工作");
        assertTrue(note.hasTag("重要"));
        assertEquals(2, note.getTags().size());

        note.removeTag("重要");
        assertFalse(note.hasTag("重要"));
        assertEquals(1, note.getTags().size());
    }

    @Test
    void testTextNoteInheritance() {
        TextNote textNote = new TextNote("1", "文本笔记", "内容", "摘要");
        assertTrue(textNote instanceof Note);
        assertEquals("摘要", textNote.getSummary());
    }
}
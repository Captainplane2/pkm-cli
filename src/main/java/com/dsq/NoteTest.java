package com.dsq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class NoteTest {
    private Note note;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        note = new Note("Test Title", "Test Content");
        tag1 = new Tag(1L, "Important");
        tag2 = new Tag(2L, "Urgent");
    }

    @Test
    void testNoteCreation() {
        assertNotNull(note.getCreatedAt());
        assertNotNull(note.getUpdatedAt());
        assertEquals("Test Title", note.getTitle());
        assertEquals("Test Content", note.getContent());
        assertTrue(note.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(note.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testAddTag() {
        note.addTag(tag1);
        note.addTag(tag2);

        assertTrue(note.getTags().contains(tag1));
        assertTrue(note.getTags().contains(tag2));
        assertEquals(2, note.getTags().size());
    }

    @Test
    void testRemoveTag() {
        note.addTag(tag1);
        note.addTag(tag2);

        note.removeTag(tag1);

        assertFalse(note.getTags().contains(tag1));
        assertTrue(note.getTags().contains(tag2));
        assertEquals(1, note.getTags().size());
    }

    @Test
    void testUpdateContentUpdatesTimestamp() {
        LocalDateTime originalUpdateTime = note.getUpdatedAt();

        // 使用小延迟确保时间差
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        note.updateContent("New Content");

        assertEquals("New Content", note.getContent());
        assertTrue(note.getUpdatedAt().isAfter(originalUpdateTime));
    }

    @Test
    void testUpdateTitleUpdatesTimestamp() {
        LocalDateTime originalUpdateTime = note.getUpdatedAt();

        // 使用小延迟确保时间差
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        note.updateTitle("New Title");

        assertEquals("New Title", note.getTitle());
        assertTrue(note.getUpdatedAt().isAfter(originalUpdateTime));
    }

    @Test
    void testGetTagNames() {
        note.addTag(tag1);
        note.addTag(tag2);

        // 明确指定类型，不使用 var
        Set<String> tagNames = note.getTagNames();

        assertTrue(tagNames.contains("Important"));
        assertTrue(tagNames.contains("Urgent"));
        assertEquals(2, tagNames.size());
    }

    @Test
    void testInheritance() {
        TextNote textNote = new TextNote("Inheritance", "Testing inheritance", "Summary");

        assertTrue(textNote instanceof Note); // 多态的体现
        assertEquals("Inheritance", textNote.getTitle()); // 继承来的方法
        assertEquals("Summary", textNote.getSummary()); // 子类特有方法
    }

    @Test
    void testNoteEquality() {
        Note note1 = new Note(1L, "Title", "Content");
        Note note2 = new Note(1L, "Different Title", "Different Content");
        Note note3 = new Note(2L, "Title", "Content");

        assertEquals(note1, note2); // 相同ID
        assertNotEquals(note1, note3); // 不同ID
    }

    @Test
    void testClearTags() {
        note.addTag(tag1);
        note.addTag(tag2);
        assertEquals(2, note.getTags().size());

        note.clearTags();

        assertEquals(0, note.getTags().size());
        assertTrue(note.getTags().isEmpty());
    }

    @Test
    void testHasTag() {
        note.addTag(tag1);

        assertTrue(note.hasTag(tag1));
        assertFalse(note.hasTag(tag2));
    }

    @Test
    void testAddNullTag() {
        int originalSize = note.getTags().size();
        note.addTag(null);

        // 添加null标签不应该改变tags集合
        assertEquals(originalSize, note.getTags().size());
    }

    @Test
    void testRemoveNullTag() {
        note.addTag(tag1);
        int originalSize = note.getTags().size();

        note.removeTag(null);

        // 移除null标签不应该改变tags集合
        assertEquals(originalSize, note.getTags().size());
    }
}
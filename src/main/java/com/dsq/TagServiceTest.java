package com.dsq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TagServiceTest {
    private TagService tagService;
    private Note note1, note2;

    @BeforeEach
    void setUp() {
        tagService = new TagService();

        note1 = new Note("1", "Java学习", "内容");
        note1.addTag("java");
        note1.addTag("编程");

        note2 = new Note("2", "Spring教程", "内容");
        note2.addTag("java");
        note2.addTag("spring");

        tagService.indexNote(note1);
        tagService.indexNote(note2);
    }

    @Test
    void testIndexAndSearch() {
        List<Note> javaNotes = tagService.findNotesByTag("java");
        assertEquals(2, javaNotes.size());

        List<Note> springNotes = tagService.findNotesByTag("spring");
        assertEquals(1, springNotes.size());
    }

    @Test
    void testTagStatistics() {
        Map<String, Integer> stats = tagService.getTagStatistics();
        assertEquals(2, stats.get("java").intValue());
        assertEquals(1, stats.get("spring").intValue());
    }

    @Test
    void testRemoveNote() {
        tagService.removeNote(note1);
        List<Note> javaNotes = tagService.findNotesByTag("java");
        assertEquals(1, javaNotes.size());
    }
}
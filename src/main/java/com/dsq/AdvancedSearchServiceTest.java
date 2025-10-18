package com.dsq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AdvancedSearchServiceTest {
    private AdvancedSearchService searchService;
    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagService = new TagService();
        searchService = new AdvancedSearchService(tagService);

        Note note1 = new Note("1", "Java基础", "内容");
        note1.addTag("java");
        note1.addTag("基础");

        Note note2 = new Note("2", "Spring框架", "内容");
        note2.addTag("java");
        note2.addTag("spring");
        note2.addTag("框架");

        tagService.indexNote(note1);
        tagService.indexNote(note2);
    }

    @Test
    void testMultipleTagsAND() {
        List<String> tags = Arrays.asList("java", "spring");
        List<Note> result = searchService.searchByMultipleTagsAND(tags);

        assertEquals(1, result.size());
        assertEquals("Spring框架", result.get(0).getTitle());
    }

    @Test
    void testFuzzySearch() {
        List<Note> result = searchService.fuzzySearchByTag("jav");
        assertEquals(2, result.size());

        result = searchService.fuzzySearchByTag("框");
        assertEquals(1, result.size());
    }
}
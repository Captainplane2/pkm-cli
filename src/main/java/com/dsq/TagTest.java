package com.dsq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag(1L, "Important");
    }

    @Test
    void testTagCreation() {
        assertNotNull(tag);
        assertEquals(1L, tag.getId());
        assertEquals("Important", tag.getName());
    }

    @Test
    void testTagEquality() {
        Tag sameTag = new Tag(1L, "Important");
        Tag differentTag = new Tag(2L, "Urgent");

        assertEquals(tag, sameTag);
        assertNotEquals(tag, differentTag);
    }

    @Test
    void testTagHashCode() {
        Tag sameTag = new Tag(1L, "Important");

        assertEquals(tag.hashCode(), sameTag.hashCode());
    }

    @Test
    void testTagToString() {
        String toString = tag.toString();
        assertTrue(toString.contains("Important"));
        assertTrue(toString.contains("1"));
    }
}

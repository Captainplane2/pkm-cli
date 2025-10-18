package com.dsq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 笔记实体类 - 根据实验指导书要求
 */
public class Note {
    private String id;
    private String title;
    private String content;
    private List<String> tags = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 精简构造方法
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Note(String id, String title, String content) {
        this(title, content);
        this.id = id;
    }

    // Getter和Setter
    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getTags() { return new ArrayList<>(tags); }
    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags != null ? tags : new ArrayList<>());
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // 标签管理方法 - 实验指导书要求
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !tags.contains(tag.trim())) {
            tags.add(tag.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeTag(String tag) {
        if (tag != null && tags.remove(tag.trim())) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean hasTag(String tag) {
        return tag != null && tags.contains(tag.trim());
    }

    // 实用方法
    public boolean hasAllTags(List<String> tagList) {
        return tagList != null && tagList.stream().allMatch(this::hasTag);
    }

    public boolean hasAnyTag(List<String> tagList) {
        return tagList != null && tagList.stream().anyMatch(this::hasTag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Note{id='%s', title='%s', tags=%s}", id, title, tags);
    }
}
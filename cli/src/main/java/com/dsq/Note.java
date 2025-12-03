package com.dsq;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 笔记实体类 - 实现序列化支持
 */
public class Note implements Serializable {
    private static final long serialVersionUID = 1L; //序列化版本号

    private String id;
    private String title;
    private String content;
    private List<String> tags = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造方法
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

    // 标签管理方法
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
        if (tagList == null || tagList.isEmpty()) {
            return false;
        }
        return tagList.stream().allMatch(this::hasTag);
    }

    public boolean hasAnyTag(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            return false;
        }
        return tagList.stream().anyMatch(this::hasTag);
    }

    /**
     * 重写equals方法，用于比较两个Note对象是否相等
     * @param o 要比较的对象
     * @return 如果两个对象相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object o) {
        // 如果是同一个对象实例，直接返回true
        if (this == o) return true;
        // 如果对象为null或两个对象的类不同，返回false
        if (o == null || getClass() != o.getClass()) return false;
        // 将对象强制转换为Note类型
        Note note = (Note) o;
        // 比较两个对象的id属性是否相等
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
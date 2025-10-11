package com.dsq;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 笔记基类
 * 包含所有笔记的通用属性和行为
 */
public class Note {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 多对多关联关系：一个笔记可以有多个标签
    private Set<Tag> tags = new HashSet<>();

    // 构造方法
    public Note() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Note(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    public Note(Long id, String title, String content) {
        this(title, content);
        this.id = id;
    }

    // Getter 方法
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // 返回 tags 的副本以保护封装性
    public Set<Tag> getTags() {
        return new HashSet<>(tags);
    }

    // Setter 方法
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 关联关系管理方法
    public void addTag(Tag tag) {
        if (tag != null) {
            this.tags.add(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeTag(Tag tag) {
        if (tag != null) {
            this.tags.remove(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void clearTags() {
        this.tags.clear();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasTag(Tag tag) {
        return this.tags.contains(tag);
    }

    // 业务方法
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
        this.updatedAt = LocalDateTime.now();
    }

    // 实用方法：获取标签名称列表
    public Set<String> getTagNames() {
        Set<String> tagNames = new HashSet<>();
        for (Tag tag : tags) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }

    // 重写 equals 和 hashCode 方法，基于 id
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

    // 重写 toString 方法
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + (content.length() > 50 ? content.substring(0, 47) + "..." : content) + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", tags=" + tags +
                '}';
    }
}
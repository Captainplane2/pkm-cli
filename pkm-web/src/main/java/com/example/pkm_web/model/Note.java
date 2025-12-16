package com.example.pkm_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 笔记实体类 - 实现序列化支持
 */
@Entity
@Table(name = "notes")
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(nullable = false)
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Size(max = 10000, message = "内容长度不能超过10000个字符")
    private String content;

    @ElementCollection
    @CollectionTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "category_id")
    private String categoryId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造方法
    public Note() {}

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


    // Getter和Setter保持不变...
    // [原有的getter/setter方法保持不变]
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
package com.dsq;

import java.io.Serializable;

/**
 * 文本笔记子类 - 实现序列化支持
 */
public class TextNote extends Note implements Serializable {
    private static final long serialVersionUID = 1L;

    private String summary;

    // 构造方法
    public TextNote(String title, String content) {
        super(title, content);
    }

    public TextNote(String id, String title, String content) {
        super(id, title, content);
    }

    public TextNote(String id, String title, String content, String summary) {
        super(id, title, content);
        this.summary = summary;
    }

    // Getter和Setter
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    @Override
    public String toString() {
        return String.format("TextNote{id='%s', title='%s', summary='%s', tags=%s}",
                getId(), getTitle(), summary, getTags());
    }
}
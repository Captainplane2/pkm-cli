package com.dsq;

/**
 * 文本笔记子类 - 精简设计，避免构造方法冲突
 */
public class TextNote extends Note {
    private String summary;

    // 只保留必要的构造方法，避免冲突
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
package com.dsq;

/**
 * 文本笔记子类
 * 继承自Note基类，可以添加摘要信息
 */
public class TextNote extends Note {
    private String summary;

    // 构造方法
    public TextNote(String title, String content) {
        super(title, content);
    }

    public TextNote(String title, String content, String summary) {
        super(title, content);
        this.summary = summary;
    }

    public TextNote(Long id, String title, String content, String summary) {
        super(id, title, content);
        this.summary = summary;
    }

    // Getter 和 Setter 方法
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
        // 调用父类的更新时间方法
        updateContent(getContent()); // 这会触发updatedAt更新
    }

    // 重写 toString 方法
    @Override
    public String toString() {
        return "TextNote{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + (getContent().length() > 30 ? getContent().substring(0, 27) + "..." : getContent()) + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", tags=" + getTags() +
                '}';
    }
}
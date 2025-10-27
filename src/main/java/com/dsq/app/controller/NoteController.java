package com.dsq.app.controller;

import com.dsq.Note;
import com.dsq.app.service.NoteService;
import java.util.List;

/**
 * 笔记控制器 - 处理笔记相关的用户交互
 */
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    public void createNote(String title, String content) {
        try {
            Note newNote = noteService.createNote(title, content);
            System.out.println("✔ 笔记创建成功！");
            System.out.println("   ID: " + newNote.getId());
            System.out.println("   标题: " + newNote.getTitle());
            if (content != null && !content.trim().isEmpty()) {
                System.out.println("   内容: " +
                        (content.length() > 50 ? content.substring(0, 47) + "..." : content));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✘ 创建笔记失败: " + e.getMessage());
        }
    }

    public void listAllNotes() {
        try {
            List<Note> notes = noteService.getAllNotes();
            if (notes.isEmpty()) {
                System.out.println("暂无笔记");
                return;
            }

            System.out.println("共有 " + notes.size() + " 条笔记:");
            for (int i = 0; i < notes.size(); i++) {
                Note note = notes.get(i);
                String tags = note.getTags().isEmpty() ? "" : " [" + String.join(", ", note.getTags()) + "]";
                System.out.printf("%d. %s%s%n", i + 1, note.getTitle(), tags);
            }
        } catch (Exception e) {
            System.err.println("✘ 获取笔记列表失败: " + e.getMessage());
        }
    }

    public void viewNote(String id) {
        try {
            Note note = noteService.getNoteById(id);
            System.out.println("标题: " + note.getTitle());
            System.out.println("ID: " + note.getId());
            if (!note.getTags().isEmpty()) {
                System.out.println("标签: " + String.join(", ", note.getTags()));
            }
            System.out.println("创建时间: " + note.getCreatedAt());
            System.out.println("更新时间: " + note.getUpdatedAt());
            System.out.println("内容:");
            System.out.println(note.getContent());
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✘ 查看笔记失败: " + e.getMessage());
        }
    }

    public void editNoteContent(String id, String newContent) {
        try {
            noteService.updateNoteContent(id, newContent);
            System.out.println("✔ 笔记内容更新成功");
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✘ 更新笔记失败: " + e.getMessage());
        }
    }

    public void deleteNote(String id) {
        try {
            noteService.deleteNote(id);
            System.out.println("✔ 笔记删除成功");
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✘ 删除笔记失败: " + e.getMessage());
        }
    }

    public void addTag(String id, String tag) {
        try {
            noteService.addTagToNote(id, tag);
            System.out.println("✔ 标签添加成功");
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✘ 添加标签失败: " + e.getMessage());
        }
    }

    public void removeTag(String id, String tag) {
        try {
            noteService.removeTagFromNote(id, tag);
            System.out.println("✔ 标签移除成功");
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✘ 移除标签失败: " + e.getMessage());
        }
    }

    public void searchNotes(String keyword) {
        try {
            List<Note> results = noteService.searchNotes(keyword);
            if (results.isEmpty()) {
                System.out.println("未找到包含 \"" + keyword + "\" 的笔记");
                return;
            }

            System.out.println("找到 " + results.size() + " 条相关笔记:");
            for (int i = 0; i < results.size(); i++) {
                Note note = results.get(i);
                String tags = note.getTags().isEmpty() ? "" : " [" + String.join(", ", note.getTags()) + "]";
                System.out.printf("%d. %s%s%n", i + 1, note.getTitle(), tags);
            }
        } catch (Exception e) {
            System.err.println("✘ 搜索失败: " + e.getMessage());
        }
    }
}
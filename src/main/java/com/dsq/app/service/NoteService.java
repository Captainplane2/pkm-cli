package com.dsq.app.service;

import com.dsq.Note;
import java.util.List;
import java.util.UUID;

/**
 * 笔记业务服务 - 封装笔记相关的业务逻辑
 */
public class NoteService {
    private final StorageService storageService;

    public NoteService(StorageService storageService) {
        this.storageService = storageService;
    }

    public Note createNote(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }

        String id = UUID.randomUUID().toString();
        Note note = new Note(id, title.trim(), content != null ? content.trim() : "");
        storageService.saveNote(note);
        return note;
    }

    public List<Note> getAllNotes() {
        return storageService.findAllNotes();
    }

    public Note getNoteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("笔记ID不能为空");
        }
        Note note = storageService.findNoteById(id.trim());
        if (note == null) {
            throw new IllegalArgumentException("未找到ID为 " + id + " 的笔记");
        }
        return note;
    }

    public void updateNoteContent(String id, String newContent) {
        Note note = getNoteById(id);
        note.setContent(newContent != null ? newContent.trim() : "");
        storageService.saveNote(note);
    }

    public void updateNoteTitle(String id, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        Note note = getNoteById(id);
        note.setTitle(newTitle.trim());
        storageService.saveNote(note);
    }

    public void deleteNote(String id) {
        storageService.deleteNote(id);
    }

    public void addTagToNote(String id, String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("标签不能为空");
        }
        Note note = getNoteById(id);
        note.addTag(tag.trim());
        storageService.saveNote(note);
    }

    public void removeTagFromNote(String id, String tag) {
        Note note = getNoteById(id);
        note.removeTag(tag);
        storageService.saveNote(note);
    }

    public List<Note> searchNotes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNotes();
        }
        String searchTerm = keyword.trim().toLowerCase();
        return getAllNotes().stream()
                .filter(note -> note.getTitle().toLowerCase().contains(searchTerm) ||
                        note.getContent().toLowerCase().contains(searchTerm) ||
                        note.getTags().stream().anyMatch(t -> t.toLowerCase().contains(searchTerm)))
                .toList();
    }
}
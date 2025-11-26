package com.example.pkm_web.service;

import com.example.pkm_web.model.Note;
import com.example.pkm_web.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 笔记业务服务 - 封装笔记相关的业务逻辑
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }

        String id = UUID.randomUUID().toString();
        Note note = new Note(id, title.trim(), content != null ? content.trim() : "");
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Note getNoteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("笔记ID不能为空");
        }
        return noteRepository.findById(id.trim())
                .orElseThrow(() -> new IllegalArgumentException("未找到ID为 " + id + " 的笔记"));
    }

    public Note updateNoteContent(String id, String newContent) {
        Note note = getNoteById(id);
        note.setContent(newContent != null ? newContent.trim() : "");
        return noteRepository.save(note);
    }

    public Note updateNoteTitle(String id, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        Note note = getNoteById(id);
        note.setTitle(newTitle.trim());
        return noteRepository.save(note);
    }

    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }

    public Note addTagToNote(String id, String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("标签不能为空");
        }
        Note note = getNoteById(id);
        note.addTag(tag.trim());
        return noteRepository.save(note);
    }

    public Note removeTagFromNote(String id, String tag) {
        Note note = getNoteById(id);
        note.removeTag(tag);
        return noteRepository.save(note);
    }

    public List<Note> searchNotes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNotes();
        }
        return noteRepository.searchByKeyword(keyword.trim());
    }
}
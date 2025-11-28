package com.example.pkm_web.service;

import com.example.pkm_web.exception.NotFoundException;
import com.example.pkm_web.exception.ValidationException;
import com.example.pkm_web.model.Note;
import com.example.pkm_web.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("title", "标题不能为空");
        }

        String id = UUID.randomUUID().toString();
        Note note = new Note(id, title.trim(), content != null ? content.trim() : "");
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Note getNoteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "笔记ID不能为空");
        }
        return noteRepository.findById(id.trim())
                .orElseThrow(() -> new NotFoundException("笔记", id));
    }

    public Note updateNoteContent(String id, String newContent) {
        Note note = getNoteById(id);
        note.setContent(newContent != null ? newContent.trim() : "");
        return noteRepository.save(note);
    }

    public Note updateNoteTitle(String id, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new ValidationException("title", "标题不能为空");
        }
        Note note = getNoteById(id);
        note.setTitle(newTitle.trim());
        return noteRepository.save(note);
    }

    public Note updateNote(Note note) {
        if (note == null || note.getId() == null) {
            throw new ValidationException("note", "笔记对象和ID不能为空");
        }
        // 确保笔记存在
        getNoteById(note.getId());
        return noteRepository.save(note);
    }

    public void deleteNote(String id) {
        if (!noteRepository.existsById(id)) {
            throw new NotFoundException("笔记", id);
        }
        noteRepository.deleteById(id);
    }

    public Note addTagToNote(String id, String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new ValidationException("tag", "标签不能为空");
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

    public List<Note> findNotesByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return List.of();
        }
        return noteRepository.findByTag(tag.trim());
    }

    public List<Note> findNotesByMultipleTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return noteRepository.findByTagsContainingAll(tags);
    }

    /**
     * 获取笔记统计信息
     */
    public NoteStatistics getStatistics() {
        List<Note> allNotes = noteRepository.findAll();
        long totalNotes = allNotes.size();
        long totalTags = allNotes.stream()
                .mapToLong(note -> note.getTags().size())
                .sum();
        long averageTagsPerNote = totalNotes > 0 ? totalTags / totalNotes : 0;

        return new NoteStatistics(totalNotes, totalTags, averageTagsPerNote);
    }

    /**
     * 笔记统计信息类
     */
    public static class NoteStatistics {
        private final long totalNotes;
        private final long totalTags;
        private final long averageTagsPerNote;

        public NoteStatistics(long totalNotes, long totalTags, long averageTagsPerNote) {
            this.totalNotes = totalNotes;
            this.totalTags = totalTags;
            this.averageTagsPerNote = averageTagsPerNote;
        }

        // Getters
        public long getTotalNotes() { return totalNotes; }
        public long getTotalTags() { return totalTags; }
        public long getAverageTagsPerNote() { return averageTagsPerNote; }
    }
}
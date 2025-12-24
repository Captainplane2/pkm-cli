package com.example.pkm_web.service;

import com.example.pkm_web.annotation.CacheEvict;
import com.example.pkm_web.annotation.Cacheable;
import com.example.pkm_web.annotation.OperationLog;
import com.example.pkm_web.annotation.PerformanceMonitor;
import com.example.pkm_web.annotation.Validation;
import com.example.pkm_web.exception.NotFoundException;
import com.example.pkm_web.exception.ValidationException;
import com.example.pkm_web.model.Note;
import com.example.pkm_web.repository.NoteRepository;
import com.example.pkm_web.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @OperationLog(value = "创建新笔记", type = OperationLog.OperationType.CREATE)
    @PerformanceMonitor(value = "创建新笔记", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public Note createNote(String title, String content) {
        String id = UUID.randomUUID().toString();
        Long currentUserId = UserContext.getCurrentUserId();
        Note note = new Note(id, title.trim(), content != null ? content.trim() : "", currentUserId);
        return noteRepository.save(note);
    }

    @OperationLog(value = "获取所有笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "获取所有笔记", threshold = 1000, recordParams = false)
    @Cacheable(key = "'all_notes_' + T(com.example.pkm_web.util.UserContext).getCurrentUserId()")
    public List<Note> getAllNotes() {
        Long currentUserId = UserContext.getCurrentUserId();
        return noteRepository.findAllByUserIdOrderByUpdatedAtDesc(currentUserId);
    }

    @OperationLog(value = "根据ID获取笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "根据ID获取笔记", threshold = 300)
    @Cacheable(key = "'note_' + #id + '_' + T(com.example.pkm_web.util.UserContext).getCurrentUserId()")
    @Validation
    public Note getNoteById(String id) {
        Long currentUserId = UserContext.getCurrentUserId();
        return noteRepository.findByIdAndUserId(id.trim(), currentUserId)
                .orElseThrow(() -> new NotFoundException("笔记", id));
    }

    @OperationLog(value = "更新笔记内容", type = OperationLog.OperationType.UPDATE)
    @PerformanceMonitor(value = "更新笔记内容", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public Note updateNoteContent(String id, String newContent) {
        Note note = getNoteById(id);
        note.setContent(newContent != null ? newContent.trim() : "");
        return noteRepository.save(note);
    }

    @OperationLog(value = "更新笔记标题", type = OperationLog.OperationType.UPDATE)
    @PerformanceMonitor(value = "更新笔记标题", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public Note updateNoteTitle(String id, String newTitle) {
        Note note = getNoteById(id);
        note.setTitle(newTitle.trim());
        return noteRepository.save(note);
    }

    @OperationLog(value = "更新笔记", type = OperationLog.OperationType.UPDATE)
    @PerformanceMonitor(value = "更新笔记", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public Note updateNote(Note note) {
        // 确保笔记存在
        getNoteById(note.getId());
        // 确保更新时不会改变用户ID
        Long currentUserId = UserContext.getCurrentUserId();
        note.setUserId(currentUserId);
        return noteRepository.save(note);
    }

    @OperationLog(value = "删除笔记", type = OperationLog.OperationType.DELETE)
    @PerformanceMonitor(value = "删除笔记", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public void deleteNote(String id) {
        Long currentUserId = UserContext.getCurrentUserId();
        // 确保只能删除自己的笔记
        if (!noteRepository.existsByIdAndUserId(id, currentUserId)) {
            throw new NotFoundException("笔记", id);
        }
        noteRepository.deleteById(id);
    }

    @OperationLog(value = "为笔记添加标签", type = OperationLog.OperationType.UPDATE)
    @PerformanceMonitor(value = "为笔记添加标签", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public Note addTagToNote(String id, String tag) {
        Note note = getNoteById(id);
        note.addTag(tag.trim());
        return noteRepository.save(note);
    }

    @OperationLog(value = "从笔记移除标签", type = OperationLog.OperationType.UPDATE)
    @PerformanceMonitor(value = "从笔记移除标签", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public Note removeTagFromNote(String id, String tag) {
        Note note = getNoteById(id);
        note.removeTag(tag);
        return noteRepository.save(note);
    }

    @OperationLog(value = "搜索笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "搜索笔记", threshold = 1000, recordResult = false)
    @Cacheable(key = "'search_' + #keyword + '_' + T(com.example.pkm_web.util.UserContext).getCurrentUserId()")
    public List<Note> searchNotes(String keyword) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNotes();
        }
        return noteRepository.searchByKeywordAndUserId(keyword.trim(), currentUserId);
    }

    @OperationLog(value = "根据标签查找笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "根据标签查找笔记", threshold = 800)
    @Cacheable(key = "'notes_by_tag_' + #tag + '_' + T(com.example.pkm_web.util.UserContext).getCurrentUserId()")
    public List<Note> findNotesByTag(String tag) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (tag == null || tag.trim().isEmpty()) {
            return List.of();
        }
        return noteRepository.findByTagAndUserId(tag.trim(), currentUserId);
    }

    @OperationLog(value = "根据多个标签查找笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "根据多个标签查找笔记", threshold = 1500, recordResult = false)
    @Cacheable(key = "'notes_by_tags_' + #tags + '_' + T(com.example.pkm_web.util.UserContext).getCurrentUserId()")
    public List<Note> findNotesByMultipleTags(List<String> tags) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return noteRepository.findByTagsContainingAllAndUserId(tags, currentUserId);
    }

    @OperationLog(value = "更新笔记分类", type = OperationLog.OperationType.UPDATE)
    @PerformanceMonitor(value = "更新笔记分类", threshold = 500)
    @CacheEvict(allEntries = true)
    @Validation
    public Note updateNoteCategory(String id, String categoryId) {
        Note note = getNoteById(id);
        note.setCategoryId(categoryId != null && categoryId.trim().isEmpty() ? null : categoryId);
        return noteRepository.save(note);
    }

    @OperationLog(value = "根据分类查找笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "根据分类查找笔记", threshold = 800)
    @Cacheable(key = "'notes_by_category_' + #categoryId + '_' + T(com.example.pkm_web.util.UserContext).getCurrentUserId()")
    public List<Note> findNotesByCategory(String categoryId) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (categoryId == null || categoryId.trim().isEmpty() || "null".equalsIgnoreCase(categoryId.trim())) {
            return noteRepository.findByCategoryIdIsNullAndUserId(currentUserId);
        }
        return noteRepository.findByCategoryIdAndUserId(categoryId.trim(), currentUserId);
    }

    /**
     * 获取笔记统计信息
     */
    @OperationLog(value = "获取笔记统计信息", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "获取笔记统计信息", threshold = 1000, recordResult = true)
    @Cacheable(key = "'note_statistics_' + T(com.example.pkm_web.util.UserContext).getCurrentUserId()", ttl = 600) // 统计信息缓存10分钟
    public NoteStatistics getStatistics() {
        Long currentUserId = UserContext.getCurrentUserId();
        List<Note> allNotes = noteRepository.findByUserId(currentUserId);
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
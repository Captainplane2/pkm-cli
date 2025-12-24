package com.example.pkm_web.service;

import com.example.pkm_web.annotation.OperationLog;
import com.example.pkm_web.annotation.PerformanceMonitor;
import com.example.pkm_web.exception.NotFoundException;
import com.example.pkm_web.exception.ValidationException;
import com.example.pkm_web.model.Note;
import com.example.pkm_web.model.Tag;
import com.example.pkm_web.repository.NoteRepository;
import com.example.pkm_web.repository.TagRepository;
import com.example.pkm_web.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public TagService(TagRepository tagRepository, NoteRepository noteRepository) {
        this.tagRepository = tagRepository;
        this.noteRepository = noteRepository;
    }

    @OperationLog(value = "根据标签查找笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "根据标签查找笔记", threshold = 800)
    public List<Note> findNotesByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Long currentUserId = UserContext.getCurrentUserId();
        return noteRepository.findByTagAndUserId(tag.trim(), currentUserId);
    }

    @OperationLog(value = "获取所有标签", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "获取所有标签", threshold = 1000, recordParams = false)
    public Set<String> getAllTags() {
        Long currentUserId = UserContext.getCurrentUserId();
        List<Note> allNotes = noteRepository.findByUserId(currentUserId);
        return allNotes.stream()
                .flatMap(note -> note.getTags().stream())
                .collect(Collectors.toSet());
    }

    @OperationLog(value = "获取标签统计信息", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "获取标签统计信息", threshold = 1500, recordResult = true)
    public Map<String, Integer> getTagStatistics() {
        Long currentUserId = UserContext.getCurrentUserId();
        List<Note> allNotes = noteRepository.findByUserId(currentUserId);
        Map<String, Integer> stats = new HashMap<>();

        // 先统计所有笔记中使用的标签
        allNotes.forEach(note ->
                note.getTags().forEach(tag ->
                        stats.put(tag, stats.getOrDefault(tag, 0) + 1)
                )
        );

        // 然后添加所有已创建但未使用的标签
        List<String> allTagNames = tagRepository.findAllTagNamesByUserId(currentUserId);
        allTagNames.forEach(tagName ->
                stats.putIfAbsent(tagName, 0)
        );

        return stats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @OperationLog(value = "根据多个标签查找笔记", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "根据多个标签查找笔记", threshold = 1200)
    public List<Note> findNotesByMultipleTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        Long currentUserId = UserContext.getCurrentUserId();
        return noteRepository.findByTagsContainingAllAndUserId(tags, currentUserId);
    }

    @OperationLog(value = "模糊搜索标签", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "模糊搜索标签", threshold = 2000, recordParams = false)
    public List<Note> fuzzySearchByTag(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = keyword.trim().toLowerCase();
        Long currentUserId = UserContext.getCurrentUserId();
        List<Note> allNotes = noteRepository.findByUserId(currentUserId);

        return allNotes.stream()
                .filter(note -> note.getTags().stream()
                        .anyMatch(tag -> tag.toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }

    @OperationLog(value = "创建标签", type = OperationLog.OperationType.CREATE)
    @PerformanceMonitor(value = "创建标签", threshold = 500)
    public Tag createTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("name", "标签名称不能为空");
        }

        String tagName = name.trim();
        Long currentUserId = UserContext.getCurrentUserId();
        return tagRepository.findByNameAndUserId(tagName, currentUserId)
                .orElseGet(() -> tagRepository.save(new Tag(tagName, currentUserId)));
    }

    @OperationLog(value = "删除标签", type = OperationLog.OperationType.DELETE)
    @PerformanceMonitor(value = "删除标签", threshold = 2500, recordResult = false)
    public void deleteTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("name", "标签名称不能为空");
        }

        String tagName = name.trim();
        Long currentUserId = UserContext.getCurrentUserId();
        Tag tag = tagRepository.findByNameAndUserId(tagName, currentUserId)
                .orElseThrow(() -> new NotFoundException("标签", name));

        // 从当前用户的所有笔记中移除该标签
        List<Note> notesWithTag = noteRepository.findByTagAndUserId(tagName, currentUserId);
        notesWithTag.forEach(note -> note.removeTag(tagName));
        noteRepository.saveAll(notesWithTag);

        tagRepository.delete(tag);
    }

    @OperationLog(value = "获取所有标签实体", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "获取所有标签实体", threshold = 600, recordParams = false)
    public List<Tag> getAllTagEntities() {
        Long currentUserId = UserContext.getCurrentUserId();
        return tagRepository.findByUserId(currentUserId);
    }

    @OperationLog(value = "根据名称获取标签", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "根据名称获取标签", threshold = 400)
    public Optional<Tag> getTagByName(String name) {
        Long currentUserId = UserContext.getCurrentUserId();
        return tagRepository.findByNameAndUserId(name, currentUserId);
    }

    @OperationLog(value = "获取热门标签", type = OperationLog.OperationType.QUERY)
    @PerformanceMonitor(value = "获取热门标签", threshold = 1800)
    public List<String> findPopularTags(int limit) {
        Map<String, Integer> stats = getTagStatistics();
        return stats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
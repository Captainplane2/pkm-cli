package com.dsq.app.service;

import com.dsq.Note;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签业务服务 - 封装标签相关的业务逻辑
 */
public class TagService {
    private final StorageService storageService;
    private final Map<String, List<Note>> tagIndex = new HashMap<>();

    public TagService(StorageService storageService) {
        this.storageService = storageService;
        rebuildIndex();
    }

    private void rebuildIndex() {
        tagIndex.clear();
        List<Note> allNotes = storageService.findAllNotes();
        for (Note note : allNotes) {
            for (String tag : note.getTags()) {
                tagIndex.computeIfAbsent(tag, k -> new ArrayList<>()).add(note);
            }
        }
    }

    public List<Note> findNotesByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(tagIndex.getOrDefault(tag.trim(), new ArrayList<>()));
    }

    public Set<String> getAllTags() {
        return new HashSet<>(tagIndex.keySet());
    }

    public Map<String, Integer> getTagStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        tagIndex.forEach((tag, notes) -> stats.put(tag, notes.size()));
        return stats;
    }

    public List<Note> findNotesByMultipleTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }

        return storageService.findAllNotes().stream()
                .filter(note -> note.getTags().containsAll(tags))
                .collect(Collectors.toList());
    }

    public List<Note> fuzzySearchByTag(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = keyword.trim().toLowerCase();
        return tagIndex.entrySet().stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searchTerm))
                .flatMap(entry -> entry.getValue().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // 当笔记更新时重新构建索引
    public void refreshIndex() {
        rebuildIndex();
    }
}
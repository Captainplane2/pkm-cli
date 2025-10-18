package com.dsq;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签服务类 - 核心功能实现
 */
public class TagService {
    private final Map<String, List<Note>> tagMap = new HashMap<>();

    /**
     * 建立标签索引 - 实验指导书要求
     */
    public void indexNote(Note note) {
        if (note == null || note.getTags() == null) return;

        note.getTags().forEach(tag ->
                tagMap.computeIfAbsent(tag, k -> new ArrayList<>()).add(note)
        );
    }

    /**
     * 标签搜索 - 实验指导书要求
     */
    public List<Note> findNotesByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(tagMap.getOrDefault(tag.trim(), new ArrayList<>()));
    }

    /**
     * 获取所有标签 - 实验指导书要求
     */
    public Set<String> getAllTags() {
        return new HashSet<>(tagMap.keySet());
    }

    /**
     * 标签统计 - 实验指导书要求
     */
    public Map<String, Integer> getTagStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        tagMap.forEach((tag, notes) -> stats.put(tag, notes.size()));
        return stats;
    }

    /**
     * 使用Stream API实现统计 - 实验指导书要求
     */
    public Map<String, Long> getTagStatisticsWithStream() {
        return tagMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
    }

    /**
     * 从索引中移除笔记
     */
    public void removeNote(Note note) {
        if (note == null) return;

        tagMap.values().forEach(notes -> notes.removeIf(n -> n.equals(note)));
        // 清理空标签
        tagMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    /**
     * 清空索引
     */
    public void clear() {
        tagMap.clear();
    }

    /**
     * 获取索引中的标签数量
     */
    public int getTagCount() {
        return tagMap.size();
    }
}
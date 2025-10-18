package com.dsq;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 高级搜索服务 - 使用Stream API实现高级功能
 */
public class AdvancedSearchService {
    private final TagService tagService;

    public AdvancedSearchService(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 多标签组合搜索（AND条件）- 实验指导书要求
     */
    public List<Note> searchByMultipleTagsAND(List<String> requiredTags) {
        if (requiredTags == null || requiredTags.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取第一个标签的笔记，然后过滤出包含所有必需标签的笔记
        return tagService.findNotesByTag(requiredTags.get(0)).stream()
                .filter(note -> note.hasAllTags(requiredTags))
                .collect(Collectors.toList());
    }

    /**
     * 标签模糊匹配 - 实验指导书要求
     */
    public List<Note> fuzzySearchByTag(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = keyword.trim().toLowerCase();

        // 找到所有包含关键词的标签，然后获取对应的笔记
        return tagService.getAllTags().stream()
                .filter(tag -> tag.toLowerCase().contains(searchTerm))
                .flatMap(tag -> tagService.findNotesByTag(tag).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取标签使用统计（按使用次数排序）
     */
    public List<Map.Entry<String, Integer>> getSortedTagStats() {
        return tagService.getTagStatistics().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());
    }
}
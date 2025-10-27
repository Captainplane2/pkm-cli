package com.dsq.app.controller;

import com.dsq.app.service.TagService;
import java.util.Map;
import java.util.Set;

/**
 * 标签控制器 - 处理标签相关的用户交互
 */
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    public void listAllTags() {
        try {
            Set<String> tags = tagService.getAllTags();
            if (tags.isEmpty()) {
                System.out.println("暂无标签");
                return;
            }

            System.out.println("所有标签 (" + tags.size() + " 个):");
            int i = 1;
            for (String tag : tags) {
                System.out.printf("%d. %s%n", i++, tag);
            }
        } catch (Exception e) {
            System.err.println("✘ 获取标签列表失败: " + e.getMessage());
        }
    }

    public void showTagStatistics() {
        try {
            Map<String, Integer> stats = tagService.getTagStatistics();
            if (stats.isEmpty()) {
                System.out.println("暂无标签使用统计");
                return;
            }

            System.out.println("标签使用统计:");
            stats.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .forEach(entry ->
                            System.out.printf("  %s: %d 条笔记%n", entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            System.err.println("✘ 获取标签统计失败: " + e.getMessage());
        }
    }

    public void searchByTag(String tag) {
        try {
            var notes = tagService.findNotesByTag(tag);
            if (notes.isEmpty()) {
                System.out.println("未找到标签为 \"" + tag + "\" 的笔记");
                return;
            }

            System.out.println("标签 \"" + tag + "\" 的笔记 (" + notes.size() + " 条):");
            for (int i = 0; i < notes.size(); i++) {
                var note = notes.get(i);
                System.out.printf("%d. %s%n", i + 1, note.getTitle());
            }
        } catch (Exception e) {
            System.err.println("✘ 按标签搜索失败: " + e.getMessage());
        }
    }

    public void fuzzySearchByTag(String keyword) {
        try {
            var notes = tagService.fuzzySearchByTag(keyword);
            if (notes.isEmpty()) {
                System.out.println("未找到包含 \"" + keyword + "\" 的标签");
                return;
            }

            System.out.println("找到 " + notes.size() + " 条相关笔记:");
            for (int i = 0; i < notes.size(); i++) {
                var note = notes.get(i);
                String tags = note.getTags().isEmpty() ? "" : " [" + String.join(", ", note.getTags()) + "]";
                System.out.printf("%d. %s%s%n", i + 1, note.getTitle(), tags);
            }
        } catch (Exception e) {
            System.err.println("✘ 标签模糊搜索失败: " + e.getMessage());
        }
    }
}
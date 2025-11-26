package com.example.pkm_web.controller;

import com.example.pkm_web.model.Note;
import com.example.pkm_web.model.Tag;
import com.example.pkm_web.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // 获取所有标签 - 对应CLI的'tags list'命令
    @GetMapping
    public ResponseEntity<Set<String>> listAllTags() {
        Set<String> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    // 获取标签统计 - 对应CLI的'tags stats'命令
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getTagStatistics() {
        Map<String, Integer> stats = tagService.getTagStatistics();
        return ResponseEntity.ok(stats);
    }

    // 按标签搜索笔记 - 对应CLI的'tags search'命令
    @GetMapping("/{tag}/notes")
    public ResponseEntity<List<Note>> getNotesByTag(@PathVariable String tag) {
        List<Note> notes = tagService.findNotesByTag(tag);
        return ResponseEntity.ok(notes);
    }

    // 多标签搜索 - 对应CLI的'tags multiple'命令
    @PostMapping("/multiple")
    public ResponseEntity<List<Note>> getNotesByMultipleTags(@RequestBody List<String> tags) {
        List<Note> notes = tagService.findNotesByMultipleTags(tags);
        return ResponseEntity.ok(notes);
    }

    // 标签模糊搜索 - 对应CLI的'tags fuzzy'命令
    @GetMapping("/search")
    public ResponseEntity<List<Note>> fuzzySearchByTag(@RequestParam String keyword) {
        List<Note> notes = tagService.fuzzySearchByTag(keyword);
        return ResponseEntity.ok(notes);
    }

    // 创建标签
    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            Tag tag = tagService.createTag(name);
            return ResponseEntity.status(HttpStatus.CREATED).body(tag);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 删除标签
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteTag(@PathVariable String name) {
        try {
            tagService.deleteTag(name);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
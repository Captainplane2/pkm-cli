package com.example.pkm_web.service;

import com.example.pkm_web.model.Note;
import com.example.pkm_web.model.Tag;
import com.example.pkm_web.repository.NoteRepository;
import com.example.pkm_web.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签业务服务 - 封装标签相关的业务逻辑
 */
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public TagService(TagRepository tagRepository, NoteRepository noteRepository) {
        this.tagRepository = tagRepository;
        this.noteRepository = noteRepository;
    }

    public List<Note> findNotesByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return noteRepository.findByTagsContaining(tag.trim());
    }

    public Set<String> getAllTags() {
        List<Note> allNotes = noteRepository.findAll();
        return allNotes.stream()
                .flatMap(note -> note.getTags().stream())
                .collect(Collectors.toSet());
    }

    public Map<String, Integer> getTagStatistics() {
        List<Note> allNotes = noteRepository.findAll();
        Map<String, Integer> stats = new HashMap<>();

        allNotes.forEach(note ->
                note.getTags().forEach(tag ->
                        stats.put(tag, stats.getOrDefault(tag, 0) + 1)
                )
        );

        return stats;
    }

    public List<Note> findNotesByMultipleTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }

        List<Note> allNotes = noteRepository.findAll();
        return allNotes.stream()
                .filter(note -> note.getTags().containsAll(tags))
                .collect(Collectors.toList());
    }

    public List<Note> fuzzySearchByTag(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = keyword.trim().toLowerCase();
        List<Note> allNotes = noteRepository.findAll();

        return allNotes.stream()
                .filter(note -> note.getTags().stream()
                        .anyMatch(tag -> tag.toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }

    public Tag createTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("标签名称不能为空");
        }

        String tagName = name.trim();
        return tagRepository.findByName(tagName)
                .orElseGet(() -> tagRepository.save(new Tag(tagName)));
    }

    public void deleteTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("标签名称不能为空");
        }

        tagRepository.findByName(name.trim()).ifPresent(tagRepository::delete);
    }
}
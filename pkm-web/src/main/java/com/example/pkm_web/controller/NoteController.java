package com.example.pkm_web.controller;

import com.example.pkm_web.model.Note;
import com.example.pkm_web.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // 创建笔记 - 对应CLI的'new'命令
    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Note note) {
        try {
            Note createdNote = noteService.createNote(note.getTitle(), note.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 列出所有笔记 - 对应CLI的'list'命令
    @GetMapping
    public ResponseEntity<List<Note>> listNotes() {
        List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    // 查看单个笔记 - 对应CLI的'view <id>'命令
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable String id) {
        try {
            Note note = noteService.getNoteById(id);
            return ResponseEntity.ok(note);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 更新笔记内容 - 对应CLI的'edit content'命令
    @PutMapping("/{id}/content")
    public ResponseEntity<?> updateNoteContent(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            Note updatedNote = noteService.updateNoteContent(id, content);
            return ResponseEntity.ok(updatedNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 更新笔记标题 - 对应CLI的'edit title'命令
    @PutMapping("/{id}/title")
    public ResponseEntity<?> updateNoteTitle(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            Note updatedNote = noteService.updateNoteTitle(id, title);
            return ResponseEntity.ok(updatedNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 删除笔记 - 对应CLI的'delete'命令
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable String id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "笔记不存在"));
        }
    }

    // 添加标签 - 对应CLI的'tag add'命令
    @PostMapping("/{id}/tags")
    public ResponseEntity<?> addTag(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String tag = request.get("tag");
            Note updatedNote = noteService.addTagToNote(id, tag);
            return ResponseEntity.ok(updatedNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 移除标签 - 对应CLI的'tag remove'命令
    @DeleteMapping("/{id}/tags/{tag}")
    public ResponseEntity<?> removeTag(@PathVariable String id, @PathVariable String tag) {
        try {
            Note updatedNote = noteService.removeTagFromNote(id, tag);
            return ResponseEntity.ok(updatedNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 搜索笔记 - 对应CLI的'search'命令
    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String keyword) {
        List<Note> notes = noteService.searchNotes(keyword);
        return ResponseEntity.ok(notes);
    }
}
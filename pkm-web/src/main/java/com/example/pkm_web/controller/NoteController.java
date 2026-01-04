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

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note createdNote = noteService.createNote(note.getTitle(), note.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @GetMapping
    public ResponseEntity<List<Note>> listNotes() {
        List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable String id) {
        Note note = noteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/{id}/content")
    public ResponseEntity<Note> updateNoteContent(@PathVariable String id, @RequestBody Map<String, String> request) {
        String content = request.get("content");
        Note updatedNote = noteService.updateNoteContent(id, content);
        return ResponseEntity.ok(updatedNote);
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<Note> updateNoteTitle(@PathVariable String id, @RequestBody Map<String, String> request) {
        String title = request.get("title");
        Note updatedNote = noteService.updateNoteTitle(id, title);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<Note> addTag(@PathVariable String id, @RequestBody Map<String, String> request) {
        String tag = request.get("tag");
        Note updatedNote = noteService.addTagToNote(id, tag);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}/tags/{tag}")
    public ResponseEntity<Note> removeTag(@PathVariable String id, @PathVariable String tag) {
        Note updatedNote = noteService.removeTagFromNote(id, tag);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String keyword) {
        List<Note> notes = noteService.searchNotes(keyword);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}/category")
    public ResponseEntity<Note> updateNoteCategory(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String categoryId = request.get("categoryId");
        Note updatedNote = noteService.updateNoteCategory(id, categoryId);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Note>> getNotesByCategory(@PathVariable String categoryId) {
        List<Note> notes = noteService.findNotesByCategory(categoryId);
        return ResponseEntity.ok(notes);
    }

    /**
     * 导出用户的所有数据
     */
    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportData() {
        Map<String, Object> data = noteService.exportUserData();
        return ResponseEntity.ok(data);
    }

    /**
     * 导入用户数据
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importData(@RequestBody Map<String, Object> data) {
        noteService.importUserData(data);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "数据导入成功"
        ));
    }
}

package com.dsq.app.service;

import com.dsq.Note;
import com.dsq.repository.NoteFileRepository;
import com.dsq.exception.FileOperationException;
import java.util.List;

/**
 * 文件存储服务实现 - 适配NoteFileRepository到StorageService接口
 */
public class NoteFileStorageService implements StorageService {
    private final NoteFileRepository repository;

    public NoteFileStorageService() {
        this.repository = new NoteFileRepository();
    }

    public NoteFileStorageService(String filePath) {
        this.repository = new NoteFileRepository(filePath);
    }

    @Override
    public void saveNote(Note note) {
        try {
            List<Note> currentNotes = findAllNotes();
            // 如果笔记已存在，更新它；否则添加新笔记
            boolean found = false;
            for (int i = 0; i < currentNotes.size(); i++) {
                if (currentNotes.get(i).getId() != null &&
                        currentNotes.get(i).getId().equals(note.getId())) {
                    currentNotes.set(i, note);
                    found = true;
                    break;
                }
            }
            if (!found) {
                currentNotes.add(note);
            }
            repository.saveNotes(currentNotes);
        } catch (FileOperationException e) {
            throw new RuntimeException("保存笔记失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Note> findAllNotes() {
        try {
            return repository.loadNotes();
        } catch (FileOperationException e) {
            throw new RuntimeException("加载笔记失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Note findNoteById(String id) {
        List<Note> notes = findAllNotes();
        return notes.stream()
                .filter(note -> note.getId() != null && note.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteNote(String id) {
        try {
            List<Note> currentNotes = findAllNotes();
            List<Note> updatedNotes = currentNotes.stream()
                    .filter(note -> !note.getId().equals(id))
                    .toList();
            repository.saveNotes(updatedNotes);
        } catch (FileOperationException e) {
            throw new RuntimeException("删除笔记失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveAllNotes(List<Note> notes) {
        try {
            repository.saveNotes(notes);
        } catch (FileOperationException e) {
            throw new RuntimeException("保存所有笔记失败: " + e.getMessage(), e);
        }
    }
}
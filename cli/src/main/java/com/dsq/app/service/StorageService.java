package com.dsq.app.service;

import com.dsq.Note;
import java.util.List;

/**
 * 存储服务接口 - 统一数据存储契约
 */
public interface StorageService {
    void saveNote(Note note);
    List<Note> findAllNotes();
    Note findNoteById(String id);
    void deleteNote(String id);
    void saveAllNotes(List<Note> notes);
}
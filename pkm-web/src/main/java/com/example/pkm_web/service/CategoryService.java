package com.example.pkm_web.service;

import com.example.pkm_web.exception.NotFoundException;
import com.example.pkm_web.exception.ValidationException;
import com.example.pkm_web.model.Category;
import com.example.pkm_web.repository.CategoryRepository;
import com.example.pkm_web.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, NoteRepository noteRepository) {
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
    }

    public Category createCategory(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("name", "分类名称不能为空");
        }

        String trimmedName = name.trim();
        if (categoryRepository.existsByName(trimmedName)) {
            throw new ValidationException("name", "分类名称已存在");
        }

        String id = UUID.randomUUID().toString();
        Category category = new Category(id, trimmedName);
        
        // 处理 description：如果为 null 或空字符串，则设为 null
        if (description != null && !description.trim().isEmpty()) {
            category.setDescription(description.trim());
        } else {
            category.setDescription(null);
        }
        
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "分类ID不能为空");
        }
        return categoryRepository.findById(id.trim())
                .orElseThrow(() -> new NotFoundException("分类", id));
    }

    public Category updateCategory(String id, String name, String description) {
        Category category = getCategoryById(id);

        if (name != null && !name.trim().isEmpty()) {
            String trimmedName = name.trim();
            // 检查新名称是否与其他分类冲突
            if (!category.getName().equals(trimmedName) && categoryRepository.existsByName(trimmedName)) {
                throw new ValidationException("name", "分类名称已存在");
            }
            category.setName(trimmedName);
        }

        if (description != null) {
            category.setDescription(description.trim());
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        Category category = getCategoryById(id);
        
        // 删除分类前，将该分类下的所有笔记的分类ID设为null
        noteRepository.findAll().stream()
                .filter(note -> id.equals(note.getCategoryId()))
                .forEach(note -> {
                    note.setCategoryId(null);
                    noteRepository.save(note);
                });

        categoryRepository.delete(category);
    }

    public long getNoteCountByCategory(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty() || "null".equalsIgnoreCase(categoryId.trim())) {
            return noteRepository.findByCategoryIdIsNull().size();
        }
        return noteRepository.findByCategoryId(categoryId.trim()).size();
    }
}


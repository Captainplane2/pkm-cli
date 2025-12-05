package com.example.pkm_web.controller;

import com.example.pkm_web.model.Category;
import com.example.pkm_web.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String description = request.get("description");
            logger.info("创建分类请求: name={}, description={}", name, description);
            Category category = categoryService.createCategory(name, description);
            logger.info("分类创建成功: id={}, name={}", category.getId(), category.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (Exception e) {
            logger.error("创建分类失败", e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<Category>> listCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        Category category = categoryService.updateCategory(id, name, description);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/count")
    public ResponseEntity<Map<String, Long>> getNoteCount(@PathVariable String id) {
        long count = categoryService.getNoteCountByCategory(id);
        return ResponseEntity.ok(Map.of("count", count));
    }
}


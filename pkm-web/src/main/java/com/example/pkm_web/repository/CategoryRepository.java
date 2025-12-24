package com.example.pkm_web.repository;

import com.example.pkm_web.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    /**
     * 根据名称和userId查找分类
     */
    Optional<Category> findByNameAndUserId(String name, Long userId);

    /**
     * 检查分类是否存在（按名称和userId）
     */
    boolean existsByNameAndUserId(String name, Long userId);
    
    /**
     * 根据ID和userId查找分类（确保只能查到自己的分类）
     */
    Optional<Category> findByIdAndUserId(String id, Long userId);
    
    /**
     * 查找当前用户的所有分类
     */
    List<Category> findByUserId(Long userId);
}


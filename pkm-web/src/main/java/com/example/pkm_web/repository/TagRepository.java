package com.example.pkm_web.repository;

import com.example.pkm_web.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 根据名称查找标签
     */
    Optional<Tag> findByName(String name);

    /**
     * 检查标签是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据名称模糊查询
     */
    List<Tag> findByNameContainingIgnoreCase(String name);

    /**
     * 获取最常用的标签（按使用次数排序）
     */
    @Query("SELECT t FROM Tag t ORDER BY t.usageCount DESC")
    List<Tag> findTopUsedTags();

    /**
     * 获取所有标签名称
     */
    @Query("SELECT t.name FROM Tag t")
    List<String> findAllTagNames();
}
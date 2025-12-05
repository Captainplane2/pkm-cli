package com.example.pkm_web.repository;

import com.example.pkm_web.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {

    /**
     * 根据关键词搜索笔记（标题、内容、标签）
     */
    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.tags t " +
            "WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Note> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据标签查找笔记
     */
    @Query("SELECT n FROM Note n JOIN n.tags t WHERE t = :tag")
    List<Note> findByTag(@Param("tag") String tag);

    /**
     * 查找包含所有指定标签的笔记
     */
    @Query("SELECT n FROM Note n WHERE n.tags IS NOT EMPTY AND :tags MEMBER OF n.tags")
    List<Note> findByTagsContainingAll(@Param("tags") List<String> tags);

    /**
     * 根据标题模糊查询
     */
    List<Note> findByTitleContainingIgnoreCase(String title);

    /**
     * 按创建时间倒序排列
     */
    List<Note> findAllByOrderByCreatedAtDesc();

    /**
     * 按更新时间倒序排列
     */
    List<Note> findAllByOrderByUpdatedAtDesc();

    /**
     * 根据分类ID查找笔记
     */
    List<Note> findByCategoryId(String categoryId);

    /**
     * 查找未分类的笔记（categoryId为null）
     */
    List<Note> findByCategoryIdIsNull();
}
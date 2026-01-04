package com.example.pkm_web.repository;

import com.example.pkm_web.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {

    /**
     * 根据关键词搜索笔记（标题、内容、标签）
     */
    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.tags t " +
            "WHERE n.userId = :userId AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByKeywordAndUserId(@Param("keyword") String keyword, @Param("userId") Long userId);

    /**
     * 根据标签查找笔记
     */
    @Query("SELECT n FROM Note n JOIN n.tags t WHERE n.userId = :userId AND t = :tag")
    List<Note> findByTagAndUserId(@Param("tag") String tag, @Param("userId") Long userId);

    /**
     * 查找包含所有指定标签的笔记
     */
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.tags IS NOT EMPTY AND :tags MEMBER OF n.tags")
    List<Note> findByTagsContainingAllAndUserId(@Param("tags") List<String> tags, @Param("userId") Long userId);

    /**
     * 根据标题模糊查询
     */
    List<Note> findByTitleContainingIgnoreCaseAndUserId(String title, Long userId);

    /**
     * 按创建时间倒序排列
     */
    List<Note> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 按更新时间倒序排列
     */
    List<Note> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    /**
     * 根据分类ID查找笔记
     */
    List<Note> findByCategoryIdAndUserId(String categoryId, Long userId);

    /**
     * 查找未分类的笔记（categoryId为null）
     */
    List<Note> findByCategoryIdIsNullAndUserId(Long userId);
    
    /**
     * 根据ID和userId查找笔记（确保只能查到自己的笔记）
     */
    Optional<Note> findByIdAndUserId(String id, Long userId);
    
    /**
     * 查找当前用户的所有笔记
     */
    List<Note> findByUserId(Long userId);

    /**
     * 删除指定用户的所有笔记
     */
    @Modifying
    @Query("DELETE FROM Note n WHERE n.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 检查笔记是否存在（按ID和userId）
     */
    boolean existsByIdAndUserId(String id, Long userId);
}

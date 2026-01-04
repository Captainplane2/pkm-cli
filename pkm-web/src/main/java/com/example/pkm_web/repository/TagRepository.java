package com.example.pkm_web.repository;

import com.example.pkm_web.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 根据名称和userId查找标签
     */
    Optional<Tag> findByNameAndUserId(String name, Long userId);

    /**
     * 检查标签是否存在（按名称和userId）
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * 根据名称模糊查询（按userId）
     */
    List<Tag> findByNameContainingIgnoreCaseAndUserId(String name, Long userId);

    /**
     * 获取最常用的标签（按使用次数排序，按userId）
    @Query("SELECT t FROM Tag t WHERE t.userId = :userId ORDER BY t.usageCount DESC")
    List<Tag> findTopUsedTagsByUserId(@Param("userId") Long userId);
     */

    /**
     * 获取当前用户的所有标签名称
     */
    @Query("SELECT t.name FROM Tag t WHERE t.userId = :userId")
    List<String> findAllTagNamesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据ID和userId查找标签（确保只能查到自己的标签）
     */
    Optional<Tag> findByIdAndUserId(Long id, Long userId);
    
    /**
     * 查找当前用户的所有标签
     */
    List<Tag> findByUserId(Long userId);

    /**
     * 删除指定用户的所有标签记录
     */
    @Modifying
    @Query("DELETE FROM Tag t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

package com.example.backend.repository;

import com.example.backend.entity.CourseChapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 课程章节数据访问接口
 * 提供对CourseChapter实体的增删改查操作
 */
@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, Long> {
    
    /**
     * 根据课程ID查找所有章节，按序号排序
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<CourseChapter> findByCourseIdOrderBySortOrder(Long courseId);
    
    /**
     * 查找课程的总章节数
     * @param courseId 课程ID
     * @return 章节数量
     */
    long countByCourseId(Long courseId);
    
    /**
     * 通过章节标题查找章节
     * @param title 章节标题
     * @return 可选的章节
     */
    Optional<CourseChapter> findByTitle(String title);
    
    /**
     * 根据课程ID和标题查找章节
     * @param courseId 课程ID
     * @param title 章节标题
     * @return 可选的章节
     */
    Optional<CourseChapter> findByCourseIdAndTitle(Long courseId, String title);
    
    /**
     * 根据标题关键词搜索章节
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<CourseChapter> findByTitleContaining(String keyword, Pageable pageable);
    
    /**
     * 删除课程的所有章节
     * @param courseId 课程ID
     * @return 删除的记录数
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM CourseChapter c WHERE c.courseId = :courseId")
    int deleteByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 更新章节顺序
     * @param chapterId 章节ID
     * @param sortOrder 新的顺序号
     * @return 更新的记录数
     */
    @Transactional
    @Modifying
    @Query("UPDATE CourseChapter c SET c.sortOrder = :sortOrder WHERE c.id = :chapterId")
    int updateChapterOrder(@Param("chapterId") Long chapterId, @Param("sortOrder") Integer sortOrder);
    
    /**
     * 查找课程中最大的章节序号
     * @param courseId 课程ID
     * @return 最大序号
     */
    @Query("SELECT MAX(c.sortOrder) FROM CourseChapter c WHERE c.courseId = :courseId")
    Integer findMaxSortOrderByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 查找课程最近更新的章节
     * @param courseId 课程ID
     * @param limit 数量限制
     * @return 章节列表
     */
    @Query("SELECT c FROM CourseChapter c WHERE c.courseId = :courseId ORDER BY c.updateTime DESC")
    List<CourseChapter> findRecentUpdatedChapters(@Param("courseId") Long courseId, Pageable limit);
    
    /**
     * 根据课程ID查找所有章节，按排序号升序排序
     * 
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<CourseChapter> findByCourseIdOrderBySortOrderAsc(Long courseId);
} 
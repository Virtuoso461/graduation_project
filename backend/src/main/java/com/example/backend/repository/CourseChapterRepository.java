package com.example.backend.repository;

import com.example.backend.entity.CourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程章节数据访问接口
 */
@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, Long> {
    
    /**
     * 根据课程ID查询课程章节，并按照排序字段升序排列
     */
    List<CourseChapter> findByCourseIdOrderBySortOrderAsc(Long courseId);
    
    /**
     * 根据课程ID删除所有章节
     */
    void deleteByCourseId(Long courseId);
    
    /**
     * 获取课程章节总数
     */
    long countByCourseId(Long courseId);
} 
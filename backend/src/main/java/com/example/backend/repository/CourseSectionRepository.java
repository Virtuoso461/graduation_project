package com.example.backend.repository;

import com.example.backend.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程章节小节数据访问接口
 */
@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    
    /**
     * 根据章节ID查询小节，并按照排序字段升序排列
     */
    List<CourseSection> findByChapterIdOrderBySortOrderAsc(Long chapterId);
    
    /**
     * 根据章节ID删除所有小节
     */
    void deleteByChapterId(Long chapterId);
    
    /**
     * 获取章节小节总数
     */
    long countByChapterId(Long chapterId);
}

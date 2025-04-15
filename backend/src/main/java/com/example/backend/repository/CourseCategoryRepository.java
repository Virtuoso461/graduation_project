package com.example.backend.repository;

import com.example.backend.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程分类数据访问接口
 */
@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {

    /**
     * 根据分类名称查找分类
     * 
     * @param name 分类名称
     * @return 分类
     */
    CourseCategory findByName(String name);
    
    /**
     * 名称包含指定字符串的分类
     * 
     * @param name 名称关键词
     * @return 分类列表
     */
    List<CourseCategory> findByNameContaining(String name);
    
    /**
     * 检查指定名称的分类是否存在
     * 
     * @param name 分类名称
     * @return 是否存在
     */
    boolean existsByName(String name);
} 
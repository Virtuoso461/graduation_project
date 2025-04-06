package com.example.backend.service;

import com.example.backend.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 * 提供课程相关的业务逻辑操作
 */
public interface CourseService {
    
    /**
     * 获取课程列表
     * 
     * @param userId 可选的用户ID，如果提供则返回用户已选课程，否则返回所有课程
     * @return 课程列表
     */
    List<Course> getCourses(Long userId);
    
    /**
     * 根据ID获取课程详情
     * 
     * @param id 课程ID
     * @return 课程对象
     */
    Course getCourseById(Long id);
    
    /**
     * 更新课程信息
     * 
     * @param course 课程对象
     * @return 更新后的课程对象
     */
    Course updateCourse(Course course);
    
    /**
     * 创建新课程
     * 
     * @param course 课程对象
     * @return 创建后的课程对象
     */
    Course createCourse(Course course);
    
    /**
     * 删除课程
     * 
     * @param id 课程ID
     */
    void deleteCourse(Long id);
} 
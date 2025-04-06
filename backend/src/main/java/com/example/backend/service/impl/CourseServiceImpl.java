package com.example.backend.service.impl;

import com.example.backend.entity.Course;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CourseRepository;
import com.example.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getCourses(Long userId) {
        if (userId != null) {
            // 如果提供了用户ID，返回该用户选修的课程
            // 实际应用中需要关联用户课程表查询
            return courseRepository.findByTeacherId(userId);
        } else {
            // 否则返回所有课程
            return courseRepository.findAll();
        }
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在，ID: " + id));
    }

    @Override
    public Course updateCourse(Course course) {
        if (course.getId() == null) {
            throw new IllegalArgumentException("更新课程时ID不能为空");
        }
        
        // 检查课程是否存在
        if (!courseRepository.existsById(course.getId())) {
            throw new ResourceNotFoundException("课程不存在，ID: " + course.getId());
        }
        
        // 设置更新时间
        course.setUpdatedAt(new Date());
        
        return courseRepository.save(course);
    }

    @Override
    public Course createCourse(Course course) {
        // 设置创建和更新时间
        Date now = new Date();
        if (course.getCreatedAt() == null) {
            course.setCreatedAt(now);
        }
        course.setUpdatedAt(now);
        
        // 新创建的课程默认为未审核状态
        course.setApproved(false);
        
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("删除课程时ID不能为空");
        }
        
        // 检查课程是否存在
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("课程不存在，ID: " + id);
        }
        
        courseRepository.deleteById(id);
    }
} 
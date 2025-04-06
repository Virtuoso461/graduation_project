package com.example.backend.repository;

import com.example.backend.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程选修记录数据访问接口
 */
@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    
    /**
     * 根据用户邮箱查询所有选课记录
     */
    List<CourseEnrollment> findByUserEmail(String userEmail);
    
    /**
     * 根据用户邮箱和是否完成状态查询课程
     */
    List<CourseEnrollment> findByUserEmailAndIsCompleted(String userEmail, Boolean isCompleted);
    
    /**
     * 根据课程ID和用户邮箱查询选课记录
     */
    Optional<CourseEnrollment> findByCourseIdAndUserEmail(Long courseId, String userEmail);
    
    /**
     * 查询课程的选课人数
     */
    long countByCourseId(Long courseId);
    
    /**
     * 查询用户选课总数
     */
    long countByUserEmail(String userEmail);
    
    /**
     * 查询用户已完成课程数
     */
    long countByUserEmailAndIsCompleted(String userEmail, Boolean isCompleted);
    
    /**
     * 检查用户是否已经选修了某门课程
     */
    boolean existsByCourseIdAndUserEmail(Long courseId, String userEmail);
    
    /**
     * 获取用户的学习进度统计
     */
    @Query("SELECT AVG(e.progress) FROM CourseEnrollment e WHERE e.userEmail = ?1")
    Double getAverageProgressByUserEmail(String userEmail);
} 
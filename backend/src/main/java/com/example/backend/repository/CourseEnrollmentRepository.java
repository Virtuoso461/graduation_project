package com.example.backend.repository;

import com.example.backend.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
     * 根据用户邮箱查询已完成的课程
     */
    List<CourseEnrollment> findByUserEmailAndIsCompletedTrue(String userEmail);
    
    /**
     * 根据用户邮箱查询未完成的课程
     */
    List<CourseEnrollment> findByUserEmailAndIsCompletedFalse(String userEmail);

    /**
     * 根据课程ID和用户邮箱查询选课记录
     */
    Optional<CourseEnrollment> findByCourseIdAndUserEmail(Long courseId, String userEmail);
    
    /**
     * 根据用户邮箱和课程ID查询选课记录
     */
    Optional<CourseEnrollment> findByUserEmailAndCourseId(String userEmail, Long courseId);

    /**
     * 根据课程ID查询所有选课记录
     */
    List<CourseEnrollment> findByCourseId(Long courseId);

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


    /**
     * 根据学生邮箱和教师ID查询选课记录
     * 原方法名: findByStudentIdAndCourseTeacherId
     */
    @Query("SELECT e FROM CourseEnrollment e JOIN Course c ON e.courseId = c.id WHERE e.userEmail = ?1 AND c.teacherId = ?2")
    List<CourseEnrollment> findByUserEmailAndCourseTeacherId(String userEmail, Long teacherId);
    
    /**
     * 统计指定日期之后创建的、指定课程列表的选课记录数量
     * 用于计算课程选课增长率
     * 
     * @param date 指定日期
     * @param courseIds 课程ID列表
     * @return 选课记录数量
     */
    long countByCreatedAtAfterAndCourseIdIn(Date date, List<Long> courseIds);
}
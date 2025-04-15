package com.example.backend.repository;

import com.example.backend.entity.Assignment;
import com.example.backend.entity.Todo;
import com.example.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业数据访问接口
 * 提供对Assignment实体的增删改查操作
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * 根据教师邮箱查找作业，按创建时间降序排序
     * @param email 教师邮箱
     * @return 作业列表
     */
    List<Assignment> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * 查找所有作业，按创建时间降序排序（用于学生）
     * @return 作业列表
     */
    List<Assignment> findAllByOrderByCreatedAtDesc();

    /**
     * 根据教师邮箱和状态查找作业，按截止日期升序排序
     * @param email 教师邮箱
     * @param status 作业状态
     * @return 作业列表
     */
    List<Assignment> findByEmailAndStatusOrderByDueDateAsc(String email, String status);
    
    /**
     * 统计教师特定状态的作业数量
     * @param email 教师邮箱
     * @param status 作业状态
     * @return 作业数量
     */
    long countByEmailAndStatus(String email, String status);

    /**
     * 统计指定日期之后创建的作业数量
     * @param date 日期
     * @return 作业数量
     */
    long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * 根据课程ID查找作业
     * @param courseId 课程ID
     * @return 作业列表
     */
    List<Assignment> findByCourseId(Long courseId);
    
    /**
     * 查找指定截止日期范围内的作业
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 作业列表
     */
    List<Assignment> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 根据标题关键词搜索作业
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Assignment> findByTitleContaining(String keyword, Pageable pageable);
    
    /**
     * 查找已过期但未完成的作业
     * @param currentDate 当前日期
     * @return 作业列表
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :currentDate AND a.status != 'COMPLETED'")
    List<Assignment> findOverdueAssignments(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * 统计各状态的作业数量
     * @param email 教师邮箱
     * @return 包含状态和数量的列表
     */
    @Query("SELECT a.status, COUNT(a) FROM Assignment a WHERE a.email = :email GROUP BY a.status")
    List<Object[]> countByStatusGroups(@Param("email") String email);
}
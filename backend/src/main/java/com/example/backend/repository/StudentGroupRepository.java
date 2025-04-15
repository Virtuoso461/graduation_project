package com.example.backend.repository;

import com.example.backend.entity.StudentGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学生分组数据访问接口
 */
@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    
    /**
     * 根据教师ID查询分组
     *
     * @param teacherId 教师ID
     * @return 分组列表
     */
    List<StudentGroup> findByTeacherId(Long teacherId);
    
    /**
     * 根据教师ID分页查询分组
     *
     * @param teacherId 教师ID
     * @param pageable 分页参数
     * @return 分组分页数据
     */
    Page<StudentGroup> findByTeacherId(Long teacherId, Pageable pageable);
    
    /**
     * 根据教师ID和分组名称模糊查询
     *
     * @param teacherId 教师ID
     * @param name 分组名称
     * @param pageable 分页参数
     * @return 分组分页数据
     */
    Page<StudentGroup> findByTeacherIdAndNameContaining(Long teacherId, String name, Pageable pageable);
    
    /**
     * 查询包含指定学生的分组
     *
     * @param studentId 学生ID
     * @return 分组列表
     */
    @Query("SELECT g FROM StudentGroup g JOIN g.studentIds s WHERE s = :studentId")
    List<StudentGroup> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 查询包含指定学生的分组（分页）
     *
     * @param studentId 学生ID
     * @param pageable 分页参数
     * @return 分组分页数据
     */
    @Query("SELECT g FROM StudentGroup g JOIN g.studentIds s WHERE s = :studentId")
    Page<StudentGroup> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);
    
    /**
     * 统计教师的分组数量
     *
     * @param teacherId 教师ID
     * @return 分组数量
     */
    long countByTeacherId(Long teacherId);
}

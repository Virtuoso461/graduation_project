package com.example.backend.repository;

import com.example.backend.entity.AssignmentSubmission;
import com.example.backend.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long>{

    /**
     * 根据作业ID查找所有提交
     */
    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);
    
    /**
     * 分页查询作业提交
     */
    Page<AssignmentSubmission> findByAssignmentId(Long assignmentId, Pageable pageable);
    
    /**
     * 查找指定学生的作业提交
     */
    AssignmentSubmission findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    
    /**
     * 查找教师所有未批改的作业提交
     */
    @Query("SELECT s FROM AssignmentSubmission s JOIN s.assignment a WHERE a.email = :teacherEmail AND s.isGraded = false")
    Page<AssignmentSubmission> findPendingSubmissionsByTeacherId(@Param("teacherEmail") String teacherEmail, Pageable pageable);
    
    /**
     * 统计教师所有未批改的作业提交数量
     */
    @Query("SELECT COUNT(s) FROM AssignmentSubmission s JOIN s.assignment a WHERE a.email = :teacherEmail AND s.isGraded = false")
    Long countPendingSubmissionsByTeacherId(@Param("teacherEmail") String teacherEmail);
}

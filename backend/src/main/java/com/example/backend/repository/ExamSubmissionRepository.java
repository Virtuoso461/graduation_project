package com.example.backend.repository;

import com.example.backend.entity.ExamSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {

    /**
     * 根据考试ID查找所有提交
     */
    List<ExamSubmission> findByExamId(Long examId);
    
    /**
     * 分页查询考试提交
     */
    Page<ExamSubmission> findByExamId(Long examId, Pageable pageable);
    
    /**
     * 查找指定学生的考试提交
     */
    ExamSubmission findByExamIdAndStudentId(Long examId, Long studentId);
    
    /**
     * 查找教师所有未批改的考试提交
     */
    @Query("SELECT s FROM ExamSubmission s JOIN Exam e ON s.examId = e.id WHERE e.teacherId = :teacherId AND s.isGraded = false")
    Page<ExamSubmission> findPendingSubmissionsByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);
    
    /**
     * 统计教师所有未批改的考试提交数量
     */
    @Query("SELECT COUNT(s) FROM ExamSubmission s JOIN Exam e ON s.examId = e.id WHERE e.teacherId = :teacherId AND s.isGraded = false")
    Long countPendingSubmissionsByTeacherId(@Param("teacherId") Long teacherId);
}

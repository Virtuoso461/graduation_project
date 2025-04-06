package com.example.backend.repository;

import com.example.backend.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    
    // 根据用户邮箱查询所有考试结果
    List<ExamResult> findByUserEmail(String userEmail);
    
    // 根据考试ID和用户邮箱查询考试结果
    Optional<ExamResult> findByExamIdAndUserEmail(Long examId, String userEmail);
    
    // 根据考试ID查询所有结果
    List<ExamResult> findByExamId(Long examId);
    
    // 根据用户邮箱和提交时间范围查询考试结果
    @Query("SELECT er FROM ExamResult er WHERE er.userEmail = :userEmail AND er.submitTime BETWEEN :startDate AND :endDate")
    List<ExamResult> findByUserEmailAndDateRange(
            @Param("userEmail") String userEmail, 
            @Param("startDate") Date startDate, 
            @Param("endDate") Date endDate);
    
    // 统计用户参与的考试数量
    @Query("SELECT COUNT(er) FROM ExamResult er WHERE er.userEmail = :userEmail")
    Long countByUserEmail(@Param("userEmail") String userEmail);
    
    // 计算用户的平均分
    @Query("SELECT AVG(er.score) FROM ExamResult er WHERE er.userEmail = :userEmail")
    Double calculateAverageScore(@Param("userEmail") String userEmail);
    
    // 计算用户的平均正确率
    @Query("SELECT AVG(er.correctRate) FROM ExamResult er WHERE er.userEmail = :userEmail")
    Double calculateAverageCorrectRate(@Param("userEmail") String userEmail);
    
    // 查找用户最好的排名
    @Query("SELECT MIN(er.ranking) FROM ExamResult er WHERE er.userEmail = :userEmail")
    Integer findBestRanking(@Param("userEmail") String userEmail);
    
    // 查找用户成绩最好的考试
    @Query("SELECT er FROM ExamResult er WHERE er.userEmail = :userEmail ORDER BY er.score DESC")
    List<ExamResult> findBestExamResults(@Param("userEmail") String userEmail);

    boolean existsByExamIdAndUserEmail(Long examId, String userEmail);
    
    // 根据用户邮箱和状态查询考试结果，用于筛选不同状态的考试记录
    List<ExamResult> findByUserEmailAndStatus(String userEmail, String status);
    
    // 根据用户邮箱和结束时间范围查询考试结果，用于统计特定时间段内完成的考试
    List<ExamResult> findByUserEmailAndEndTimeBetween(String userEmail, Date startDate, Date endDate);
} 
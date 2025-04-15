package com.example.backend.repository;

import com.example.backend.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 考试答案仓库接口
 * 提供对ExamAnswer实体的数据访问操作
 */
@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {
    
    /**
     * 根据考试ID和用户ID查找答案
     * 
     * @param examId 考试ID
     * @param userId 用户ID
     * @return 可选的考试答案
     */
    Optional<ExamAnswer> findByExamIdAndUserId(Long examId, Long userId);
    
    /**
     * 根据考试ID查找所有答案
     * 
     * @param examId 考试ID
     * @return 答案列表
     */
    List<ExamAnswer> findByExamId(Long examId);
    
    /**
     * 根据用户ID查找所有答案
     * 
     * @param userId 用户ID
     * @return 答案列表
     */
    List<ExamAnswer> findByUserId(Long userId);
    
    /**
     * 检查是否存在指定考试和用户的答案
     * 
     * @param examId 考试ID
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByExamIdAndUserId(Long examId, Long userId);
    
    /**
     * 获取用户在特定考试中的正确答案数量
     * 
     * @param examId 考试ID
     * @param userId 用户ID
     * @return 正确答案数量
     */
    @Query("SELECT COUNT(ea) FROM ExamAnswer ea WHERE ea.examId = :examId AND ea.userId = :userId AND ea.isCorrect = true")
    Long countCorrectAnswers(@Param("examId") Long examId, @Param("userId") Long userId);
    
    /**
     * 删除指定考试的所有答案
     * 
     * @param examId 考试ID
     */
    void deleteByExamId(Long examId);
    
    /**
     * 获取用户正确率最低的题目类型
     * 
     * @param userId 用户ID
     * @return 题目类型和正确率的列表
     */
    @Query("SELECT ea.questionType, COUNT(CASE WHEN ea.isCorrect = true THEN 1 ELSE NULL END) * 100.0 / COUNT(ea) " +
           "FROM ExamAnswer ea WHERE ea.userId = :userId GROUP BY ea.questionType ORDER BY COUNT(CASE WHEN ea.isCorrect = true THEN 1 ELSE NULL END) * 100.0 / COUNT(ea)")
    List<Object[]> findWeakestQuestionTypes(@Param("userId") Long userId);
} 
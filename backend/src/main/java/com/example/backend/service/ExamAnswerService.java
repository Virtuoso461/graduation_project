package com.example.backend.service;

import com.example.backend.dto.ExamAnswerDTO;
import com.example.backend.entity.ExamAnswer;

import java.util.List;
import java.util.Map;

/**
 * 考试答案服务接口
 * 提供考试答案相关的业务逻辑方法
 */
public interface ExamAnswerService {

    /**
     * 保存单个考试答案
     *
     * @param examAnswer 考试答案实体
     * @return 保存后的考试答案
     */
    ExamAnswer saveAnswer(ExamAnswer examAnswer);

    /**
     * 批量保存考试答案
     *
     * @param examId 考试ID
     * @param userId 用户ID
     * @param answers 答案列表
     * @return 保存的答案数量
     */
    int saveAnswers(Long examId, Long userId, List<Map<String, Object>> answers);

    /**
     * 处理考试提交
     *
     * @param userId 用户ID
     * @param answerDTO 考试答案DTO
     * @return 处理结果，包含成绩、正确题数等信息
     */
    Map<String, Object> processExamSubmission(Long userId, ExamAnswerDTO answerDTO);

    /**
     * 根据考试ID和用户ID获取答案
     *
     * @param examId 考试ID
     * @param userId 用户ID
     * @return 答案列表
     */
    List<ExamAnswer> getAnswersByExamAndUser(Long examId, Long userId);

    /**
     * 根据考试ID获取所有答案
     *
     * @param examId 考试ID
     * @return 答案列表
     */
    List<ExamAnswer> getAnswersByExam(Long examId);

    /**
     * 根据用户ID获取所有答案
     *
     * @param userId 用户ID
     * @return 答案列表
     */
    List<ExamAnswer> getAnswersByUser(Long userId);

    /**
     * 自动批改考试答案
     *
     * @param examId 考试ID
     * @param userId 用户ID
     * @return 批改结果，包含得分、正确率等信息
     */
    Map<String, Object> autoGradeExam(Long examId, Long userId);

    /**
     * 手动批改考试答案
     *
     * @param examAnswerId 考试答案ID
     * @param isCorrect 是否正确
     * @param score 得分
     * @param comments 评语
     * @return 更新后的考试答案
     */
    ExamAnswer gradeAnswer(Long examAnswerId, Boolean isCorrect, Double score, String comments);

    /**
     * 获取用户的答题统计数据
     *
     * @param userId 用户ID
     * @return 统计数据，包含总答题数、正确率等信息
     */
    Map<String, Object> getUserAnswerStatistics(Long userId);

    /**
     * 获取用户在考试中的表现
     *
     * @param examId 考试ID
     * @param userId 用户ID
     * @return 用户在考试中的表现数据
     */
    Map<String, Object> getUserExamPerformance(Long examId, Long userId);

    /**
     * 获取用户的薄弱知识点
     *
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 薄弱知识点列表，按正确率升序排列
     */
    List<Map<String, Object>> getUserWeakKnowledgePoints(Long userId, int limit);

    /**
     * 获取考试的难度系数
     *
     * @param examId 考试ID
     * @return 难度系数，基于所有用户的平均正确率
     */
    double getExamDifficultyFactor(Long examId);

    /**
     * 获取考试中最难的题目
     *
     * @param examId 考试ID
     * @param limit 返回数量限制
     * @return 最难题目列表，按正确率升序排列
     */
    List<Map<String, Object>> getMostDifficultQuestions(Long examId, int limit);

    /**
     * 删除考试的所有答案
     *
     * @param examId 考试ID
     */
    void deleteAnswersByExam(Long examId);

    /**
     * 获取用户知识点掌握情况
     *
     * @param userId 用户ID
     * @return 知识点掌握情况数据
     */
    Map<String, Object> getUserKnowledgePointsMastery(Long userId);

    /**
     * 删除用户的所有答案
     *
     * @param userId 用户ID
     */
    void deleteAnswersByUser(Long userId);
} 
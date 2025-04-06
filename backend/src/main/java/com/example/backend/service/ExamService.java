package com.example.backend.service;

import com.example.backend.dto.ExamStatsDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;

import java.util.Date;
import java.util.List;

public interface ExamService {
    
    // 创建考试
    Exam createExam(Exam exam);
    
    // 更新考试
    Exam updateExam(Long examId, Exam updatedExam);
    
    // 删除考试
    void deleteExam(Long examId);
    
    // 根据ID获取考试
    Exam getExamById(Long examId);
    
    // 获取所有考试
    List<Exam> getAllExams();
    
    // 根据创建者邮箱获取考试
    List<Exam> getExamsByCreator(String creatorEmail);
    
    // 获取可参加的考试
    List<Exam> getAvailableExams();
    
    // 提交考试结果
    ExamResult submitExamResult(ExamResult examResult);
    
    // 获取考试结果
    ExamResult getExamResult(Long examId, String userEmail);
    
    // 获取用户完成的所有考试
    List<ExamResult> getCompletedExams(String userEmail);
    
    // 获取考试统计数据
    ExamStatsDTO getExamStats(String userEmail);
    
    // 根据日期范围获取考试结果
    List<ExamResult> getExamResultsByDateRange(String userEmail, Date startDate, Date endDate);
} 
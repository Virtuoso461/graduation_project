package com.example.backend.service.impl;

import com.example.backend.dto.ExamStatsDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.ExamResultRepository;
import com.example.backend.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ExamServiceImpl implements ExamService {
    
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    
    @Autowired
    public ExamServiceImpl(ExamRepository examRepository, 
                          ExamResultRepository examResultRepository) {
        this.examRepository = examRepository;
        this.examResultRepository = examResultRepository;
    }
    
    @Override
    @Transactional
    public Exam createExam(Exam exam) {
        // 设置创建时间
        if (exam.getStartTime() == null) {
            exam.setStartTime(new Date());
        }
        return examRepository.save(exam);
    }
    
    @Override
    @Transactional
    public Exam updateExam(Long examId, Exam updatedExam) {
        Exam existingExam = getExamById(examId);
        if (existingExam != null) {
            // 更新基本信息
            existingExam.setTitle(updatedExam.getTitle());
            existingExam.setDescription(updatedExam.getDescription());
            existingExam.setCourseName(updatedExam.getCourseName());
            existingExam.setDuration(updatedExam.getDuration());
            existingExam.setStartTime(updatedExam.getStartTime());
            existingExam.setEndTime(updatedExam.getEndTime());
            existingExam.setTotalScore(updatedExam.getTotalScore());
            existingExam.setPassingScore(updatedExam.getPassingScore());
            existingExam.setIsPublished(updatedExam.getIsPublished());
            existingExam.setCategory(updatedExam.getCategory());
            existingExam.setDifficulty(updatedExam.getDifficulty());
            existingExam.setQuestionCount(updatedExam.getQuestionCount());
            
            return examRepository.save(existingExam);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteExam(Long examId) {
        examRepository.deleteById(examId);
    }
    
    @Override
    public Exam getExamById(Long examId) {
        return examRepository.findById(examId).orElse(null);
    }
    
    @Override
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
    
    @Override
    public List<Exam> getExamsByCreator(String creatorEmail) {
        return examRepository.findByCreatorEmail(creatorEmail);
    }
    
    @Override
    public List<Exam> getAvailableExams() {
        return examRepository.findAvailableExams(new Date());
    }
    
    @Override
    @Transactional
    public ExamResult submitExamResult(ExamResult examResult) {
        // 设置提交时间
        examResult.setSubmitTime(new Date());
        
        // 计算正确率
        if (examResult.getTotalCount() != null && examResult.getTotalCount() > 0) {
            double correctRate = (double) examResult.getCorrectCount() / examResult.getTotalCount();
            examResult.setCorrectRate(correctRate);
        }
        
        // 获取考试标题和课程名称
        Exam exam = examRepository.findById(examResult.getExamId()).orElse(null);
        if (exam != null) {
            examResult.setExamTitle(exam.getTitle());
            examResult.setCourseName(exam.getCourseName());
        }
        
        return examResultRepository.save(examResult);
    }
    
    @Override
    public ExamResult getExamResult(Long examId, String userEmail) {
        Optional<ExamResult> result = examResultRepository.findByExamIdAndUserEmail(examId, userEmail);
        return result.orElse(null);
    }
    
    @Override
    public List<ExamResult> getCompletedExams(String userEmail) {
        return examResultRepository.findByUserEmail(userEmail);
    }
    
    @Override
    public ExamStatsDTO getExamStats(String userEmail) {
        ExamStatsDTO stats = new ExamStatsDTO();
        
        // 获取考试总数
        Long totalCount = examResultRepository.countByUserEmail(userEmail);
        stats.setTotalExams(totalCount != null ? totalCount.intValue() : 0);
        
        // 设置完成的考试数量
        stats.setCompletedExams(totalCount != null ? totalCount.intValue() : 0);
        
        // 计算平均分
        Double avgScore = examResultRepository.calculateAverageScore(userEmail);
        stats.setAverageScore(avgScore != null ? avgScore : 0.0);
        
        // 计算平均正确率
        Double avgCorrectRate = examResultRepository.calculateAverageCorrectRate(userEmail);
        stats.setAverageCorrectRate(avgCorrectRate != null ? avgCorrectRate : 0.0);
        
        // 获取最好排名
        Integer bestRanking = examResultRepository.findBestRanking(userEmail);
        stats.setBestRanking(bestRanking != null ? bestRanking : 0);
        
        // 获取成绩最好的考试
        List<ExamResult> bestResults = examResultRepository.findBestExamResults(userEmail);
        if (!bestResults.isEmpty()) {
            stats.setBestExamTitle(bestResults.get(0).getExamTitle());
        }
        
        // 设置默认分布数据
        stats.setCategoryDistribution(1.0);
        
        return stats;
    }
    
    @Override
    public List<ExamResult> getExamResultsByDateRange(String userEmail, Date startDate, Date endDate) {
        return examResultRepository.findByUserEmailAndDateRange(userEmail, startDate, endDate);
    }
} 
package com.example.backend.vo;

import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 测试结果视图对象，用于返回给前端
 */
public class ExamResultVO {
    
    private Long id;
    private Long examId;
    private String title;  // 测试标题
    private String type;   // 测试类型
    private String description;
    private String courseId;
    private String courseName;
    private Integer score;
    private Integer correctCount;
    private Integer totalCount; // 替代wrongCount，表示总题目数
    private Double correctRate;
    private String ranking; // 排名，格式为 "10/100"
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    // 转换completedAt为字符串格式，方便前端显示
    private String completedTime;
    
    private Integer timeSpent;
    private Integer totalScore;
    
    // 构造函数
    public ExamResultVO() {
    }
    
    // 将实体转换为VO
    public static ExamResultVO fromEntity(ExamResult result, Exam exam) {
        ExamResultVO vo = new ExamResultVO();
        vo.setId(result.getId());
        vo.setExamId(result.getExamId());
        
        // 设置考试相关信息
        if (exam != null) {
            vo.setTitle(exam.getTitle());
            vo.setType(exam.getCategory()); // 修改为使用category字段
            vo.setDescription(exam.getDescription());
            vo.setCourseId(null); // 新Exam实体没有courseId字段，设为null
            vo.setCourseName(exam.getCourseName());
            vo.setTotalScore(exam.getTotalScore());
        }
        
        // 设置结果信息
        vo.setScore(result.getScore());
        vo.setCorrectCount(result.getCorrectCount());
        vo.setTotalCount(result.getTotalCount());
        vo.setCorrectRate(result.getCorrectRate());
        
        // 设置排名，格式为 "10/100"
        if (result.getRanking() != null) {
            // 假设总参与人数存储在其他地方，这里仅作示例，实际逻辑会从特定地方获取
            int totalParticipants = 100; // 假设值
            vo.setRanking(result.getRanking() + "/" + totalParticipants);
        }
        
        // 从submitTime转换为completedAt
        if (result.getSubmitTime() != null) {
            vo.setCompletedAt(LocalDateTime.ofInstant(
                result.getSubmitTime().toInstant(), 
                ZoneId.systemDefault()
            ));
            
            // 格式化日期
            vo.setCompletedTime(vo.getCompletedAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            ));
        }
        
        vo.setTimeSpent(result.getTimeSpent());
        
        return vo;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Double getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(Double correctRate) {
        this.correctRate = correctRate;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
    }

    public Integer getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Integer timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
} 
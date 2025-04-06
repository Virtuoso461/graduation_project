package com.example.backend.vo;

import com.example.backend.entity.Exam;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 测试视图对象，用于返回给前端
 */
public class ExamVO {
    
    private Long id;
    private String title;
    private String type; // 对应 Exam 实体中的 category 字段
    private String description;
    private Integer duration;
    private Integer questionCount;
    private Integer totalScore;
    private Integer passingScore;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline; // 对应 Exam 实体中的 endTime 字段
    
    private String courseName;
    private String creatorEmail; // 对应 Exam 实体中的 creatorEmail 字段
    private Boolean isPublished; // 对应 Exam 实体中的 isPublished 字段
    private String difficulty;
    
    // 转换deadline为字符串格式，方便前端显示
    private String deadlineStr;
    
    // 构造函数
    public ExamVO() {
    }
    
    // 将实体转换为VO
    public static ExamVO fromEntity(Exam exam) {
        ExamVO vo = new ExamVO();
        vo.setId(exam.getId());
        vo.setTitle(exam.getTitle());
        vo.setType(exam.getCategory()); // 使用 category 字段
        vo.setDescription(exam.getDescription());
        vo.setDuration(exam.getDuration());
        vo.setQuestionCount(exam.getQuestionCount());
        vo.setTotalScore(exam.getTotalScore());
        vo.setPassingScore(exam.getPassingScore());
        vo.setCourseName(exam.getCourseName());
        vo.setCreatorEmail(exam.getCreatorEmail());
        vo.setIsPublished(exam.getIsPublished());
        vo.setDifficulty(exam.getDifficulty());
        
        // 转换开始时间
        if (exam.getStartTime() != null) {
            vo.setStartTime(LocalDateTime.ofInstant(
                exam.getStartTime().toInstant(),
                ZoneId.systemDefault()
            ));
        }
        
        // 转换截止时间
        if (exam.getEndTime() != null) {
            LocalDateTime endTime = LocalDateTime.ofInstant(
                exam.getEndTime().toInstant(),
                ZoneId.systemDefault()
            );
            vo.setDeadline(endTime);
            
            // 格式化日期
            vo.setDeadlineStr(endTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            ));
        }
        
        return vo;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDeadlineStr() {
        return deadlineStr;
    }

    public void setDeadlineStr(String deadlineStr) {
        this.deadlineStr = deadlineStr;
    }
} 
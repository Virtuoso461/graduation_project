package com.example.backend.entity;

import jakarta.persistence.*;

import java.util.Date;



/**
 * 在线测试实体类
 */
@Entity
@Table(name = "exams")
public class Exam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 测试标题
    @Column(nullable = false)
    private String title;
    
    // 测试描述
    @Column(nullable = false)
    private String description;
    
    // 所属课程名称
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    // 测试时长(分钟)
    @Column(nullable = false)
    private Integer duration;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time", nullable = false)
    private Date endTime;
    
    // 总分值
    @Column(nullable = false)
    private Integer totalScore;
    
    @Column(name = "passing_score")
    private Integer passingScore;
    
    // 创建教师邮箱
    @Column(name = "creator_email", nullable = false)
    private String creatorEmail;
    
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;
    
    @Column(name = "category", nullable = false)
    private String category = "未分类";
    
    @Column(name = "difficulty", nullable = false)
    private String difficulty = "中等";
    
    // 题目数量
    @Column(name = "question_count")
    private Integer questionCount = 0;

    // 构造函数
    public Exam() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
} 
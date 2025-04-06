package com.example.backend.entity;

import jakarta.persistence.*;

import java.util.Date;



/**
 * 测试结果实体类
 */
@Entity
@Table(name = "exam_results")
public class ExamResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 关联的测试ID
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    // 学生邮箱
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    // 得分
    @Column(nullable = false)
    private Integer score;
    
    // 正确题目数
    @Column(name = "correct_count")
    private Integer correctCount;
    
    // 错误题目数
    @Column(name = "total_count")
    private Integer totalCount;
    
    // 正确率
    @Column(name = "correct_rate")
    private Double correctRate;
    
    // 排名
    @Column
    private Integer ranking;
    
    // 用时(分钟)
    @Column(name = "time_spent")
    private Integer timeSpent;
    
    // 提交时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submit_time", nullable = false)
    private Date submitTime;
    
    // 考试结束时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;
    
    // 考试标题
    @Column(name = "exam_title")
    private String examTitle;
    
    // 课程名称
    @Column(name = "course_name")
    private String courseName;
    
    // 状态（如"已完成"、"进行中"等）
    @Column(name = "status", length = 20)
    private String status;
    
    // 构造函数
    public ExamResult() {
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Integer getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Integer timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 
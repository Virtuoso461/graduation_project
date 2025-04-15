package com.example.backend.entity;

import jakarta.persistence.*;

import java.util.Date;



/**
 * 在线测试实体类
 * 存储系统中所有考试/测验的基本信息
 * 用于考试管理、安排和学生参与考试等功能
 */
@Entity
@Table(name = "exams")
public class Exam {
    
    /**
     * 考试ID，主键，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 考试标题
     * 简明描述考试内容，不可为空
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * 考试描述
     * 详细说明考试内容、要求等信息，不可为空
     */
    @Column(nullable = false)
    private String description;
    
    /**
     * 所属课程名称
     * 考试关联的课程，不可为空
     */
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    /**
     * 考试时长(分钟)
     * 完成考试的限定时间，不可为空
     */
    @Column(nullable = false)
    private Integer duration;
    
    /**
     * 考试开始时间
     * 考试可以开始参加的时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;
    
    /**
     * 考试结束时间
     * 考试必须结束的时间，不可为空
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time", nullable = false)
    private Date endTime;
    
    /**
     * 总分值
     * 考试的满分分数，不可为空
     */
    @Column(nullable = false)
    private Integer totalScore;
    
    /**
     * 及格分数
     * 考试通过的最低分数
     */
    @Column(name = "passing_score")
    private Integer passingScore;
    
    /**
     * 创建教师邮箱
     * 创建考试的教师邮箱，不可为空
     */
    @Column(name = "creator_email", nullable = false)
    private String creatorEmail;
    
    /**
     * 是否已发布
     * true表示考试已发布，学生可以参加；false表示尚未发布
     */
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;
    
    /**
     * 考试分类
     * 考试的分类，如：期中考试、期末考试、章节测验等，默认为"未分类"
     */
    @Column(name = "category", nullable = false)
    private String category = "未分类";
    
    /**
     * 考试难度
     * 考试的难度级别，如：简单、中等、困难，默认为"中等"
     */
    @Column(name = "difficulty", nullable = false)
    private String difficulty = "中等";
    
    /**
     * 题目数量
     * 考试包含的题目总数，默认为0
     */
    @Column(name = "question_count")
    private Integer questionCount = 0;

    /**
     * 教师ID
     * 创建考试的教师ID
     */
    @Column(name = "teacher_id")
    private Long teacherId;
    
    /**
     * 创建时间
     * 记录考试创建的时间
     */
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    /**
     * 默认构造函数
     */
    public Exam() {
    }
    
    /**
     * 获取考试ID
     * @return 考试ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置考试ID
     * @param id 考试ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取考试标题
     * @return 考试标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置考试标题
     * @param title 考试标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取考试描述
     * @return 考试描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置考试描述
     * @param description 考试描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取所属课程名称
     * @return 课程名称
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 设置所属课程名称
     * @param courseName 课程名称
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * 获取考试时长
     * @return 考试时长（分钟）
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 设置考试时长
     * @param duration 考试时长（分钟）
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 获取考试开始时间
     * @return 开始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置考试开始时间
     * @param startTime 开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取考试结束时间
     * @return 结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置考试结束时间
     * @param endTime 结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取总分值
     * @return 总分值
     */
    public Integer getTotalScore() {
        return totalScore;
    }

    /**
     * 设置总分值
     * @param totalScore 总分值
     */
    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * 获取及格分数
     * @return 及格分数
     */
    public Integer getPassingScore() {
        return passingScore;
    }

    /**
     * 设置及格分数
     * @param passingScore 及格分数
     */
    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    /**
     * 获取创建教师邮箱
     * @return 教师邮箱
     */
    public String getCreatorEmail() {
        return creatorEmail;
    }

    /**
     * 设置创建教师邮箱
     * @param creatorEmail 教师邮箱
     */
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    /**
     * 获取发布状态
     * @return 是否已发布
     */
    public Boolean getIsPublished() {
        return isPublished;
    }

    /**
     * 设置发布状态
     * @param isPublished 是否已发布
     */
    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    /**
     * 获取考试分类
     * @return 考试分类
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置考试分类
     * @param category 考试分类
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取考试难度
     * @return 考试难度
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * 设置考试难度
     * @param difficulty 考试难度
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * 获取题目数量
     * @return 题目数量
     */
    public Integer getQuestionCount() {
        return questionCount;
    }

    /**
     * 设置题目数量
     * @param questionCount 题目数量
     */
    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    /**
     * 获取教师ID
     * @return 教师ID
     */
    public Long getTeacherId() {
        return teacherId;
    }

    /**
     * 设置教师ID
     * @param teacherId 教师ID
     */
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
    
    /**
     * 获取创建时间
     * @return 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
} 
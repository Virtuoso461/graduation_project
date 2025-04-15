package com.example.backend.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 课程实体类
 * 存储系统中所有课程的基本信息
 * 用于课程管理、课程展示和课程查询等功能
 */
@Entity
@Table(name = "courses")
public class Course {

    /**
     * 课程ID，主键，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 课程名称，不可为空
     */
    @Column(nullable = false)
    private String name;

    /**
     * 课程描述，最大长度1000字符
     * 详细介绍课程内容、目标和适用人群等
     */
    @Column(length = 1000)
    private String description;

    /**
     * 教师ID，关联到User表
     * 表示课程的创建者/教师
     */
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * 课程审核状态
     * true表示已审核通过，false表示未通过或待审核
     */
    @Column(name = "is_approved")
    private boolean approved;

    /**
     * 课程创建时间
     * 记录课程首次创建的时间戳
     */
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * 课程更新时间
     * 记录课程最后一次更新的时间戳
     */
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    /**
     * 课程分类
     * 如：编程、数学、物理、语言等
     */
    @Column(name = "category")
    private String category;

    /**
     * 课程封面图片URL
     * 存储课程封面图片的访问路径
     */
    @Column(name = "cover_image")
    private String coverImage;

    /**
     * 课程时长（分钟）
     * 估计完成课程需要的总时间
     */
    @Column(name = "duration")
    private Integer duration;

    /**
     * 课程难度级别
     * 通常分为：初级、中级、高级
     */
    @Column(name = "difficulty")
    private String difficulty;

    /**
     * 课程学分
     * 完成课程可获得的学分数量
     */
    @Column(name = "credits")
    private Integer credits;

    /**
     * 审核反馈
     * 管理员对课程的审核意见，最大长度1000字符
     */
    @Column(name = "approval_feedback", length = 1000)
    private String approvalFeedback;

    /**
     * 默认构造函数
     */
    public Course() {
    }

    /**
     * 获取课程ID
     * @return 课程ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置课程ID
     * @param id 课程ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取课程名称
     * @return 课程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置课程名称
     * @param name 课程名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取课程描述
     * @return 课程描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置课程描述
     * @param description 课程描述
     */
    public void setDescription(String description) {
        this.description = description;
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
     * 获取课程审核状态
     * @return 是否已审核通过
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * 设置课程审核状态
     * @param approved 是否已审核通过
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     * 获取课程创建时间
     * @return 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置课程创建时间
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取课程更新时间
     * @return 更新时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置课程更新时间
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取课程分类
     * @return 课程分类
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置课程分类
     * @param category 课程分类
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取课程封面图片URL
     * @return 封面图片URL
     */
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * 设置课程封面图片URL
     * @param coverImage 封面图片URL
     */
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    /**
     * 获取课程时长（分钟）
     * @return 课程时长
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 设置课程时长（分钟）
     * @param duration 课程时长
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 获取课程难度级别
     * @return 难度级别
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * 设置课程难度级别
     * @param difficulty 难度级别
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * 获取课程学分
     * @return 学分
     */
    public Integer getCredits() {
        return credits;
    }

    /**
     * 设置课程学分
     * @param credits 学分
     */
    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    /**
     * 获取审核反馈
     * @return 审核反馈
     */
    public String getApprovalFeedback() {
        return approvalFeedback;
    }

    /**
     * 设置审核反馈
     * @param approvalFeedback 审核反馈
     */
    public void setApprovalFeedback(String approvalFeedback) {
        this.approvalFeedback = approvalFeedback;
    }

    /**
     * 实体保存前自动设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    /**
     * 实体更新前自动更新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
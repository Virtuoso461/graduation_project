package com.example.backend.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 课程选修记录实体类
 */
@Entity
@Table(name = "course_enrollment", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "user_email"})})
public class CourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @Column
    private Integer progress = 0;
    
    @Column(name = "last_accessed_chapter_id")
    private Long lastAccessedChapterId;
    
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "completion_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completionDate;
    
    @Column
    private String grade;
    
    @Column(name = "has_certificate")
    private Boolean hasCertificate = false;
    
    @Column(name = "enroll_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enrollTime;
    
    @Column(name = "last_access_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessTime;
    
    // 构造函数
    public CourseEnrollment() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Long getLastAccessedChapterId() {
        return lastAccessedChapterId;
    }

    public void setLastAccessedChapterId(Long lastAccessedChapterId) {
        this.lastAccessedChapterId = lastAccessedChapterId;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Boolean getHasCertificate() {
        return hasCertificate;
    }

    public void setHasCertificate(Boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }

    public Date getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(Date enrollTime) {
        this.enrollTime = enrollTime;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
} 
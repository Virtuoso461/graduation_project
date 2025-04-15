package com.example.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 作业提交实体类
 */
@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属作业ID
     */
    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    /**
     * 所属作业实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;

    /**
     * 提交学生ID
     */
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 提交学生实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;

    /**
     * 提交内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 提交文件URL
     */
    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    /**
     * 评分
     */
    @Column(name = "score")
    private Double score;

    /**
     * 教师反馈
     */
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    /**
     * 是否已批改
     */
    @Column(name = "is_graded")
    private Boolean isGraded;

    /**
     * 提交时间
     */
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    /**
     * 批改时间
     */
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    // 默认构造函数
    public AssignmentSubmission() {
    }
    
    // 全参数构造函数
    public AssignmentSubmission(Long id, Long assignmentId, Long studentId, String content, String fileUrl,
                               Double score, String feedback, Boolean isGraded, LocalDateTime submittedAt,
                               LocalDateTime gradedAt) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.content = content;
        this.fileUrl = fileUrl;
        this.score = score;
        this.feedback = feedback;
        this.isGraded = isGraded;
        this.submittedAt = submittedAt;
        this.gradedAt = gradedAt;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
        this.assignmentId = assignment != null ? assignment.getId() : null;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
        this.studentId = student != null ? student.getId() : null;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Boolean getIsGraded() {
        return isGraded;
    }

    public void setIsGraded(Boolean isGraded) {
        this.isGraded = isGraded;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        isGraded = false;
    }
}

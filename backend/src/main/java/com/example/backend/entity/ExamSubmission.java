package com.example.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 考试提交实体类
 */
@Entity
@Table(name = "exam_submissions")
public class ExamSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属考试ID
     */
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    /**
     * 所属考试实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    private Exam exam;

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
     * 答案内容 (JSON格式)
     */
    @Column(name = "answers", columnDefinition = "TEXT")
    private String answers;

    /**
     * 提交文件URL
     */
    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    /**
     * 总分
     */
    @Column(name = "total_score")
    private Double totalScore;

    /**
     * 教师评语
     */
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

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

    /**
     * 开始考试时间
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    // 默认构造函数
    public ExamSubmission() {
    }

    // 全参数构造函数
    public ExamSubmission(Long id, Long examId, Long studentId, String answers, String fileUrl, Double totalScore,
                          String comments, Boolean isGraded, LocalDateTime submittedAt, LocalDateTime gradedAt,
                          LocalDateTime startedAt) {
        this.id = id;
        this.examId = examId;
        this.studentId = studentId;
        this.answers = answers;
        this.fileUrl = fileUrl;
        this.totalScore = totalScore;
        this.comments = comments;
        this.isGraded = isGraded;
        this.submittedAt = submittedAt;
        this.gradedAt = gradedAt;
        this.startedAt = startedAt;
    }

    // Getter和Setter方法
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

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
        this.examId = exam != null ? exam.getId() : null;
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

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        isGraded = false;
    }
}

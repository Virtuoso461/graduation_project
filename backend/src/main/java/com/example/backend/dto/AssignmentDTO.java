package com.example.backend.dto;

import com.example.backend.entity.Assignment;
import java.time.LocalDateTime;

public class AssignmentDTO {
    private Long id;
    private String email;
    private String title;
    private String description;
    private String status;
    private Integer score;
    private String feedback;
    private LocalDateTime dueDate;
    private LocalDateTime submissionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long teacherId;
    private String teacherName;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    // 转换方法
    public static AssignmentDTO fromEntity(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setEmail(assignment.getEmail());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setStatus(assignment.getStatus());
        dto.setScore(assignment.getScore());
        dto.setFeedback(assignment.getFeedback());
        dto.setDueDate(assignment.getDueDate());
        dto.setSubmissionDate(assignment.getSubmissionDate());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }

    public Assignment toEntity() {
        Assignment assignment = new Assignment();
        assignment.setId(this.id);
        assignment.setEmail(this.email);
        assignment.setTitle(this.title);
        assignment.setDescription(this.description);
        assignment.setStatus(this.status);
        assignment.setScore(this.score);
        assignment.setFeedback(this.feedback);
        assignment.setDueDate(this.dueDate);
        assignment.setSubmissionDate(this.submissionDate);
        assignment.setCreatedAt(this.createdAt);
        assignment.setUpdatedAt(this.updatedAt);
        return assignment;
    }
} 
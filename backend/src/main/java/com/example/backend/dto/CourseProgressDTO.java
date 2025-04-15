package com.example.backend.dto;

/**
 * 课程进度DTO
 */
public class CourseProgressDTO {
    private Integer progress;
    private Long chapterId;
    private Boolean completed;

    public CourseProgressDTO() {
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
} 
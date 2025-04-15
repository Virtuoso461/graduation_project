package com.example.backend.dto;

/**
 * 学习时间DTO
 */
public class StudyTimeDTO {
    private Integer minutes;
    private Long chapterId;

    public StudyTimeDTO() {
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }
} 
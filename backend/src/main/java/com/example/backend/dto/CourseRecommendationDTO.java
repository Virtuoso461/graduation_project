package com.example.backend.dto;

/**
 * 课程推荐DTO
 * 用于请求推荐课程
 */
public class CourseRecommendationDTO {
    private String category;
    private String difficulty;
    private Integer limit = 5;

    public CourseRecommendationDTO() {
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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
} 
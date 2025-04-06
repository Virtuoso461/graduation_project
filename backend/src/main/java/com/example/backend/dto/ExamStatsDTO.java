package com.example.backend.dto;

import java.io.Serializable;

public class ExamStatsDTO implements Serializable {
    
    private Integer totalExams; // 参加的考试总数
    private Integer completedExams; // 完成的考试数
    private Integer totalScore; // 总得分
    private Double averageScore; // 平均分
    private Double averageCorrectRate; // 平均正确率
    private Integer bestRanking; // 最好排名
    private String bestExamTitle; // 成绩最好的考试
    private Double categoryDistribution; // 考试类别分布
    
    public ExamStatsDTO() {
        this.totalExams = 0;
        this.completedExams = 0;
        this.totalScore = 0;
        this.averageScore = 0.0;
        this.averageCorrectRate = 0.0;
        this.bestRanking = 0;
        this.bestExamTitle = "";
        this.categoryDistribution = 0.0;
    }

    public Integer getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(Integer totalExams) {
        this.totalExams = totalExams;
    }

    public Integer getCompletedExams() {
        return completedExams;
    }

    public void setCompletedExams(Integer completedExams) {
        this.completedExams = completedExams;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getAverageCorrectRate() {
        return averageCorrectRate;
    }

    public void setAverageCorrectRate(Double averageCorrectRate) {
        this.averageCorrectRate = averageCorrectRate;
    }

    public Integer getBestRanking() {
        return bestRanking;
    }

    public void setBestRanking(Integer bestRanking) {
        this.bestRanking = bestRanking;
    }

    public String getBestExamTitle() {
        return bestExamTitle;
    }

    public void setBestExamTitle(String bestExamTitle) {
        this.bestExamTitle = bestExamTitle;
    }

    public Double getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Double categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }
} 
package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI指导记录实体类
 */
@Entity
@Table(name = "ai_guidance_records")
public class AIGuidanceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;
    
    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "topic")
    private String topic;
    
    // 构造函数
    public AIGuidanceRecord() {
    }
    
    public AIGuidanceRecord(String userEmail, String question, String answer) {
        this.userEmail = userEmail;
        this.question = question;
        this.answer = answer;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    @Override
    public String toString() {
        return "AIGuidanceRecord{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", question='" + question + '\'' +
                ", createdAt=" + createdAt +
                ", topic='" + topic + '\'' +
                '}';
    }
}

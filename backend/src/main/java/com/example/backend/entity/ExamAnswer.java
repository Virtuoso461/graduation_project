package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 考试答案实体类
 * 用于存储学生对考试题目的答案
 */
@Entity
@Table(name = "exam_answers")
public class ExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 考试ID
     */
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 题目ID
     */
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    /**
     * 题目类型
     * 例如：单选题、多选题、判断题、填空题、主观题等
     */
    @Column(name = "question_type", length = 50)
    private String questionType;

    /**
     * 用户答案
     * 存储用户的答案内容，对于选择题可以是选项编号，对于主观题可以是文本内容
     */
    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    /**
     * 正确答案
     * 题目的标准答案
     */
    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    /**
     * 是否正确
     * 用户答案是否与正确答案匹配
     */
    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    /**
     * 得分
     * 该题获得的分数
     */
    @Column(name = "score")
    private Double score = 0.0;

    /**
     * 评语
     * 教师对该答案的评语，主要用于主观题
     */
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    /**
     * 答题时间
     * 用户提交该答案的时间
     */
    @Column(name = "answer_time")
    private LocalDateTime answerTime;

    /**
     * 批改时间
     * 教师批改该答案的时间
     */
    @Column(name = "graded_time")
    private LocalDateTime gradedTime;

    /**
     * 知识点
     * 该题目关联的知识点
     */
    @Column(name = "knowledge_point", length = 100)
    private String knowledgePoint;

    /**
     * 默认构造函数
     */
    public ExamAnswer() {
    }

    /**
     * 全参数构造函数
     */
    public ExamAnswer(Long id, Long examId, Long userId, Long questionId, String questionType,
                     String userAnswer, String correctAnswer, Boolean isCorrect, Double score,
                     String comments, LocalDateTime answerTime, LocalDateTime gradedTime,
                     String knowledgePoint) {
        this.id = id;
        this.examId = examId;
        this.userId = userId;
        this.questionId = questionId;
        this.questionType = questionType;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
        this.score = score;
        this.comments = comments;
        this.answerTime = answerTime;
        this.gradedTime = gradedTime;
        this.knowledgePoint = knowledgePoint;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(LocalDateTime answerTime) {
        this.answerTime = answerTime;
    }

    public LocalDateTime getGradedTime() {
        return gradedTime;
    }

    public void setGradedTime(LocalDateTime gradedTime) {
        this.gradedTime = gradedTime;
    }

    public String getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(String knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }

    @PrePersist
    protected void onCreate() {
        answerTime = LocalDateTime.now();
    }
} 
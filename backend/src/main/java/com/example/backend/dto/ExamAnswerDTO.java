package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * 考试答案DTO
 * 用于前端提交考试答案
 */
public class ExamAnswerDTO {

    /**
     * 考试ID
     */
    @NotNull(message = "考试ID不能为空")
    private Long examId;

    /**
     * 答案列表
     * 每个Map包含题目ID(questionId)、用户答案(userAnswer)等信息
     */
    @NotNull(message = "答案不能为空")
    private List<Map<String, Object>> answers;

    /**
     * 所用时间(分钟)
     * 完成考试所用的时间
     */
    private Integer timeSpent;

    /**
     * 答案文件URL
     * 提交的附件URL，一般用于主观题或论述题上传的文件
     */
    private String fileUrl;

    /**
     * 自动评分
     * 是否需要自动评分，对客观题进行自动判分
     */
    private Boolean autoGrade = true;

    /**
     * 提交说明
     * 考生提交考试时的补充说明
     */
    private String submitNotes;

    /**
     * 默认构造函数
     */
    public ExamAnswerDTO() {
    }

    /**
     * 带参数构造函数
     */
    public ExamAnswerDTO(Long examId, List<Map<String, Object>> answers, Integer timeSpent, 
                          String fileUrl, Boolean autoGrade, String submitNotes) {
        this.examId = examId;
        this.answers = answers;
        this.timeSpent = timeSpent;
        this.fileUrl = fileUrl;
        this.autoGrade = autoGrade;
        this.submitNotes = submitNotes;
    }

    /**
     * 获取考试ID
     */
    public Long getExamId() {
        return examId;
    }

    /**
     * 设置考试ID
     */
    public void setExamId(Long examId) {
        this.examId = examId;
    }

    /**
     * 获取答案列表
     */
    public List<Map<String, Object>> getAnswers() {
        return answers;
    }

    /**
     * 设置答案列表
     */
    public void setAnswers(List<Map<String, Object>> answers) {
        this.answers = answers;
    }

    /**
     * 获取所用时间
     */
    public Integer getTimeSpent() {
        return timeSpent;
    }

    /**
     * 设置所用时间
     */
    public void setTimeSpent(Integer timeSpent) {
        this.timeSpent = timeSpent;
    }

    /**
     * 获取答案文件URL
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 设置答案文件URL
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 获取自动评分标志
     */
    public Boolean getAutoGrade() {
        return autoGrade;
    }

    /**
     * 设置自动评分标志
     */
    public void setAutoGrade(Boolean autoGrade) {
        this.autoGrade = autoGrade;
    }

    /**
     * 获取提交说明
     */
    public String getSubmitNotes() {
        return submitNotes;
    }

    /**
     * 设置提交说明
     */
    public void setSubmitNotes(String submitNotes) {
        this.submitNotes = submitNotes;
    }
}

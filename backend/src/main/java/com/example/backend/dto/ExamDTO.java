package com.example.backend.dto;

import com.example.backend.entity.Exam;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 测试数据传输对象，用于接收前端传来的测试数据
 */
public class ExamDTO {
    
    private Long id;
    
    @NotBlank(message = "测试标题不能为空")
    @Size(max = 255, message = "测试标题长度不能超过255个字符")
    private String title;
    
    @NotBlank(message = "测试类型不能为空")
    private String type;
    
    @Size(max = 1000, message = "测试描述长度不能超过1000个字符")
    private String description;
    
    @NotNull(message = "测试时长不能为空")
    @Min(value = 1, message = "测试时长必须大于0")
    private Integer duration;
    
    @NotNull(message = "题目数量不能为空")
    @Min(value = 1, message = "题目数量必须大于0")
    private Integer questionCount;
    
    @NotNull(message = "总分值不能为空")
    @Min(value = 1, message = "总分值必须大于0")
    private Integer totalScore;
    
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;
    
    private String courseId;
    
    private String courseName;
    
    private Boolean enabled;
    
    // 教师邮箱，从认证信息中获取
    private String email;
    
    // 构造函数
    public ExamDTO() {
    }
    
    // 将实体转换为DTO
    public static ExamDTO fromEntity(Exam exam) {
        ExamDTO dto = new ExamDTO();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setType(exam.getCategory());
        dto.setDescription(exam.getDescription());
        dto.setDuration(exam.getDuration());
        dto.setQuestionCount(exam.getQuestionCount());
        dto.setTotalScore(exam.getTotalScore());
        if (exam.getEndTime() != null) {
            LocalDateTime deadline = LocalDateTime.ofInstant(
                exam.getEndTime().toInstant(), 
                java.time.ZoneId.systemDefault()
            );
            dto.setDeadline(deadline);
        }
        dto.setCourseName(exam.getCourseName());
        dto.setEnabled(exam.getIsPublished());
        dto.setEmail(exam.getCreatorEmail());
        return dto;
    }
    
    // 将DTO转换为实体
    public Exam toEntity() {
        Exam exam = new Exam();
        exam.setId(this.id);
        exam.setTitle(this.title);
        exam.setCategory(this.type);
        exam.setDescription(this.description);
        exam.setDuration(this.duration);
        exam.setQuestionCount(this.questionCount);
        exam.setTotalScore(this.totalScore);
        if (this.deadline != null) {
            Date endTime = Date.from(this.deadline.atZone(
                java.time.ZoneId.systemDefault()).toInstant()
            );
            exam.setEndTime(endTime);
        }
        exam.setCourseName(this.courseName);
        exam.setIsPublished(this.enabled != null ? this.enabled : true);
        exam.setCreatorEmail(this.email);
        return exam;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
} 
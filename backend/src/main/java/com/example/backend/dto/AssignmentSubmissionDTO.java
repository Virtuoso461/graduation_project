package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 作业提交DTO
 */
public class AssignmentSubmissionDTO {

    @NotNull(message = "作业ID不能为空")
    private Long assignmentId;

    private String content;

    private String fileUrl;

    public AssignmentSubmissionDTO() {
    }

    public AssignmentSubmissionDTO(Long assignmentId, String content, String fileUrl) {
        this.assignmentId = assignmentId;
        this.content = content;
        this.fileUrl = fileUrl;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}

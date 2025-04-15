package com.example.backend.dto;

/**
 * 回复DTO
 */
public class ReplyDTO {
    private String content;
    private Long parentId;

    public ReplyDTO() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
} 
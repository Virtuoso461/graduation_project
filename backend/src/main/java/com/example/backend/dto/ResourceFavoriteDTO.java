package com.example.backend.dto;

/**
 * 资源收藏DTO
 */
public class ResourceFavoriteDTO {
    private String folderId;
    private String notes;

    public ResourceFavoriteDTO() {
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 
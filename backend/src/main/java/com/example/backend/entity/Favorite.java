package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 收藏记录实体类
 */
@Entity
@Table(name = "favorite", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"resource_id", "resource_type", "user_email"})})
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "resource_id", nullable = false)
    private Long resourceId; // 收藏的资源ID
    
    @Column(name = "resource_type", nullable = false)
    private String resourceType; // 资源类型：资源库、课程、作业等
    
    @Column(name = "user_email", nullable = false)
    private String userEmail; // 用户邮箱
    
    @Column(name = "folder_id")
    private String folderId; // 所属文件夹ID
    
    @Column(name = "resource_title")
    private String resourceTitle; // 资源标题
    
    @Column(name = "resource_description", columnDefinition = "TEXT")
    private String resourceDescription; // 资源描述
    
    @Column(name = "resource_url")
    private String resourceUrl; // 资源URL
    
    @Column(name = "collection_time")
    private LocalDateTime collectionTime; // 收藏时间
    
    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime; // 最后访问时间
    
    @Column
    private String notes; // 收藏备注
    
    // 构造函数
    public Favorite() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getResourceDescription() {
        return resourceDescription;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(LocalDateTime collectionTime) {
        this.collectionTime = collectionTime;
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(LocalDateTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 
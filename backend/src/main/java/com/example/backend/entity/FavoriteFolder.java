package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 收藏夹实体类
 */
@Entity
@Table(name = "favorite_folder", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"folder_id", "user_email"})})
public class FavoriteFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "folder_id", nullable = false)
    private String folderId; // 文件夹唯一标识
    
    @Column(nullable = false)
    private String name; // 文件夹名称
    
    @Column
    private String description; // 文件夹描述
    
    @Column(name = "user_email", nullable = false)
    private String userEmail; // 所属用户
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新时间
    
    @Column
    private String icon; // 文件夹图标
    
    @Column
    private Integer sortOrder; // 排序顺序
    
    // 构造函数
    public FavoriteFolder() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
} 
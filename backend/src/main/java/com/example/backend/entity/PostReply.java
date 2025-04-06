package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 帖子回复实体类
 */
@Entity
@Table(name = "post_replies")
public class PostReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "post_id", nullable = false)
    private Long postId;
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    @Column(name = "author_email", nullable = false)
    private String authorEmail;
    
    @Column(name = "author_name")
    private String authorName;
    
    @Column(name = "author_avatar")
    private String authorAvatar;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    
    @Column
    private Integer likes = 0;
    
    @Column(name = "parent_id")
    private Long parentId; // 用于回复的回复，如果为null则为一级回复
    
    // 构造函数
    public PostReply() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
} 
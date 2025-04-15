package com.example.backend.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 用户实体类
 * 用于存储系统中所有用户的基本信息，包括学生、教师和管理员
 * 作为用户认证、授权和个人信息管理的基础
 */
@Entity
@Table(name = "user")
public class User {
    /**
     * 用户ID，主键，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名/邮箱，作为登录凭证，不可重复
     * 在本系统中使用邮箱作为用户名
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 用户密码，存储加密后的密码字符串
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * 用户角色，对应Role枚举
     * 决定用户在系统中的权限和可访问的功能
     */
    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;
    
    /**
     * 用户创建时间
     * 记录用户账号的注册时间
     */
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    /**
     * 最后登录时间
     * 记录用户最近一次成功登录系统的时间
     */
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
    
    /**
     * 账号是否启用
     * true表示账号可用，false表示账号被禁用
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * 默认构造函数
     */
    public User() {
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名/邮箱
     * @return 用户名/邮箱
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名/邮箱
     * @param username 用户名/邮箱
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取用户密码（加密后）
     * @return 加密后的密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户密码
     * @param password 用户密码（通常在保存前会进行加密）
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户角色
     * @return 用户角色
     */
    public Role getRole() {
        return role;
    }

    /**
     * 设置用户角色
     * @param role 用户角色
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * 获取账号创建时间
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置账号创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取最后登录时间
     * @return 最后登录时间
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * 设置最后登录时间
     * @param lastLoginTime 最后登录时间
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    /**
     * 获取账号启用状态
     * @return 是否启用
     */
    public Boolean getEnabled() {
        return enabled;
    }
    
    /**
     * 设置账号启用状态
     * @param enabled 是否启用
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
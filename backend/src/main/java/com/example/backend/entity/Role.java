package com.example.backend.entity;

/**
 * 用户角色枚举
 * 定义系统中的用户角色类型，用于权限控制和身份识别
 */
public enum Role {
    /**
     * 学生角色：普通用户，可以参与课程学习、考试等
     */
    STUDENT,
    
    /**
     * 教师角色：可以创建和管理课程、考试、作业等教学资源
     */
    TEACHER,
    
    /**
     * 管理员角色：系统管理员，拥有最高权限，可以管理所有资源和用户
     */
    ADMIN
}
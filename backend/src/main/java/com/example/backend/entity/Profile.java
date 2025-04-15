package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/**
 * 用户个人资料实体类
 * 存储用户的详细个人信息，与User实体关联但分开存储
 * 主要用于用户信息展示和个人资料管理
 */
@Entity
public class Profile {

    /**
     * 电子邮箱，作为主键
     * 与User表中的username字段对应
     */
    @Id
    @Email(message = "邮箱格式不正确")
    @Column(nullable = false)
    private String email;
    
    /**
     * 用户名/昵称
     * 用户在平台上显示的名称
     */
    private String username;
    
    /**
     * 真实姓名
     * 用户的实际姓名
     */
    private String name;
    
    /**
     * 电话号码
     * 用户的联系电话
     */
    private String phoneNumber;
    
    /**
     * 性别
     * 用户的性别信息
     */
    private String gender;
    
    /**
     * 出生日期
     * 必须是过去的日期
     */
    @Past(message = "生日必须是过去的日期")
    private LocalDate birthday;
    
    /**
     * 个人简介
     * 用户自我描述，限制1000字符
     */
    @Column(length = 1000)
    private String bio;
    
    /**
     * 头像
     * 存储用户头像的URL路径
     */
    private String avatar;
    
    /**
     * 默认构造函数
     */
    public Profile() {
    }

    /**
     * 带邮箱参数的构造函数
     * @param email 用户邮箱
     */
    public Profile(String email) {
        this.email = email;
    }

    /**
     * 带全部参数的构造函数
     * @param email 用户邮箱
     * @param username 用户名/昵称
     * @param name 真实姓名
     * @param phoneNumber 电话号码
     * @param gender 性别
     * @param birthday 出生日期
     * @param bio 个人简介
     * @param avatar 头像URL
     */
    public Profile(String email, String username, String name, String phoneNumber, 
                   String gender, LocalDate birthday, String bio, String avatar) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthday = birthday;
        this.bio = bio;
        this.avatar = avatar;
    }

    /**
     * 获取用户邮箱
     * @return 邮箱地址
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置用户邮箱
     * @param email 邮箱地址
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取用户名/昵称
     * @return 用户名/昵称
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名/昵称
     * @param username 用户名/昵称
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取真实姓名
     * @return 真实姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置真实姓名
     * @param name 真实姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取电话号码
     * @return 电话号码
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置电话号码
     * @param phoneNumber 电话号码
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 获取性别
     * @return 性别
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置性别
     * @param gender 性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 获取出生日期
     * @return 出生日期
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * 设置出生日期
     * @param birthday 出生日期
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取个人简介
     * @return 个人简介
     */
    public String getBio() {
        return bio;
    }

    /**
     * 设置个人简介
     * @param bio 个人简介
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * 获取头像URL
     * @return 头像URL
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像URL
     * @param avatar 头像URL
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 将对象转换为字符串表示
     * @return 对象的字符串表示
     */
    @Override
    public String toString() {
        return "Profile{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", bio='" + bio + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}

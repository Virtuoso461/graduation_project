package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@Entity
public class Profile {

    @Id
    @Email(message = "邮箱格式不正确")
    @Column(nullable = false)
    private String email;
    
    private String username;
    
    private String name;
    
    private String phoneNumber;
    
    private String gender;
    
    @Past(message = "生日必须是过去的日期")
    private LocalDate birthday;
    
    @Column(length = 1000)
    private String bio;
    
    private String avatar;
    
    // 默认构造函数
    public Profile() {
    }

    // 带参数的构造函数
    public Profile(String email) {
        this.email = email;
    }

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

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

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

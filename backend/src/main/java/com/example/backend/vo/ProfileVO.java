package com.example.backend.vo;

public class ProfileVO {
    
    private String email;
    private String username;
    private String name;
    private String phoneNumber;
    private String gender;
    private String birthday;
    private String bio;
    private String avatar;
    
    public ProfileVO() {
    }
    
    public ProfileVO(String email) {
        this.email = email;
    }
    
    public ProfileVO(String email, String username, String name, String phoneNumber, 
                    String gender, String birthday, String bio, String avatar) {
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
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String birthday) {
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
} 
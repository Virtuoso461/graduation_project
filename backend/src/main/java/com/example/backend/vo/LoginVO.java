package com.example.backend.vo;

import com.example.backend.entity.Role;

public class LoginVO {
    private Long id;
    private String username;
    private String phoneNumber;
    private Long number;
    private Role role;

    public LoginVO(Long id, String username) {
        this.id = id;
        this.username = username;
    }
    
    public LoginVO(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
    
    public LoginVO(Long id, String username, String phoneNumber, Long number, Role role) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.number = number;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}

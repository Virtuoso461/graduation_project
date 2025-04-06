package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 密码重置相关数据传输对象
 */
public class PasswordResetDTO {

    // 用于请求验证码
    @Email(message = "邮箱格式不正确")
    private String email;
    
    // 用于验证验证码
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;
    
    // 用于重置密码
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    
    public PasswordResetDTO() {
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
} 
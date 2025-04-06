package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.PasswordResetDTO;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

/**
 * 密码重置控制器
 */
@RestController
@RequestMapping("/reset-password")
public class PasswordResetController {

    @Autowired
    private UserService userService;
    
    /**
     * 发送密码重置验证码
     * 
     * @param email 用户邮箱
     * @return 发送结果
     */
    @GetMapping("/code")
    public Result<String> sendResetCode(@RequestParam @Email String email) {
        String result = userService.generatePasswordResetCode(email);
        return Result.success(result);
    }
    
    /**
     * 验证重置验证码
     * 
     * @param email 用户邮箱
     * @param code 验证码
     * @return 验证结果
     */
    @GetMapping("/verify")
    public Result<Boolean> verifyResetCode(
            @RequestParam @Email String email,
            @RequestParam String code) {
        boolean verified = userService.verifyPasswordResetCode(email, code);
        return Result.success(verified);
    }
    
    /**
     * 重置密码
     * 
     * @param resetDTO 重置密码数据
     * @return 重置结果
     */
    @PostMapping("/reset")
    public Result<String> resetPassword(@RequestBody @Valid PasswordResetDTO resetDTO) {
        String result = userService.resetPassword(
                resetDTO.getEmail(),
                resetDTO.getNewPassword(),
                resetDTO.getVerificationCode()
        );
        return Result.success(result);
    }
} 
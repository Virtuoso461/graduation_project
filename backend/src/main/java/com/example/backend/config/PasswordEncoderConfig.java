package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置类
 * 为系统提供PasswordEncoder Bean，用于密码的加密和验证
 */
@Configuration
public class PasswordEncoderConfig {
    
    /**
     * 创建PasswordEncoder Bean
     * 使用BCrypt算法进行密码加密
     * 
     * @return PasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 
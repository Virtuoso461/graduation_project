package com.example.backend.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     *
     * @param to 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String to, String code);

    /**
     * 发送密码重置邮件
     *
     * @param to 收件人邮箱
     * @param resetToken 重置令牌
     * @return 是否发送成功
     */
    boolean sendPasswordResetEmail(String to, String resetToken);
}
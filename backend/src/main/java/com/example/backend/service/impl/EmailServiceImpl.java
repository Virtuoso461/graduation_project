package com.example.backend.service.impl;

import com.example.backend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件服务实现类
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:notset}")
    private String username;
    
    @Value("${spring.mail.password:FAkhefmNNXcQxyRP}")
    private String password;
    
    @Value("${spring.mail.host:imap.163.com}")
    private String host;
    
    @Value("${spring.mail.port:465}")
    private int port;
    
    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean auth;
    
    @Value("${spring.mail.properties.mail.smtp.ssl.enable:true}")
    private boolean sslEnable;
    
    @PostConstruct
    public void init() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.ssl.enable", sslEnable);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.debug", "true");
        
        this.mailSender = sender;
    }
    
    @Override
    public boolean sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject("密码重置验证码");
            
            String content = String.format(
                "<html><body>" +
                "<h2>密码重置验证码</h2>" +
                "<p>您好，您正在尝试重置密码，您的验证码是：</p>" +
                "<h3 style='color:red;'>%s</h3>" +
                "<p>此验证码有效期为5分钟，请勿泄露给他人。</p>" +
                "<p>如果您没有请求此操作，请忽略此邮件。</p>" +
                "</body></html>",
                code
            );
            
            helper.setText(content, true);
            mailSender.send(message);
            return true;
            
        } catch (MessagingException e) {
            logger.error("发送验证码邮件失败", e);
            return false;
        }
    }
    
    @Override
    public boolean sendPasswordResetEmail(String to, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject("密码重置链接");
            
            String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;
            
            String content = String.format(
                "<html><body>" +
                "<h2>密码重置链接</h2>" +
                "<p>您好，您正在尝试重置密码，请点击下面的链接继续操作：</p>" +
                "<a href='%s' style='display:inline-block;background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;border-radius:4px;'>重置密码</a>" +
                "<p>或者复制以下链接到浏览器地址栏：</p>" +
                "<p>%s</p>" +
                "<p>此链接有效期为30分钟，请勿泄露给他人。</p>" +
                "<p>如果您没有请求此操作，请忽略此邮件。</p>" +
                "</body></html>",
                resetUrl, resetUrl
            );
            
            helper.setText(content, true);
            mailSender.send(message);
            return true;
            
        } catch (MessagingException e) {
            logger.error("发送密码重置邮件失败", e);
            return false;
        }
    }
} 
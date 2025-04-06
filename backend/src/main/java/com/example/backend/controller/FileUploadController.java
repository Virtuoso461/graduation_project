package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Profile;
import com.example.backend.repository.ProfileRepository;
import com.example.backend.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${file.upload.avatar-dir}")
    private String avatarDir;

    @Value("${file.upload.avatar-base-url}")
    private String avatarBaseUrl;

    @Value("${file.upload.frontend-path}")
    private String frontendPath;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileService profileService;

    /**
     * 上传头像
     * @param file 头像文件
     * @param email 用户邮箱 (使用查询参数)
     * @return 上传结果
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email) {

        logger.info("接收到头像上传请求，邮箱：{}，文件大小：{}", email, file.getSize());
        logger.info("当前配置 - 前端路径：{}，头像目录：{}", frontendPath, avatarDir);
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            logger.warn("上传的文件为空");
            return Result.failed("上传的文件不能为空");
        }

        // 检查邮箱是否为空
        if (email == null || email.trim().isEmpty()) {
            logger.error("邮箱参数为空");
            return Result.failed("邮箱参数不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png"))) {
            logger.warn("不支持的文件类型：{}", contentType);
            return Result.failed("只支持JPEG和PNG格式的图片");
        }

        try {
            // 使用File对象处理路径，避免使用Path API
            File frontendDir = new File(frontendPath);
            logger.info("前端路径是否绝对路径: {}", frontendDir.isAbsolute());
            logger.info("前端路径是否存在: {}", frontendDir.exists());
            
            // 检查前端路径是否为绝对路径
            if (!frontendDir.isAbsolute()) {
                logger.error("前端路径配置错误：不是绝对路径 - {}", frontendPath);
                return Result.failed("服务器配置错误：请使用绝对路径配置前端目录");
            }
            
            // 确保前端目录存在
            if (!frontendDir.exists()) {
                logger.error("前端路径不存在: {}", frontendPath);
                return Result.failed("服务器配置错误：前端目录不存在");
            }
            
            // 构建头像存储目录
            File uploadDir = new File(frontendDir, avatarDir);
            logger.info("上传目录完整路径：{}", uploadDir.getAbsolutePath());
            
            // 确保头像存储目录存在
            if (!uploadDir.exists()) {
                logger.info("上传目录不存在，正在创建：{}", uploadDir.getAbsolutePath());
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    logger.error("无法创建目录：{}", uploadDir.getAbsolutePath());
                    return Result.failed("服务器配置错误：无法创建上传目录");
                }
            }

            // 根据邮箱生成文件名
            String fileExtension = getFileExtension(file.getOriginalFilename());
            // 确保文件名基础部分有效
            String safeEmail = email.replaceAll("[^a-zA-Z0-9.-]", "_");
            
            // 如果处理后的邮箱为空，使用时间戳或UUID
            if (safeEmail == null || safeEmail.trim().isEmpty()) {
                safeEmail = "user_" + System.currentTimeMillis();
                logger.warn("处理后的邮箱名为空，使用时间戳生成文件名：{}", safeEmail);
            }
            
            String fileName = safeEmail + fileExtension;
            logger.info("生成的文件名：{}", fileName);

            // 创建目标文件
            File destFile = new File(uploadDir, fileName);
            logger.info("文件将保存到：{}", destFile.getAbsolutePath());
            
            // 保存文件
            try {
                file.transferTo(destFile);
            } catch (IOException e) {
                logger.error("保存文件时出错：{}", e.getMessage(), e);
                return Result.failed("保存文件失败：" + e.getMessage());
            }
            
            // 验证文件是否成功保存
            if (destFile.exists()) {
                logger.info("文件成功保存，大小：{} 字节", destFile.length());
            } else {
                logger.warn("文件似乎未成功保存");
                return Result.failed("文件保存失败：无法验证文件是否已保存");
            }

            // 构建访问URL
            String avatarUrl = avatarBaseUrl + "/" + fileName;
            logger.info("生成的头像URL：{}", avatarUrl);

            // 更新用户个人资料中的头像URL
            Optional<Profile> profileOpt = profileRepository.findByEmail(email);
            if (profileOpt.isPresent()) {
                Profile profile = profileOpt.get();
                profile.setAvatar(avatarUrl);
                profileRepository.save(profile);
                logger.info("更新用户头像URL成功");
            } else {
                logger.warn("未找到邮箱为{}的用户资料", email);
            }

            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", avatarUrl);

            logger.info("头像上传成功: {}", avatarUrl);
            return Result.success(result, "头像上传成功");

        } catch (Exception e) {
            logger.error("头像上传过程中发生错误：{}", e.getMessage(), e);
            return Result.failed("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            logger.warn("原始文件名为空，默认使用.jpg扩展名");
            return ".jpg";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            logger.warn("无法从文件名'{}'获取扩展名，默认使用.jpg", filename);
            return ".jpg";
        }
        String extension = filename.substring(lastDotIndex);
        logger.debug("从文件名'{}'获取到扩展名：{}", filename, extension);
        return extension;
    }
} 
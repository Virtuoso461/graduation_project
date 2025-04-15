package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.PasswordChangeDTO;
import com.example.backend.dto.ProfileDTO;
import com.example.backend.entity.Profile;
import com.example.backend.entity.User;
import com.example.backend.service.TeacherProfileService;
import com.example.backend.service.UserService;
import com.example.backend.vo.ProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 教师个人中心控制器
 * 提供教师个人资料管理功能
 */
@RestController
@RequestMapping("/api/teacher/profile")
public class TeacherProfileController {

    private final TeacherProfileService teacherProfileService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload.avatar-dir:images/avatars}")
    private String avatarDir;

    @Value("${file.upload.frontend-path:frontend/public}")
    private String frontendPath;

    @Value("${file.upload.avatar-base-url:/images/avatars}")
    private String avatarBaseUrl;

    @Autowired
    public TeacherProfileController(TeacherProfileService teacherProfileService, 
                                   UserService userService, 
                                   PasswordEncoder passwordEncoder) {
        this.teacherProfileService = teacherProfileService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前登录用户的邮箱
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 获取个人资料
     *
     * @return 个人资料
     */
    @GetMapping
    public Result<ProfileVO> getProfile() {
        try {
            String email = getCurrentUserEmail();
            Profile profile = teacherProfileService.getProfile(email);

            ProfileVO profileVO = new ProfileVO();
            profileVO.setEmail(profile.getEmail());
            profileVO.setUsername(profile.getUsername());
            profileVO.setName(profile.getName());
            profileVO.setPhoneNumber(profile.getPhoneNumber());
            profileVO.setGender(profile.getGender());
            profileVO.setBirthday(profile.getBirthday() != null ? profile.getBirthday().toString() : null);
            profileVO.setBio(profile.getBio());
            profileVO.setAvatar(profile.getAvatar());

            return Result.success(profileVO);
        } catch (Exception e) {
            return Result.failed("获取个人资料失败: " + e.getMessage());
        }
    }

    /**
     * 更新个人资料
     *
     * @param profileDTO 个人资料DTO
     * @return 更新后的个人资料
     */
    @PostMapping
    public Result<ProfileVO> updateProfile(@RequestBody ProfileDTO profileDTO) {
        try {
            String email = getCurrentUserEmail();

            // 确保邮箱一致
            profileDTO.setEmail(email);

            Profile profile = teacherProfileService.getProfile(email);

            // 更新个人资料字段
            if (profileDTO.getUsername() != null) {
                profile.setUsername(profileDTO.getUsername());
            }
            if (profileDTO.getName() != null) {
                profile.setName(profileDTO.getName());
            }
            if (profileDTO.getPhoneNumber() != null) {
                profile.setPhoneNumber(profileDTO.getPhoneNumber());
            }
            if (profileDTO.getGender() != null) {
                profile.setGender(profileDTO.getGender());
            }
            if (profileDTO.getBirthday() != null) {
                profile.setBirthday(LocalDate.parse(profileDTO.getBirthday()));
            }
            if (profileDTO.getBio() != null) {
                profile.setBio(profileDTO.getBio());
            }
            if (profileDTO.getAvatar() != null) {
                profile.setAvatar(profileDTO.getAvatar());
            }

            // 保存更新后的个人资料
            profile = teacherProfileService.updateProfile(profile);

            // 转换为VO
            ProfileVO profileVO = new ProfileVO();
            profileVO.setEmail(profile.getEmail());
            profileVO.setUsername(profile.getUsername());
            profileVO.setName(profile.getName());
            profileVO.setPhoneNumber(profile.getPhoneNumber());
            profileVO.setGender(profile.getGender());
            profileVO.setBirthday(profile.getBirthday() != null ? profile.getBirthday().toString() : null);
            profileVO.setBio(profile.getBio());
            profileVO.setAvatar(profile.getAvatar());

            return Result.success(profileVO, "个人资料更新成功");
        } catch (Exception e) {
            return Result.failed("更新个人资料失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String email = getCurrentUserEmail();

            if (file.isEmpty()) {
                return Result.failed("请选择要上传的文件");
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID().toString() + extension;

            // 确保目录存在
            String uploadDir = frontendPath + "/" + avatarDir;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件
            Path filePath = Paths.get(uploadDir, filename);
            file.transferTo(filePath.toFile());

            // 生成头像URL
            String avatarUrl = avatarBaseUrl + "/" + filename;

            // 更新个人资料
            Profile profile = teacherProfileService.getProfile(email);
            profile.setAvatar(avatarUrl);
            teacherProfileService.updateProfile(profile);

            return Result.success(avatarUrl, "头像上传成功");
        } catch (IOException e) {
            return Result.failed("头像上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.failed("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     *
     * @param passwordChangeDTO 密码修改DTO
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            String email = getCurrentUserEmail();

            // 验证旧密码
            User user = userService.findByUsername(email);
            if (user == null) {
                return Result.failed("用户不存在");
            }

            if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
                return Result.failed("旧密码不正确");
            }

            // 更新密码
            boolean success = userService.changePassword(email, passwordChangeDTO.getOldPassword(), passwordChangeDTO.getNewPassword());

            return success ? 
                Result.success(true, "密码修改成功") : 
                Result.failed("密码修改失败");
        } catch (Exception e) {
            return Result.failed("密码修改失败: " + e.getMessage());
        }
    }
}

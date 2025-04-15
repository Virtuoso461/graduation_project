package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.PasswordChangeDTO;
import com.example.backend.dto.ProfileDTO;
import com.example.backend.entity.AssignmentSubmission;
import com.example.backend.entity.ExamResult;
import com.example.backend.entity.Profile;
import com.example.backend.entity.User;
import com.example.backend.service.AssignmentService;
import com.example.backend.service.ExamService;
import com.example.backend.service.StudentProfileService;
import com.example.backend.service.StudentService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 学生个人中心控制器
 * 提供学生个人学习记录、作业提交情况、测试成绩等功能
 */
@RestController
@RequestMapping("/api/student/profile")
public class StudentProfileController {

    private final StudentService studentService;
    private final AssignmentService assignmentService;
    private final ExamService examService;
    private final StudentProfileService studentProfileService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload.avatar-dir:images/avatars}")
    private String avatarDir;

    @Value("${file.upload.frontend-path:frontend/public}")
    private String frontendPath;

    @Value("${file.upload.avatar-base-url:/images/avatars}")
    private String avatarBaseUrl;

    @Autowired
    public StudentProfileController(StudentService studentService,
                                   AssignmentService assignmentService,
                                   ExamService examService,
                                   StudentProfileService studentProfileService,
                                   UserService userService,
                                   PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.assignmentService = assignmentService;
        this.examService = examService;
        this.studentProfileService = studentProfileService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取学生学习概况
     *
     * @return 学习概况数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getStudyOverview() {
        try {
            String userEmail = getCurrentUserEmail();
            Map<String, Object> overview = studentProfileService.getStudyOverview(userEmail);
            return Result.success(overview);
        } catch (Exception e) {
            return Result.failed("获取学习概况失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业提交情况
     *
     * @param params 请求参数，包含status（可选，pending-待完成，completed-已完成）
     * @return 作业提交列表
     */
    @GetMapping("/assignments")
    public Result<List<AssignmentSubmission>> getAssignmentSubmissions(@RequestBody(required = false) Map<String, String> params) {
        try {
            String userEmail = getCurrentUserEmail();
            String status = params != null ? params.get("status") : null;
            List<AssignmentSubmission> submissions = studentProfileService.getAssignmentSubmissions(userEmail, status);
            return Result.success(submissions);
        } catch (Exception e) {
            return Result.failed("获取作业提交情况失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试成绩
     *
     * @param params 请求参数，包含startDate和endDate（可选）
     * @return 考试成绩列表
     */
    @GetMapping("/exams")
    public Result<List<ExamResult>> getExamResults(@RequestBody(required = false) Map<String, Date> params) {
        try {
            String userEmail = getCurrentUserEmail();
            Date startDate = params != null ? params.get("startDate") : null;
            Date endDate = params != null ? params.get("endDate") : null;
            List<ExamResult> examResults = studentProfileService.getExamResults(userEmail, startDate, endDate);
            return Result.success(examResults);
        } catch (Exception e) {
            return Result.failed("获取考试成绩失败: " + e.getMessage());
        }
    }

    /**
     * 获取学习统计数据
     *
     * @return 学习统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStudyStatistics() {
        try {
            String userEmail = getCurrentUserEmail();
            Map<String, Object> statistics = studentProfileService.getStudyStatistics(userEmail);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.failed("获取学习统计数据失败: " + e.getMessage());
        }
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
            ProfileVO profileVO = studentProfileService.getProfileVO(email);
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
            ProfileVO profileVO = studentProfileService.updateProfileFromDTO(profileDTO);
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
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String email = getCurrentUserEmail();
            Map<String, String> result = studentProfileService.uploadAvatar(email, file);
            return Result.success(result, "头像上传成功");
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
    public Result<String> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            String email = getCurrentUserEmail();
            boolean success = studentService.changePassword(email, 
                                                           passwordChangeDTO.getOldPassword(), 
                                                           passwordChangeDTO.getNewPassword());
            if (success) {
                return Result.success(null, "密码修改成功");
            } else {
                return Result.failed("旧密码不正确");
            }
        } catch (Exception e) {
            return Result.failed("密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 获取学习记录
     *
     * @return 学习记录列表
     */
    @GetMapping("/learning-records")
    public Result<List<Map<String, Object>>> getLearningRecords() {
        try {
            String userEmail = getCurrentUserEmail();
            List<Map<String, Object>> records = studentProfileService.getLearningRecords(userEmail);
            return Result.success(records);
        } catch (Exception e) {
            return Result.failed("获取学习记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取学习趋势
     *
     * @return 学习趋势数据
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getStudyTrend() {
        try {
            String userEmail = getCurrentUserEmail();
            Map<String, Object> trend = studentProfileService.getStudyTrend(userEmail);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.failed("获取学习趋势失败: " + e.getMessage());
        }
    }
}

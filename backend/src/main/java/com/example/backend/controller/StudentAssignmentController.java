package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.AssignmentDTO;
import com.example.backend.dto.AssignmentSubmissionDTO;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.AssignmentSubmission;
import com.example.backend.entity.User;
import com.example.backend.service.AssignmentService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 学生作业控制器
 * 提供学生作业相关的API
 */
@RestController
@RequestMapping("/api/student/assignments")
public class StudentAssignmentController {

    private final AssignmentService assignmentService;
    private final UserService userService;

    @Value("${file.upload.assignment-dir:files/assignments}")
    private String assignmentDir;

    @Value("${file.upload.frontend-path:frontend/public}")
    private String frontendPath;

    @Value("${file.upload.assignment-base-url:/files/assignments}")
    private String assignmentBaseUrl;

    @Autowired
    public StudentAssignmentController(AssignmentService assignmentService, UserService userService) {
        this.assignmentService = assignmentService;
        this.userService = userService;
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());
        return userService.findById(userId);
    }

    /**
     * 获取可见的作业列表
     *
     * @return 作业列表
     */
    @GetMapping
    public Result<List<AssignmentDTO>> getVisibleAssignments() {
        try {
            User student = getCurrentUser();
            List<Assignment> assignments = assignmentService.findAllAssignments();
            
            // 转换为DTO
            List<AssignmentDTO> assignmentDTOs = assignments.stream()
                    .map(AssignmentDTO::fromEntity)
                    .collect(Collectors.toList());
            
            return Result.success(assignmentDTOs);
        } catch (Exception e) {
            return Result.failed("获取作业列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业详情
     *
     * @param assignmentId 作业ID
     * @return 作业详情
     */
    @GetMapping("/{assignmentId}")
    public Result<Map<String, Object>> getAssignmentDetail(@PathVariable Long assignmentId) {
        try {
            User student = getCurrentUser();
            
            // 获取作业详情
            Assignment assignment = assignmentService.findAssignmentById(assignmentId);
            
            // 获取学生的提交情况
            AssignmentSubmission submission = assignmentService.getStudentSubmission(assignmentId, student.getId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("assignment", AssignmentDTO.fromEntity(assignment));
            
            if (submission != null) {
                Map<String, Object> submissionData = new HashMap<>();
                submissionData.put("id", submission.getId());
                submissionData.put("content", submission.getContent());
                submissionData.put("fileUrl", submission.getFileUrl());
                submissionData.put("score", submission.getScore());
                submissionData.put("feedback", submission.getFeedback());
                submissionData.put("isGraded", submission.getIsGraded());
                submissionData.put("submittedAt", submission.getSubmittedAt());
                submissionData.put("gradedAt", submission.getGradedAt());
                
                result.put("submission", submissionData);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("获取作业详情失败: " + e.getMessage());
        }
    }

    /**
     * 提交作业
     *
     * @param submissionDTO 作业提交DTO
     * @return 提交结果
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitAssignment(@RequestBody AssignmentSubmissionDTO submissionDTO) {
        try {
            User student = getCurrentUser();
            
            // 检查作业是否存在
            Assignment assignment = assignmentService.findAssignmentById(submissionDTO.getAssignmentId());
            
            // 检查截止日期
            if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
                return Result.failed("作业已过截止日期，无法提交");
            }
            
            // 创建或更新提交
            AssignmentSubmission submission = assignmentService.submitAssignment(
                    submissionDTO.getAssignmentId(),
                    student.getId(),
                    submissionDTO.getContent(),
                    submissionDTO.getFileUrl()
            );
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", submission.getId());
            result.put("assignmentId", submission.getAssignmentId());
            result.put("content", submission.getContent());
            result.put("fileUrl", submission.getFileUrl());
            result.put("submittedAt", submission.getSubmittedAt());
            
            return Result.success(result, "作业提交成功");
        } catch (Exception e) {
            return Result.failed("提交作业失败: " + e.getMessage());
        }
    }

    /**
     * 上传作业文件
     *
     * @param assignmentId 作业ID
     * @param file 文件
     * @return 文件URL
     */
    @PostMapping("/{assignmentId}/upload")
    public Result<Map<String, String>> uploadAssignmentFile(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file) {
        try {
            User student = getCurrentUser();
            
            // 检查作业是否存在
            Assignment assignment = assignmentService.findAssignmentById(assignmentId);
            
            // 检查截止日期
            if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
                return Result.failed("作业已过截止日期，无法上传文件");
            }
            
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.failed("请选择要上传的文件");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID().toString() + extension;
            
            // 确保目录存在
            String uploadDir = frontendPath + "/" + assignmentDir + "/" + assignmentId;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 保存文件
            Path filePath = Paths.get(uploadDir, filename);
            file.transferTo(filePath.toFile());
            
            // 生成文件URL
            String fileUrl = assignmentBaseUrl + "/" + assignmentId + "/" + filename;
            
            // 返回文件URL
            Map<String, String> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            
            return Result.success(result, "文件上传成功");
        } catch (IOException e) {
            return Result.failed("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.failed("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业提交历史
     *
     * @return 提交历史列表
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getSubmissionHistory() {
        try {
            User student = getCurrentUser();
            
            // 获取学生的所有作业提交
            List<AssignmentSubmission> submissions = assignmentService.getAllAssignmentSubmissions(student.getUsername());
            
            // 转换为前端需要的格式
            List<Map<String, Object>> result = submissions.stream().map(submission -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", submission.getId());
                item.put("assignmentId", submission.getAssignmentId());
                
                // 添加空值检查，防止空指针异常
                if (submission.getAssignment() != null) {
                    item.put("assignmentTitle", submission.getAssignment().getTitle());
                    item.put("dueDate", submission.getAssignment().getDueDate());
                } else {
                    // 如果关联的作业对象为空，通过assignmentId查找作业
                    try {
                        Assignment assignment = assignmentService.findAssignmentById(submission.getAssignmentId());
                        if (assignment != null) {
                            item.put("assignmentTitle", assignment.getTitle());
                            item.put("dueDate", assignment.getDueDate());
                        } else {
                            item.put("assignmentTitle", "未知作业");
                            item.put("dueDate", null);
                        }
                    } catch (Exception e) {
                        item.put("assignmentTitle", "未知作业");
                        item.put("dueDate", null);
                    }
                }
                
                item.put("content", submission.getContent());
                item.put("fileUrl", submission.getFileUrl());
                item.put("score", submission.getScore());
                item.put("feedback", submission.getFeedback());
                item.put("isGraded", submission.getIsGraded());
                item.put("submittedAt", submission.getSubmittedAt());
                item.put("gradedAt", submission.getGradedAt());
                
                return item;
            }).collect(Collectors.toList());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("获取提交历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业提交详情
     *
     * @param assignmentId 作业ID
     * @return 提交详情
     */
    @GetMapping("/submissions/{assignmentId}")
    public Result<Map<String, Object>> getSubmissionDetail(@PathVariable Long assignmentId) {
        try {
            User student = getCurrentUser();
            
            // 获取作业详情
            Assignment assignment = assignmentService.findAssignmentById(assignmentId);
            
            // 获取学生的提交情况
            AssignmentSubmission submission = assignmentService.getStudentSubmission(assignmentId, student.getId());
            
            if (submission == null) {
                return Result.failed("未找到该作业的提交记录");
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", submission.getId());
            result.put("assignmentId", submission.getAssignmentId());
            
            // 添加空值检查，防止空指针异常
            if (submission.getAssignment() != null) {
                result.put("assignmentTitle", submission.getAssignment().getTitle());
                result.put("dueDate", submission.getAssignment().getDueDate());
            } else {
                // 使用上面获取的作业信息
                result.put("assignmentTitle", assignment.getTitle());
                result.put("dueDate", assignment.getDueDate());
            }
            
            result.put("content", submission.getContent());
            result.put("fileUrl", submission.getFileUrl());
            result.put("score", submission.getScore());
            result.put("feedback", submission.getFeedback());
            result.put("isGraded", submission.getIsGraded());
            result.put("submittedAt", submission.getSubmittedAt());
            result.put("gradedAt", submission.getGradedAt());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("获取提交详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取待完成作业
     *
     * @return 待完成作业列表
     */
    @GetMapping("/pending")
    public Result<List<AssignmentDTO>> getPendingAssignments() {
        try {
            User student = getCurrentUser();
            
            // 获取学生的待完成作业
            List<Assignment> assignments = assignmentService.getPendingAssignments(student.getUsername());
            
            // 转换为DTO
            List<AssignmentDTO> assignmentDTOs = assignments.stream()
                    .map(AssignmentDTO::fromEntity)
                    .collect(Collectors.toList());
            
            return Result.success(assignmentDTOs);
        } catch (Exception e) {
            return Result.failed("获取待完成作业失败: " + e.getMessage());
        }
    }

    /**
     * 获取已完成作业
     *
     * @return 已完成作业列表
     */
    @GetMapping("/completed")
    public Result<List<Map<String, Object>>> getCompletedAssignments() {
        try {
            User student = getCurrentUser();
            
            // 获取学生的已完成作业
            List<AssignmentSubmission> submissions = assignmentService.getCompletedAssignments(student.getUsername());
            
            // 转换为前端需要的格式
            List<Map<String, Object>> result = submissions.stream().map(submission -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", submission.getId());
                item.put("assignmentId", submission.getAssignmentId());
                
                // 添加空值检查，防止空指针异常
                if (submission.getAssignment() != null) {
                    item.put("assignmentTitle", submission.getAssignment().getTitle());
                    item.put("dueDate", submission.getAssignment().getDueDate());
                } else {
                    // 如果关联的作业对象为空，通过assignmentId查找作业
                    try {
                        Assignment assignment = assignmentService.findAssignmentById(submission.getAssignmentId());
                        if (assignment != null) {
                            item.put("assignmentTitle", assignment.getTitle());
                            item.put("dueDate", assignment.getDueDate());
                        } else {
                            item.put("assignmentTitle", "未知作业");
                            item.put("dueDate", null);
                        }
                    } catch (Exception e) {
                        item.put("assignmentTitle", "未知作业");
                        item.put("dueDate", null);
                    }
                }
                
                item.put("content", submission.getContent());
                item.put("fileUrl", submission.getFileUrl());
                item.put("score", submission.getScore());
                item.put("feedback", submission.getFeedback());
                item.put("isGraded", submission.getIsGraded());
                item.put("submittedAt", submission.getSubmittedAt());
                item.put("gradedAt", submission.getGradedAt());
                
                return item;
            }).collect(Collectors.toList());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("获取已完成作业失败: " + e.getMessage());
        }
    }
}

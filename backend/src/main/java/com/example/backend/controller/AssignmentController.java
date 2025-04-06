package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.AssignmentDTO;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.AssignmentService;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 作业管理控制器
 * 提供作业相关的REST API接口，包括作业的创建、修改、删除和查询等功能
 * 教师可以创建、编辑和删除自己的作业
 * 学生可以查看所有可用的作业
 */
@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 创建新作业（仅教师）
     * 处理教师创建新作业的请求，验证身份并保存作业信息
     * 
     * @param assignmentDTO 包含作业信息的DTO对象
     * @return 创建成功返回作业信息，失败返回错误信息
     */
    @PostMapping
    public Result<AssignmentDTO> createAssignment(@RequestBody @Valid AssignmentDTO assignmentDTO) {
        try {
            // 检查权限
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User teacher = userService.findById(Long.valueOf(authentication.getName()));
            
            // 检查用户是否为教师
            if (teacher.getRole() != Role.TEACHER) {
                return Result.forbidden("只有教师可以创建作业");
            }

            Assignment assignment = assignmentService.createAssignment(assignmentDTO, teacher);
            return Result.success(AssignmentDTO.fromEntity(assignment), "作业创建成功");
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
    
    /**
     * 获取所有作业
     * 根据用户角色获取不同的作业列表：
     * - 教师：获取该教师发布的所有作业
     * - 学生：获取系统中的所有作业
     * 
     * @return 返回作业列表，发生异常则返回错误信息
     */
    @GetMapping
    public Result<List<AssignmentDTO>> getAllAssignments() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findById(Long.valueOf(authentication.getName()));
            
            List<Assignment> assignments = assignmentService.findAllAssignments();
            
            // 如果是教师，只返回该教师创建的作业
            if (user.getRole() == Role.TEACHER) {
                assignments = assignments.stream()
                        .filter(assignment -> assignment.getEmail().equals(user.getUsername()))
                        .collect(Collectors.toList());
            }
            
            List<AssignmentDTO> assignmentDTOs = assignments.stream()
                    .map(AssignmentDTO::fromEntity)
                    .collect(Collectors.toList());
            
            return Result.success(assignmentDTOs);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping("/user/{email}")
    public Result<List<AssignmentDTO>> getAssignmentsByEmail(@PathVariable String email) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByEmail(email);
            List<AssignmentDTO> dtos = assignments.stream()
                    .map(AssignmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return Result.success(dtos);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping("/stats/{email}")
    public Result<Map<String, Object>> getAssignmentStats(@PathVariable String email) {
        try {
            Map<String, Object> stats = assignmentService.getAssignmentStats(email);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<AssignmentDTO> getAssignmentById(@PathVariable Long id) {
        try {
            Assignment assignment = assignmentService.getAssignmentById(id);
            return Result.success(AssignmentDTO.fromEntity(assignment));
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<AssignmentDTO> updateAssignment(
            @PathVariable Long id,
            @RequestBody @Valid AssignmentDTO assignmentDTO) {
        try {
            // 检查权限
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User teacher = userService.findById(Long.valueOf(authentication.getName()));
            
            // 检查用户是否为教师
            if (teacher.getRole() != Role.TEACHER) {
                return Result.forbidden("只有教师可以修改作业");
            }
            
            Assignment assignment = assignmentDTO.toEntity();
            Assignment updatedAssignment = assignmentService.updateAssignment(id, assignment);
            return Result.success(AssignmentDTO.fromEntity(updatedAssignment));
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteAssignment(@PathVariable Long id) {
        try {
            // 检查权限
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User teacher = userService.findById(Long.valueOf(authentication.getName()));
            
            // 检查用户是否为教师
            if (teacher.getRole() != Role.TEACHER) {
                return Result.forbidden("只有教师可以删除作业");
            }
            
            assignmentService.deleteAssignment(id);
            return Result.success("作业已删除");
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
} 
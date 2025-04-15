package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.ExamService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员控制面板控制器
 * 提供管理员控制面板所需的统计数据和概览信息
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final UserService userService;
    private final CourseService courseService;
    private final ExamService examService;

    @Autowired
    public AdminDashboardController(UserService userService, CourseService courseService, ExamService examService) {
        this.userService = userService;
        this.courseService = courseService;
        this.examService = examService;
    }

    /**
     * 验证当前用户是否为管理员
     */
    private void validateAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findByUsername(userEmail);
        
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new SecurityException("非管理员账号，无权访问");
        }
    }

    /**
     * 获取管理员控制面板统计数据
     * 
     * @return 系统统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            validateAdminRole();
            
            Map<String, Object> stats = new HashMap<>();
            
            // 用户统计信息
            long totalUsers = userService.countAllUsers();
            long userGrowth = userService.calculateUserGrowthRate();
            
            // 课程统计信息
            long totalCourses = courseService.countAllCourses();
            long courseGrowth = courseService.calculateCourseGrowthRate();
            
            // 考试统计信息
            long totalExams = examService.countAllExams();
            long examGrowth = examService.calculateExamGrowthRate();
            
            // 待审核课程数
            long pendingApprovals = courseService.countPendingApprovalCourses();
            
            stats.put("totalUsers", totalUsers);
            stats.put("userGrowth", userGrowth);
            stats.put("totalCourses", totalCourses);
            stats.put("courseGrowth", courseGrowth);
            stats.put("totalExams", totalExams);
            stats.put("examGrowth", examGrowth);
            stats.put("pendingApprovals", pendingApprovals);
            
            return Result.success(stats);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近注册的用户
     * 
     * @param limit 限制返回数量
     * @return 最近注册的用户列表
     */
    @GetMapping("/recent-users")
    public Result<List<User>> getRecentUsers(@RequestParam(defaultValue = "5") int limit) {
        try {
            validateAdminRole();
            
            List<User> recentUsers = userService.findRecentUsers(limit);
            return Result.success(recentUsers);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取最近注册用户失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近添加的课程
     * 
     * @param limit 限制返回数量
     * @return 最近添加的课程列表
     */
    @GetMapping("/recent-courses")
    public Result<List<Map<String, Object>>> getRecentCourses(@RequestParam(defaultValue = "5") int limit) {
        try {
            validateAdminRole();
            
            List<Map<String, Object>> recentCourses = courseService.findRecentCourses(limit);
            return Result.success(recentCourses);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取最近添加课程失败: " + e.getMessage());
        }
    }
}

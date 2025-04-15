package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Course;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程审批控制器
 * 提供管理员审批课程的接口
 */
@RestController
@RequestMapping("/api/admin/course-approval")
public class CourseApprovalController {

    private final CourseService courseService;
    private final UserService userService;

    @Autowired
    public CourseApprovalController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
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
     * 获取所有待审批的课程
     * 
     * @return 待审批课程列表
     */
    @GetMapping("/pending")
    public Result<List<Course>> getPendingCourses() {
        try {
            validateAdminRole();
            
            List<Course> pendingCourses = courseService.getPendingApprovalCourses();
            return Result.success(pendingCourses);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取待审批课程失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程审批详情
     * 
     * @param courseId 课程ID
     * @return 课程详细信息
     */
    @GetMapping("/{courseId}")
    public Result<Map<String, Object>> getCourseApprovalDetail(@PathVariable Long courseId) {
        try {
            validateAdminRole();
            
            Map<String, Object> courseDetail = courseService.getCourseApprovalDetail(courseId);
            return Result.success(courseDetail);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程审批详情失败: " + e.getMessage());
        }
    }

    /**
     * 批准课程
     * 
     * @param courseId 课程ID
     * @param comments 审批备注
     * @return 审批结果
     */
    @PostMapping("/approve/{courseId}")
    public Result<String> approveCourse(@PathVariable Long courseId, @RequestBody(required = false) String comments) {
        try {
            validateAdminRole();
            
            courseService.approveCourse(courseId, comments);
            return Result.success("课程已批准");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批准课程失败: " + e.getMessage());
        }
    }

    /**
     * 拒绝课程
     * 
     * @param courseId 课程ID
     * @param reason 拒绝原因
     * @return 拒绝结果
     */
    @PostMapping("/reject/{courseId}")
    public Result<String> rejectCourse(@PathVariable Long courseId, @RequestBody String reason) {
        try {
            validateAdminRole();
            
            courseService.rejectCourse(courseId, reason);
            return Result.success("课程已拒绝");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("拒绝课程失败: " + e.getMessage());
        }
    }
}

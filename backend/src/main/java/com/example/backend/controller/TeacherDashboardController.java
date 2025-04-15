package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.AssignmentService;
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

/**
 * 教师控制面板控制器
 * 提供教师控制面板所需的统计数据和概览信息
 */
@RestController
@RequestMapping("/api/teacher/dashboard")
public class TeacherDashboardController {

    private final UserService userService;
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final ExamService examService;

    @Autowired
    public TeacherDashboardController(UserService userService, CourseService courseService,
                                     AssignmentService assignmentService, ExamService examService) {
        this.userService = userService;
        this.courseService = courseService;
        this.assignmentService = assignmentService;
        this.examService = examService;
    }

    /**
     * 获取当前登录的教师ID
     */
    private Long getCurrentTeacherId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findByUsername(userEmail);
        
        if (user == null || user.getRole() != Role.TEACHER) {
            throw new SecurityException("非教师账号，无权访问");
        }
        
        return user.getId();
    }

    /**
     * 获取教师控制面板统计数据
     * 
     * @return 教师相关统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Long teacherId = getCurrentTeacherId();
            
            Map<String, Object> stats = new HashMap<>();
            
            // 课程统计信息
            long totalCourses = courseService.countTeacherCourses(teacherId);
            int courseProgress = courseService.calculateCourseCompletionRate(teacherId);
            
            // 学生统计信息
            long totalStudents = courseService.countTeacherStudents(teacherId);
            int studentProgress = courseService.calculateStudentGrowthRate(teacherId);
            
            // 待批改作业数
            long pendingAssignments = assignmentService.countPendingAssignments(teacherId);
            
            // 待批改考试数
            long pendingExams = examService.countPendingExams(teacherId);
            
            stats.put("totalCourses", totalCourses);
            stats.put("courseProgress", courseProgress);
            stats.put("totalStudents", totalStudents);
            stats.put("studentProgress", studentProgress);
            stats.put("pendingAssignments", pendingAssignments);
            stats.put("pendingExams", pendingExams);
            
            return Result.success(stats);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师最近课程活动
     * 
     * @param limit 限制返回数量
     * @return 最近课程活动列表
     */
    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentCourseActivities(@RequestParam(defaultValue = "5") int limit) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            List<Map<String, Object>> activities = courseService.getRecentCourseActivities(teacherId, limit);
            return Result.success(activities);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取最近课程活动失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师待办事项
     * 
     * @return 待办事项列表
     */
    @GetMapping("/todos")
    public Result<List<Map<String, Object>>> getTodos() {
        try {
            Long teacherId = getCurrentTeacherId();
            
            List<Map<String, Object>> todos = userService.getTeacherTodos(teacherId);
            return Result.success(todos);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 添加待办事项
     * 
     * @param todoData 待办事项数据
     * @return 添加结果
     */
    @PostMapping("/todos")
    public Result<Map<String, Object>> addTodo(@RequestBody Map<String, Object> todoData) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            Map<String, Object> todo = userService.addTeacherTodo(teacherId, todoData);
            return Result.success(todo, "添加待办事项成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("添加待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 更新待办事项
     * 
     * @param todoId 待办事项ID
     * @param todoData 待办事项数据
     * @return 更新结果
     */
    @PutMapping("/todos/{todoId}")
    public Result<Map<String, Object>> updateTodo(@PathVariable Long todoId, @RequestBody Map<String, Object> todoData) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            Map<String, Object> todo = userService.updateTeacherTodo(teacherId, todoId, todoData);
            return Result.success(todo, "更新待办事项成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 删除待办事项
     * 
     * @param todoId 待办事项ID
     * @return 删除结果
     */
    @DeleteMapping("/todos/{todoId}")
    public Result<String> deleteTodo(@PathVariable Long todoId) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            userService.deleteTeacherTodo(teacherId, todoId);
            return Result.success("删除待办事项成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除待办事项失败: " + e.getMessage());
        }
    }
}

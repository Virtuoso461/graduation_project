package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.UserDTO;
import com.example.backend.entity.Course;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员控制器
 * 提供管理员相关的功能，包括用户管理、课程管理和系统设置
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, CourseService courseService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.courseService = courseService;
        this.passwordEncoder = passwordEncoder;
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
     * 获取所有用户
     * 
     * @return 所有用户的列表
     */
    @GetMapping("/all-users")
    public Result<List<UserDTO>> getAllUsers() {
        try {
            validateAdminRole();
            
            List<User> users = userService.findAllUsers();
            List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            return Result.success(userDTOs);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/all-users/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        try {
            validateAdminRole();
            
            User user = userService.findById(id);
            UserDTO userDTO = convertToDTO(user);
            
            return Result.success(userDTO);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新用户
     * 
     * @param userDTO 用户信息
     * @return 创建结果
     */
    @PostMapping("/all-users")
    public Result<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        try {
            validateAdminRole();
            
            User user = convertToEntity(userDTO);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            User createdUser = userService.saveUser(user);
            UserDTO createdUserDTO = convertToDTO(createdUser);
            
            return Result.success(createdUserDTO, "用户创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param userDTO 用户信息
     * @return 更新结果
     */
    @PutMapping("/all-users/{id}")
    public Result<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            validateAdminRole();
            
            User existingUser = userService.findById(id);
            User updatedUser = updateUserFields(existingUser, userDTO);
            
            User savedUser = userService.saveUser(updatedUser);
            UserDTO savedUserDTO = convertToDTO(savedUser);
            
            return Result.success(savedUserDTO, "用户信息更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/all-users/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            validateAdminRole();
            
            userService.deleteUser(id);
            return Result.success("用户删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统角色列表
     * 
     * @return 角色列表
     */
    @GetMapping("/roles")
    public Result<Role[]> getRoles() {
        try {
            validateAdminRole();
            return Result.success(Role.values());
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有课程
     * 
     * @return 所有课程的列表
     */
    @GetMapping("/all-courses")
    public Result<List<Course>> getAllCourses() {
        try {
            validateAdminRole();
            
            List<Course> courses = courseService.getCourses(null);
            return Result.success(courses);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 审核课程
     * 
     * @param id 课程ID
     * @param status 审核状态
     * @return 审核结果
     */
    @PutMapping("/courses/{id}/approve")
    public Result<Course> approveCourse(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            validateAdminRole();
            
            Course course = courseService.getCourseById(id);
            course.setApproved(approved);
            
            Course updatedCourse = courseService.updateCourse(course);
            return Result.success(updatedCourse, approved ? "课程审核通过" : "课程被拒绝");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("课程审核失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统统计信息
     * 
     * @return 系统统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getSystemStats() {
        try {
            validateAdminRole();
            
            Map<String, Object> stats = new HashMap<>();
            
            // 用户统计
            List<User> allUsers = userService.findAllUsers();
            long studentCount = allUsers.stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .count();
            long teacherCount = allUsers.stream()
                .filter(user -> user.getRole() == Role.TEACHER)
                .count();
            long adminCount = allUsers.stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .count();
            
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("total", allUsers.size());
            userStats.put("students", studentCount);
            userStats.put("teachers", teacherCount);
            userStats.put("admins", adminCount);
            
            // 课程统计
            List<Course> allCourses = courseService.getCourses(null);
            long approvedCourses = allCourses.stream()
                .filter(Course::isApproved)
                .count();
            long pendingCourses = allCourses.size() - approvedCourses;
            
            Map<String, Object> courseStats = new HashMap<>();
            courseStats.put("total", allCourses.size());
            courseStats.put("approved", approvedCourses);
            courseStats.put("pending", pendingCourses);
            
            stats.put("users", userStats);
            stats.put("courses", courseStats);
            
            return Result.success(stats);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取系统统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统设置
     * 
     * @return 系统设置
     */
    @GetMapping("/settings")
    public Result<Map<String, Object>> getSystemSettings() {
        try {
            validateAdminRole();
            
            Map<String, Object> settings = new HashMap<>();
            // 这里是示例设置，实际项目中可能需要从数据库或配置文件读取
            settings.put("siteName", "在线学习系统");
            settings.put("allowRegistration", true);
            settings.put("allowPasswordReset", true);
            settings.put("maxUploadSize", 10); // MB
            settings.put("maintenance", false);
            
            return Result.success(settings);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 更新系统设置
     * 
     * @param settings 系统设置
     * @return 更新结果
     */
    @PutMapping("/settings")
    public Result<Map<String, Object>> updateSystemSettings(@RequestBody Map<String, Object> settings) {
        try {
            validateAdminRole();
            
            // 这里是示例，实际项目中需要将设置保存到数据库或配置文件
            return Result.success(settings, "系统设置更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 将User实体转换为UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        // 不传输密码
        return dto;
    }

    /**
     * 将UserDTO转换为User实体
     */
    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }

    /**
     * 使用DTO中的值更新User实体
     */
    private User updateUserFields(User user, UserDTO dto) {
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        return user;
    }
} 
package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.ProfileService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.ProfileDTO;
import com.example.backend.vo.ProfileVO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 管理员用户管理控制器
 * 提供用户管理功能
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final UserService userService;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminUsersController(UserService userService, ProfileService profileService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.profileService = profileService;
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
     * 获取用户列表
     *
     * @param keyword 搜索关键词
     * @param role 角色筛选
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    @GetMapping()
    public Result<Map<String, Object>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            validateAdminRole();

            Map<String, Object> usersData = userService.findUsers(keyword, role, page, size);
            return Result.success(usersData);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建新用户
     *
     * @param userData 用户数据
     * @return 创建结果
     */
    @PostMapping()
    public Result<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        try {
            validateAdminRole();

            // 验证必要字段
            if (!userData.containsKey("username") || !userData.containsKey("password") || !userData.containsKey("role")) {
                return Result.validateFailed("用户名、密码和角色不能为空");
            }

            String username = (String) userData.get("username");
            String password = (String) userData.get("password");
            String roleStr = (String) userData.get("role");

            // 验证用户名是否已存在
            if (userService.findByUsername(username) != null) {
                return Result.validateFailed("用户名已存在");
            }

            // 创建用户
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));

            // 设置角色
            try {
                Role role = Role.valueOf(roleStr);
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                return Result.validateFailed("无效的角色");
            }

            User createdUser = userService.saveUser(user);

            // 初始化用户资料
            profileService.initProfile(username);

            // 如果提供了资料信息，更新资料
            if (userData.containsKey("profile")) {
                Map<String, Object> profileData = (Map<String, Object>) userData.get("profile");
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setEmail(username);

                if (profileData.containsKey("username")) {
                    profileDTO.setUsername((String) profileData.get("username"));
                }

                if (profileData.containsKey("name")) {
                    profileDTO.setName((String) profileData.get("name"));
                }

                if (profileData.containsKey("phoneNumber")) {
                    profileDTO.setPhoneNumber((String) profileData.get("phoneNumber"));
                }

                if (profileData.containsKey("gender")) {
                    profileDTO.setGender((String) profileData.get("gender"));
                }

                if (profileData.containsKey("birthday")) {
                    profileDTO.setBirthday((String) profileData.get("birthday"));
                }

                if (profileData.containsKey("bio")) {
                    profileDTO.setBio((String) profileData.get("bio"));
                }

                if (profileData.containsKey("avatar")) {
                    profileDTO.setAvatar((String) profileData.get("avatar"));
                }

                profileService.saveOrUpdateProfile(profileDTO);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("id", createdUser.getId());
            result.put("username", createdUser.getUsername());
            result.put("role", createdUser.getRole());
            result.put("createdAt", createdUser.getCreateTime());

            return Result.success(result, "用户创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("/{userId}")
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long userId) {
        try {
            validateAdminRole();

            User user = userService.findById(userId);
            if (user == null) {
                return Result.notFound("用户不存在");
            }

            Map<String, Object> userDetail = new HashMap<>();
            userDetail.put("id", user.getId());
            userDetail.put("username", user.getUsername());
            userDetail.put("role", user.getRole());
            userDetail.put("enabled", user.getEnabled());
            userDetail.put("createdAt", user.getCreateTime());
            userDetail.put("lastLoginAt", user.getLastLoginTime());

            // 获取用户资料
            ProfileVO profileVO = profileService.getProfileByEmail(user.getUsername());
            if (profileVO != null) {
                Map<String, Object> profileMap = new HashMap<>();
                profileMap.put("email", profileVO.getEmail());
                profileMap.put("username", profileVO.getUsername());
                profileMap.put("name", profileVO.getName());
                profileMap.put("phoneNumber", profileVO.getPhoneNumber());
                profileMap.put("gender", profileVO.getGender());
                profileMap.put("birthday", profileVO.getBirthday());
                profileMap.put("bio", profileVO.getBio());
                profileMap.put("avatar", profileVO.getAvatar());
                userDetail.put("profile", profileMap);
            }

            return Result.success(userDetail);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param userData 用户数据
     * @return 更新结果
     */
    @PutMapping("/{userId}")
    public Result<Map<String, Object>> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> userData) {
        try {
            validateAdminRole();

            User user = userService.findById(userId);
            if (user == null) {
                return Result.notFound("用户不存在");
            }

            // 更新角色
            if (userData.containsKey("role")) {
                try {
                    Role role = Role.valueOf((String) userData.get("role"));
                    user.setRole(role);
                } catch (IllegalArgumentException e) {
                    return Result.validateFailed("无效的角色");
                }
            }

            // 更新密码
            if (userData.containsKey("password")) {
                user.setPassword(passwordEncoder.encode((String) userData.get("password")));
            }

            // 更新启用状态
            if (userData.containsKey("enabled")) {
                user.setEnabled((Boolean) userData.get("enabled"));
            }

            User updatedUser = userService.saveUser(user);

            // 更新用户资料
            if (userData.containsKey("profile")) {
                Map<String, Object> profileData = (Map<String, Object>) userData.get("profile");
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setEmail(user.getUsername());

                if (profileData.containsKey("username")) {
                    profileDTO.setUsername((String) profileData.get("username"));
                }

                if (profileData.containsKey("name")) {
                    profileDTO.setName((String) profileData.get("name"));
                }

                if (profileData.containsKey("phoneNumber")) {
                    profileDTO.setPhoneNumber((String) profileData.get("phoneNumber"));
                }

                if (profileData.containsKey("gender")) {
                    profileDTO.setGender((String) profileData.get("gender"));
                }

                if (profileData.containsKey("birthday")) {
                    profileDTO.setBirthday((String) profileData.get("birthday"));
                }

                if (profileData.containsKey("bio")) {
                    profileDTO.setBio((String) profileData.get("bio"));
                }

                if (profileData.containsKey("avatar")) {
                    profileDTO.setAvatar((String) profileData.get("avatar"));
                }

                profileService.saveOrUpdateProfile(profileDTO);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("id", updatedUser.getId());
            result.put("username", updatedUser.getUsername());
            result.put("role", updatedUser.getRole());
            result.put("enabled", updatedUser.getEnabled());
            result.put("updatedAt", new Date());

            return Result.success(result, "用户信息更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")
    public Result<String> deleteUser(@PathVariable Long userId) {
        try {
            validateAdminRole();

            User user = userService.findById(userId);
            if (user == null) {
                return Result.notFound("用户不存在");
            }

            // 不允许删除管理员账号
            if (user.getRole() == Role.ADMIN) {
                return Result.forbidden("不能删除管理员账号");
            }

            userService.deleteUser(userId);
            return Result.success("用户删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 重置结果
     */
    @PostMapping("/{userId}/reset-password")
    public Result<String> resetPassword(@PathVariable Long userId) {
        try {
            validateAdminRole();

            User user = userService.findById(userId);
            if (user == null) {
                return Result.notFound("用户不存在");
            }

            // 生成随机密码
            String newPassword = generateRandomPassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);

            return Result.success("密码重置成功，新密码为: " + newPassword);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态数据
     * @return 更新结果
     */
    @PutMapping("/{userId}/status")
    public Result<String> updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Boolean> status) {
        try {
            validateAdminRole();

            User user = userService.findById(userId);
            if (user == null) {
                return Result.notFound("用户不存在");
            }

            // 不允许禁用管理员账号
            if (user.getRole() == Role.ADMIN && !status.get("enabled")) {
                return Result.forbidden("不能禁用管理员账号");
            }

            user.setEnabled(status.get("enabled"));
            userService.saveUser(user);

            return Result.success(status.get("enabled") ? "用户已启用" : "用户已禁用");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生列表
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 学生列表
     */
    @GetMapping("/students")
    public Result<Map<String, Object>> getStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            validateAdminRole();

            Map<String, Object> studentsData = userService.findUsers(keyword, Role.STUDENT.name(), page, size);
            return Result.success(studentsData);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师列表
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 教师列表
     */
    @GetMapping("/teachers")
    public Result<Map<String, Object>> getTeachers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            validateAdminRole();

            Map<String, Object> teachersData = userService.findUsers(keyword, Role.TEACHER.name(), page, size);
            return Result.success(teachersData);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取教师列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取管理员列表
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 管理员列表
     */
    @GetMapping("/admins")
    public Result<Map<String, Object>> getAdmins(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            validateAdminRole();

            Map<String, Object> adminsData = userService.findUsers(keyword, Role.ADMIN.name(), page, size);
            return Result.success(adminsData);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取管理员列表失败: " + e.getMessage());
        }
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

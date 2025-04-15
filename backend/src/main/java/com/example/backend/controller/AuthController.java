package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.PasswordResetDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.StudentProfileService;
import com.example.backend.service.TeacherProfileService;
import com.example.backend.service.UserService;
import com.example.backend.util.JwtUtil;
import com.example.backend.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用认证控制器
 * 提供登录、登出、刷新令牌、密码重置等功能
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final StudentProfileService studentProfileService;
    private final TeacherProfileService teacherProfileService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                         JwtUtil jwtUtil, StudentProfileService studentProfileService,
                         TeacherProfileService teacherProfileService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.studentProfileService = studentProfileService;
        this.teacherProfileService = teacherProfileService;
    }

    /**
     * 用户登录
     * 统一的登录接口，根据角色进行不同的处理
     *
     * @param loginDTO 登录信息DTO
     * @return 登录成功返回用户信息和token，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            // 认证成功，获取用户信息
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());

            // 如果在登录时指定了角色，验证用户角色是否匹配
            if (loginDTO.getRole() != null && user.getRole() != loginDTO.getRole()) {
                String roleMessage = "";
                switch (loginDTO.getRole()) {
                    case STUDENT:
                        roleMessage = "非学生账号，无法登录";
                        break;
                    case TEACHER:
                        roleMessage = "非教师账号，无法登录";
                        break;
                    case ADMIN:
                        roleMessage = "非管理员账号，无法登录";
                        break;
                }
                return Result.forbidden(roleMessage);
            }

            // 根据用户角色进行不同的处理
            if (user.getRole() == Role.STUDENT) {
                // 学生登录成功后初始化个人资料（如果不存在）
                studentProfileService.initProfile(user.getUsername());
            } else if (user.getRole() == Role.TEACHER) {
                // 教师登录成功后初始化个人资料（如果不存在）
                teacherProfileService.initProfile(user.getUsername());
            }

            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", new LoginVO(
                    user.getId(),
                    user.getUsername(),
                    user.getUsername(), // 使用username作为邮箱
                    user.getId(), // 使用id代替number
                    user.getRole()
            ));

            return Result.success(result, "登录成功");
        } catch (BadCredentialsException e) {
            return Result.unauthorized("用户名或密码错误");
        } catch (Exception e) {
            return Result.failed("登录失败: " + e.getMessage());
        }
    }

    /**
     * 学生注册
     *
     * @param registerDTO 注册信息DTO
     * @return 注册结果
     */
    @PostMapping("/student/register")
    public Result<String> studentRegister(@RequestBody RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(registerDTO.getUsername())) {
            return Result.failed("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRole(Role.STUDENT);
        user.setEnabled(true);

        // 保存用户
        userService.save(user);

        // 初始化学生个人资料
        studentProfileService.initProfile(registerDTO.getUsername());

        return Result.success(null, "注册成功");
    }

    /**
     * 教师注册
     *
     * @param registerDTO 注册信息DTO
     * @return 注册结果
     */
    @PostMapping("/teacher/register")
    public Result<String> teacherRegister(@RequestBody RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(registerDTO.getUsername())) {
            return Result.failed("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRole(Role.TEACHER);
        user.setEnabled(true);

        // 保存用户
        userService.save(user);

        // 初始化教师个人资料
        teacherProfileService.initProfile(registerDTO.getUsername());

        return Result.success(null, "注册成功");
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // JWT是无状态的，客户端只需要删除令牌即可
        // 这里可以添加令牌黑名单等逻辑，但为简化实现，暂不处理
        return Result.success(null, "登出成功");
    }

    /**
     * 刷新令牌
     *
     * @param request 请求，包含旧令牌
     * @return 新令牌
     */
    @PostMapping("/refresh-token")
    public Result<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isEmpty()) {
            return Result.failed("令牌不能为空");
        }

        try {
            // 从令牌中提取用户名和角色
            String username = jwtUtil.extractUsername(token);
            Role role = jwtUtil.extractRole(token);

            // 生成新令牌
            String newToken = jwtUtil.generateToken(username, role);

            Map<String, String> result = new HashMap<>();
            result.put("token", newToken);

            return Result.success(result, "令牌刷新成功");
        } catch (Exception e) {
            return Result.unauthorized("无效的令牌");
        }
    }

    /**
     * 密码重置请求
     *
     * @param request 请求，包含邮箱
     * @return 发送结果
     */
    @PostMapping("/password-reset")
    public Result<String> passwordResetRequest(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return Result.failed("邮箱不能为空");
        }

        String result = userService.generatePasswordResetCode(email);
        if (result.startsWith("验证码已发送")) {
            return Result.success(null, result);
        } else {
            return Result.failed(result);
        }
    }

    /**
     * 确认密码重置
     *
     * @param passwordResetDTO 密码重置DTO
     * @return 重置结果
     */
    @PostMapping("/password-reset/confirm")
    public Result<String> passwordResetConfirm(@RequestBody PasswordResetDTO passwordResetDTO) {
        String result = userService.resetPassword(
                passwordResetDTO.getEmail(),
                passwordResetDTO.getNewPassword(),
                passwordResetDTO.getVerificationCode()
        );

        if (result.equals("密码重置成功")) {
            return Result.success(null, result);
        } else {
            return Result.failed(result);
        }
    }
}

package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.LoginDTO;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.ProfileService;
import com.example.backend.service.UserService;
import com.example.backend.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 学生账号控制器
 * 提供学生登录和注册功能
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final ProfileService profileService;

    @Autowired
    public StudentController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    /**
     * 学生登录
     * 
     * @param loginDTO 登录信息DTO
     * @return 登录成功返回用户信息和token，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        Optional<LoginVO> loginResult = userService.login(loginDTO);
        
        if (loginResult.isEmpty()) {
            return Result.unauthorized("用户名或密码错误");
        }
        
        // 验证角色
        LoginVO loginVO = loginResult.get();
        if (loginVO.getRole() != Role.STUDENT) {
            return Result.forbidden("非学生账号，无法登录");
        }
        
        // 登录成功后初始化个人资料（如果不存在）
        // 用户名在当前系统中是邮箱
        profileService.initProfile(loginVO.getUsername());
        
        return Result.success(loginVO, "登录成功");
    }

    /**
     * 学生注册
     * 
     * @param user 用户信息
     * @return 注册成功返回成功信息，失败返回错误信息
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        try {
            String register = userService.registerStudent(user);
            return Result.success(register);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed("注册失败: " + e.getMessage());
        }
    }
}

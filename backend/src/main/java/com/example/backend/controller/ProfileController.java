package com.example.backend.controller;

import com.example.backend.dto.ProfileDTO;
import com.example.backend.service.ProfileService;
import com.example.backend.vo.ProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    private final ProfileService profileService;
    
    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
    
    /**
     * 获取个人资料
     * @param email 邮箱（从cookie中获取）
     * @return 个人资料视图对象
     */
    @GetMapping
    public ResponseEntity<ProfileVO> getProfile(@RequestParam String email) {
        ProfileVO profileVO = profileService.getProfileByEmail(email);
        return ResponseEntity.ok(profileVO);
    }
    
    /**
     * 更新个人资料
     * @param profileDTO 个人资料数据传输对象
     * @return 更新后的个人资料视图对象
     */
    @PostMapping
    public ResponseEntity<ProfileVO> updateProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        ProfileVO updatedProfile = profileService.saveOrUpdateProfile(profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }
    
    /**
     * 初始化个人资料（仅在用户首次登录时调用）
     * @param email 邮箱
     * @return 初始化的个人资料视图对象
     */
    @PostMapping("/init")
    public ResponseEntity<ProfileVO> initProfile(@RequestParam String email) {
        if (profileService.existsByEmail(email)) {
            // 如果个人资料已存在，返回现有个人资料
            return ResponseEntity.ok(profileService.getProfileByEmail(email));
        } else {
            // 否则初始化个人资料
            ProfileVO profileVO = profileService.initProfile(email);
            return new ResponseEntity<>(profileVO, HttpStatus.CREATED);
        }
    }
} 
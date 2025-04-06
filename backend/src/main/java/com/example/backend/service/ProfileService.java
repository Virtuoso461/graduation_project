package com.example.backend.service;

import com.example.backend.dto.ProfileDTO;
import com.example.backend.vo.ProfileVO;

public interface ProfileService {
    
    /**
     * 根据邮箱获取个人资料
     * @param email 邮箱
     * @return 个人资料视图对象
     */
    ProfileVO getProfileByEmail(String email);
    
    /**
     * 创建或更新个人资料
     * @param profileDTO 个人资料数据传输对象
     * @return 更新后的个人资料视图对象
     */
    ProfileVO saveOrUpdateProfile(ProfileDTO profileDTO);
    
    /**
     * 检查邮箱是否已存在个人资料
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 初始化个人资料（仅包含邮箱）
     * @param email 邮箱
     * @return 初始化的个人资料视图对象
     */
    ProfileVO initProfile(String email);
} 
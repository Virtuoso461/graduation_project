package com.example.backend.service.impl;

import com.example.backend.dto.ProfileDTO;
import com.example.backend.entity.Profile;
import com.example.backend.repository.ProfileRepository;
import com.example.backend.service.ProfileService;
import com.example.backend.vo.ProfileVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {
    
    private final ProfileRepository profileRepository;
    
    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    
    @Override
    public ProfileVO getProfileByEmail(String email) {
        Optional<Profile> profileOpt = profileRepository.findByEmail(email);
        
        // 如果找到个人资料，返回完整信息；否则只返回邮箱
        if (profileOpt.isPresent()) {
            Profile profile = profileOpt.get();
            return convertToVO(profile);
        } else {
            return new ProfileVO(email);
        }
    }
    
    @Override
    @Transactional
    public ProfileVO saveOrUpdateProfile(ProfileDTO profileDTO) {
        if (profileDTO.getEmail() == null || profileDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        // 查找是否已存在该邮箱的个人资料
        Optional<Profile> existingProfileOpt = profileRepository.findByEmail(profileDTO.getEmail());
        
        Profile profile;
        if (existingProfileOpt.isPresent()) {
            // 更新现有个人资料
            profile = existingProfileOpt.get();
        } else {
            // 创建新的个人资料
            profile = new Profile();
            profile.setEmail(profileDTO.getEmail());
        }
        
        // 更新个人资料字段
        if (profileDTO.getUsername() != null) {
            profile.setUsername(profileDTO.getUsername());
        }
        if (profileDTO.getName() != null) {
            profile.setName(profileDTO.getName());
        }
        if (profileDTO.getPhoneNumber() != null) {
            profile.setPhoneNumber(profileDTO.getPhoneNumber());
        }
        if (profileDTO.getGender() != null) {
            profile.setGender(profileDTO.getGender());
        }
        if (profileDTO.getBirthday() != null && !profileDTO.getBirthday().trim().isEmpty()) {
            try {
                // 处理ISO 8601格式的日期
                String dateStr = profileDTO.getBirthday();
                // 如果日期包含T，说明是ISO格式，需要提取日期部分
                if (dateStr.contains("T")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf("T"));
                }
                profile.setBirthday(LocalDate.parse(dateStr));
            } catch (Exception e) {
                // 如果日期格式不正确，记录错误但不影响其他字段的保存
                System.err.println("日期格式转换错误: " + e.getMessage());
            }
        }
        if (profileDTO.getBio() != null) {
            profile.setBio(profileDTO.getBio());
        }
        if (profileDTO.getAvatar() != null) {
            profile.setAvatar(profileDTO.getAvatar());
        }
        
        // 保存到数据库
        profile = profileRepository.save(profile);
        
        // 转换为VO并返回
        return convertToVO(profile);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional
    public ProfileVO initProfile(String email) {
        // 检查是否已存在个人资料
        if (profileRepository.existsByEmail(email)) {
            return getProfileByEmail(email);
        }
        
        // 创建只包含邮箱的个人资料
        Profile profile = new Profile(email);
        profile = profileRepository.save(profile);
        
        // 转换为VO并返回
        return convertToVO(profile);
    }
    
    /**
     * 将实体对象转换为视图对象
     */
    private ProfileVO convertToVO(Profile profile) {
        ProfileVO vo = new ProfileVO();
        vo.setEmail(profile.getEmail());
        vo.setUsername(profile.getUsername());
        vo.setName(profile.getName());
        vo.setPhoneNumber(profile.getPhoneNumber());
        vo.setGender(profile.getGender());
        vo.setBirthday(profile.getBirthday() != null ? profile.getBirthday().toString() : null);
        vo.setBio(profile.getBio());
        vo.setAvatar(profile.getAvatar());
        return vo;
    }
} 
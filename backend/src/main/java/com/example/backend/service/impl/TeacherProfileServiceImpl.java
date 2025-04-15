package com.example.backend.service.impl;

import com.example.backend.entity.Profile;
import com.example.backend.repository.ProfileRepository;
import com.example.backend.service.TeacherProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 教师个人资料服务实现类
 */
@Service
public class TeacherProfileServiceImpl implements TeacherProfileService {

    private final ProfileRepository profileRepository;

    @Autowired
    public TeacherProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile initProfile(String email) {
        // 检查是否已存在
        Optional<Profile> existingProfile = profileRepository.findById(email);
        if (existingProfile.isPresent()) {
            return existingProfile.get();
        }

        // 创建新的个人资料
        Profile profile = new Profile();
        profile.setEmail(email);
        profile.setUsername(email);
        profile.setName("教师");
        profile.setGender("未设置");
        profile.setBirthday(LocalDate.now().minusYears(30)); // 默认30岁
        profile.setBio("这个人很懒，什么都没有留下...");
        profile.setAvatar("/images/avatars/default.png");

        return profileRepository.save(profile);
    }

    @Override
    public Profile getProfile(String email) {
        return profileRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("个人资料不存在"));
    }

    @Override
    public Profile updateProfile(Profile profile) {
        // 检查是否存在
        if (!profileRepository.existsById(profile.getEmail())) {
            throw new RuntimeException("个人资料不存在");
        }

        return profileRepository.save(profile);
    }
}

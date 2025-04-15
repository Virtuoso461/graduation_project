package com.example.backend.service;

import com.example.backend.entity.Profile;

/**
 * 教师个人资料服务接口
 */
public interface TeacherProfileService {

    /**
     * 初始化教师个人资料
     *
     * @param email 教师邮箱
     * @return 初始化的个人资料
     */
    Profile initProfile(String email);

    /**
     * 获取教师个人资料
     *
     * @param email 教师邮箱
     * @return 个人资料
     */
    Profile getProfile(String email);

    /**
     * 更新教师个人资料
     *
     * @param profile 个人资料
     * @return 更新后的个人资料
     */
    Profile updateProfile(Profile profile);
}

package com.example.backend.service;

import com.example.backend.dto.LoginDTO;
import com.example.backend.entity.User;
import com.example.backend.vo.LoginVO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<LoginVO> login(LoginDTO loginDTO);

    String register(User user);
    
    String registerStudent(User user);
    
    String registerTeacher(User user);
    
    /**
     * 保存或更新用户
     * 
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User saveUser(User user);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 获取所有用户
     * 
     * @return 所有用户列表
     */
    List<User> findAllUsers();
    
    /**
     * 根据用户ID查找用户
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Long id);

    /**
     * 根据用户编号（学号/工号）查找用户 - 为了兼容性保留，内部实现转为使用ID查询
     * 
     * @param number 用户编号
     * @return 用户对象
     * @deprecated 请使用 findById 方法替代
     */
    @Deprecated
    User findByNumber(Long number);
    
    /**
     * 根据用户名(邮箱)查找用户
     * 
     * @param username 用户名(邮箱)
     * @return 用户对象，如果找不到则返回null
     */
    User findByUsername(String username);
    
    /**
     * 生成密码重置验证码并发送到用户邮箱
     * 
     * @param email 用户邮箱
     * @return 生成结果消息
     */
    String generatePasswordResetCode(String email);
    
    /**
     * 验证密码重置验证码
     * 
     * @param email 用户邮箱
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyPasswordResetCode(String email, String code);
    
    /**
     * 重置用户密码
     * 
     * @param email 用户邮箱
     * @param newPassword 新密码
     * @param code 验证码
     * @return 重置结果消息
     */
    String resetPassword(String email, String newPassword, String code);
}

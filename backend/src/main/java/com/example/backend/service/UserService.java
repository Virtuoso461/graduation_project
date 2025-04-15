package com.example.backend.service;

import com.example.backend.dto.LoginDTO;
import com.example.backend.entity.User;
import com.example.backend.vo.LoginVO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Optional<LoginVO> login(LoginDTO loginDTO);

    String register(User user);

    String registerStudent(User user);

    String registerTeacher(User user);

    /**
     * 检查当前用户是否为管理员
     *
     * @return 是否为管理员
     */
    boolean isCurrentUserAdmin();

    /**
     * 保存或更新用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User saveUser(User user);

    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User save(User user);

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
     * 检查用户名是否存在
     *
     * @param username 用户名(邮箱)
     * @return 是否存在
     */
    boolean existsByUsername(String username);

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

    /**
     * 统计所有用户数量
     *
     * @return 用户总数
     */
    long countAllUsers();

    /**
     * 计算用户增长率
     *
     * @return 增长率百分比
     */
    long calculateUserGrowthRate();

    /**
     * 获取最近注册的用户
     *
     * @param limit 限制返回数量
     * @return 最近注册的用户列表
     */
    List<User> findRecentUsers(int limit);

    /**
     * 根据关键词和角色查找用户（带分页）
     *
     * @param keyword 关键词
     * @param role 角色
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的用户列表
     */
    Map<String, Object> findUsers(String keyword, String role, int page, int size);

    /**
     * 获取用户统计数据
     *
     * @return 统计数据
     */
    Map<String, Object> getUserStatistics();

    /**
     * 批量启用用户
     *
     * @param userIds 用户ID列表
     */
    void batchEnableUsers(List<Long> userIds);

    /**
     * 批量禁用用户
     *
     * @param userIds 用户ID列表
     */
    void batchDisableUsers(List<Long> userIds);

    /**
     * 导出用户数据
     *
     * @param format 导出格式（csv或excel）
     * @return 导出文件的URL
     */
    String exportUsers(String format);

    /**
     * 导入用户数据
     *
     * @param fileUrl 文件URL
     * @param format 文件格式
     * @return 导入结果
     */
    Map<String, Object> importUsers(String fileUrl, String format);

    /**
     * 统计总用户数
     * @return 用户总数
     */
    long countUsers();

    /**
     * 统计活跃用户数
     * @return 活跃用户数
     */
    long countActiveUsers();

    /**
     * 统计管理员用户数
     * @return 管理员用户数
     */
    long countAdminUsers();

    /**
     * 统计教师用户数
     * @return 教师用户数
     */
    long countTeacherUsers();

    /**
     * 统计学生用户数
     * @return 学生用户数
     */
    long countStudentUsers();

    /**
     * 获取教师待办事项
     * 
     * @param teacherId 教师ID
     * @return 待办事项列表
     */
    List<Map<String, Object>> getTeacherTodos(Long teacherId);

    /**
     * 添加教师待办事项
     * 
     * @param teacherId 教师ID
     * @param todoData 待办事项数据
     * @return 添加的待办事项
     */
    Map<String, Object> addTeacherTodo(Long teacherId, Map<String, Object> todoData);

    /**
     * 更新教师待办事项
     * 
     * @param teacherId 教师ID
     * @param todoId 待办事项ID
     * @param todoData 待办事项数据
     * @return 更新后的待办事项
     */
    Map<String, Object> updateTeacherTodo(Long teacherId, Long todoId, Map<String, Object> todoData);

    /**
     * 删除教师待办事项
     * 
     * @param teacherId 教师ID
     * @param todoId 待办事项ID
     */
    void deleteTeacherTodo(Long teacherId, Long todoId);

    /**
     * 修改用户密码
     * 
     * @param email 用户邮箱
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(String email, String oldPassword, String newPassword);
}

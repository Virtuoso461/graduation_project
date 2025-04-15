package com.example.backend.service.impl;

import com.example.backend.dto.LoginDTO;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.EmailService;
import com.example.backend.service.UserService;
import com.example.backend.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Redis中存储验证码的key前缀
    private static final String RESET_CODE_PREFIX = "password:reset:code:";
    // 验证码有效期（分钟）
    private static final int CODE_EXPIRATION_MINUTES = 5;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<LoginVO> login(LoginDTO loginDTO) {
        // 根据用户名查找用户
        Optional<User> userOptional = userRepository.findByUsername(loginDTO.getUsername());

        // 如果用户不存在，返回空
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        // 使用PasswordEncoder验证密码
        if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            // 密码正确，返回LoginVO对象，包含用户信息
            return Optional.of(new LoginVO(
                user.getId(),
                user.getUsername(),
                user.getUsername(), // 使用username作为邮箱
                user.getId(), // 使用id代替number，因为User类中没有getNumber方法
                user.getRole() // 直接使用Role枚举
            ));
        } else {
            // 密码错误，返回空
            return Optional.empty();
        }
    }

    @Override
    public String register(User user) {
        // 检查用户名(邮箱)是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            return "用户已存在";
        }

        try {
            // 打印调试信息
            System.out.println("注册前用户信息: " + user.toString());

            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 保存用户
            userRepository.save(user);
            return "注册成功";
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息，方便调试
            return "注册失败: " + e.getMessage();
        }
    }

    @Override
    public String registerStudent(User user) {
        // 设置角色为学生
        user.setRole(Role.STUDENT);
        return register(user);
    }

    @Override
    public String registerTeacher(User user) {
        // 设置角色为教师
        user.setRole(Role.TEACHER);
        return register(user);
    }

    @Override
    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID: " + id));
    }

    @Override
    @Deprecated
    public User findByNumber(Long number) {
        // 为了兼容性，直接调用findById方法
        return findById(number);
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }

    @Override
    public String generatePasswordResetCode(String email) {
        // 检查邮箱是否存在
        User user = findByUsername(email);
        if (user == null) {
            return "该邮箱未注册";
        }

        // 生成6位随机验证码
        String code = generateRandomCode(6);

        // 将验证码存储到Redis，设置过期时间
        redisTemplate.opsForValue().set(
            RESET_CODE_PREFIX + email,
            code,
            CODE_EXPIRATION_MINUTES,
            TimeUnit.MINUTES
        );

        // 发送验证码邮件
        boolean sent = emailService.sendVerificationCode(email, code);

        return sent ? "验证码已发送到您的邮箱，请查收" : "验证码发送失败，请稍后重试";
    }

    @Override
    public boolean verifyPasswordResetCode(String email, String code) {
        // 从Redis获取存储的验证码
        String storedCode = redisTemplate.opsForValue().get(RESET_CODE_PREFIX + email);

        // 验证码无效或已过期
        if (storedCode == null) {
            return false;
        }

        // 验证码匹配
        return storedCode.equals(code);
    }

    @Override
    public String resetPassword(String email, String newPassword, String code) {
        // 验证码是否正确
        if (!verifyPasswordResetCode(email, code)) {
            return "验证码无效或已过期";
        }

        // 查找用户
        User user = findByUsername(email);
        if (user == null) {
            return "用户不存在";
        }

        try {
            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // 删除Redis中的验证码
            redisTemplate.delete(RESET_CODE_PREFIX + email);

            return "密码重置成功";
        } catch (Exception e) {
            return "密码重置失败: " + e.getMessage();
        }
    }

    /**
     * 生成随机验证码
     */
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户对象不能为空");
        }

        // 检查创建时间，如果为空则设置
        if (user.getCreateTime() == null) {
            user.setCreateTime(new Date());
        }

        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户对象不能为空");
        }

        // 如果是新用户且密码未加密，则加密密码
        if (user.getId() == null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 检查创建时间，如果为空则设置
        if (user.getCreateTime() == null) {
            user.setCreateTime(new Date());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public long countAllUsers() {
        return userRepository.count();
    }

    @Override
    public long calculateUserGrowthRate() {
        // 计算过去30天内注册的用户数量
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        long recentUsers = userRepository.countByCreateTimeAfter(thirtyDaysAgo);

        // 计算30天前的用户总数
        long totalUsers = countAllUsers();
        long oldUsers = totalUsers - recentUsers;

        // 计算增长率
        if (oldUsers > 0) {
            return (recentUsers * 100) / oldUsers;
        } else {
            return recentUsers > 0 ? 100 : 0; // 如果之前没有用户，但现在有，则增长率为100%
        }
    }

    @Override
    public List<User> findRecentUsers(int limit) {
        // 按创建时间降序排序，获取最近注册的用户
        List<User> recentUsers = userRepository.findTop10ByOrderByCreateTimeDesc();

        // 如果结果少于请求的数量，直接返回
        if (recentUsers.size() <= limit) {
            return recentUsers;
        }

        // 否则只返回请求的数量
        return recentUsers.subList(0, limit);
    }

    @Override
    public Map<String, Object> findUsers(String keyword, String role, int page, int size) {
        // 创建分页对象，页码从0开始
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage;

        // 根据关键词和角色进行查询
        if (keyword != null && !keyword.isEmpty()) {
            if (role != null && !role.isEmpty()) {
                // 同时指定了关键词和角色
                userPage = userRepository.findByUsernameContainingAndRole(keyword, Role.valueOf(role), pageable);
            } else {
                // 只指定了关键词
                userPage = userRepository.findByUsernameContaining(keyword, pageable);
            }
        } else if (role != null && !role.isEmpty()) {
            // 只指定了角色
            userPage = userRepository.findByRole(Role.valueOf(role), pageable);
        } else {
            // 没有指定关键词和角色，返回所有用户
            userPage = userRepository.findAll(pageable);
        }

        // 将结果转换为Map
        Map<String, Object> result = new HashMap<>();
        result.put("content", userPage.getContent());
        result.put("totalElements", userPage.getTotalElements());
        result.put("totalPages", userPage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);

        return result;
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 统计总用户数
        long totalUsers = countAllUsers();
        statistics.put("totalUsers", totalUsers);

        // 统计启用的用户数
        long activeUsers = userRepository.countByEnabled(true);
        statistics.put("activeUsers", activeUsers);

        // 统计各角色用户数
        long studentCount = userRepository.countByRole("STUDENT");
        long teacherCount = userRepository.countByRole("TEACHER");
        long adminCount = userRepository.countByRole("ADMIN");

        statistics.put("studentCount", studentCount);
        statistics.put("teacherCount", teacherCount);
        statistics.put("adminCount", adminCount);

        // 用户增长率
        long growthRate = calculateUserGrowthRate();
        statistics.put("growthRate", growthRate);

        // 最近注册的用户
        List<User> recentUsers = findRecentUsers(5);
        statistics.put("recentUsers", recentUsers);

        return statistics;
    }

    @Override
    public void batchEnableUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        for (Long userId : userIds) {
            User user = findById(userId);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    @Override
    public void batchDisableUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        for (Long userId : userIds) {
            User user = findById(userId);
            user.setEnabled(false);
            userRepository.save(user);
        }
    }

    @Override
    public String exportUsers(String format) {
        // 实际项目中应该实现导出功能
        // 这里只是简单返回一个消息
        return "用户数据导出成功，格式：" + format;
    }

    @Override
    public Map<String, Object> importUsers(String fileUrl, String format) {
        // TODO: 实现用户导入逻辑
        return new HashMap<>();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countByEnabled(true);
    }

    @Override
    public long countAdminUsers() {
        return userRepository.countByRole("ADMIN");
    }

    @Override
    public long countTeacherUsers() {
        return userRepository.countByRole("TEACHER");
    }

    @Override
    public long countStudentUsers() {
        return userRepository.countByRole("STUDENT");
    }

    @Override
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        User user = findByUsername(username);
        return user != null && user.getRole() == Role.ADMIN;
    }

    @Override
    public List<Map<String, Object>> getTeacherTodos(Long teacherId) {
        return List.of();
    }

    @Override
    public Map<String, Object> addTeacherTodo(Long teacherId, Map<String, Object> todoData) {
        return Map.of();
    }

    @Override
    public Map<String, Object> updateTeacherTodo(Long teacherId, Long todoId, Map<String, Object> todoData) {
        return Map.of();
    }

    @Override
    public void deleteTeacherTodo(Long teacherId, Long todoId) {

    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        return false;
    }
}

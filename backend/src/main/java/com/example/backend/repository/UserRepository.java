package com.example.backend.repository;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名(邮箱)查找用户
    Optional<User> findByUsername(String username);

    // 检查用户名(邮箱)是否存在
    Boolean existsByUsername(String username);

    // 统计指定日期之后创建的用户数量
    long countByCreateTimeAfter(java.util.Date date);

    // 获取最近注册的用户，按创建时间降序排序
    List<User> findTop10ByOrderByCreateTimeDesc();

    // 根据用户名关键词查询用户
    Page<User> findByUsernameContaining(String username, Pageable pageable);

    // 根据用户名关键词和角色查询用户
    Page<User> findByUsernameContainingAndRole(String username, Role role, Pageable pageable);

    // 根据角色查询用户
    Page<User> findByRole(Role role, Pageable pageable);

    // 统计指定角色的用户数量
    long countByRole(Role role);

    // 统计启用状态的用户数量
    long countByEnabled(boolean enabled);

    // 根据角色名称统计用户数量
    long countByRole(String roleName);
}
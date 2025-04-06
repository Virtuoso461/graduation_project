package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据用户名(邮箱)查找用户
    Optional<User> findByUsername(String username);
    
    // 检查用户名(邮箱)是否存在
    Boolean existsByUsername(String username);
}
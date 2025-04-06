package com.example.backend.repository;

import com.example.backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    
    // 通过邮箱查找个人资料
    Optional<Profile> findByEmail(String email);
    
    // 检查邮箱是否存在
    boolean existsByEmail(String email);
    
    // 通过用户名查找个人资料
    Optional<Profile> findByUsername(String username);
} 
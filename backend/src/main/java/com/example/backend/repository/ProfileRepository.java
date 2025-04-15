package com.example.backend.repository;

import com.example.backend.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 用户个人资料数据访问接口
 * 提供对Profile实体的增删改查操作
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    
    /**
     * 通过邮箱查找个人资料
     * @param email 用户邮箱
     * @return 个人资料Optional包装对象
     */
    Optional<Profile> findByEmail(String email);
    
    /**
     * 检查邮箱是否存在
     * @param email 用户邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 通过用户名查找个人资料
     * @param username 用户名
     * @return 个人资料Optional包装对象
     */
    Optional<Profile> findByUsername(String username);
    
    /**
     * 通过姓名模糊搜索个人资料
     * @param name 姓名关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Profile> findByNameContaining(String name, Pageable pageable);
    
    /**
     * 查找某个生日之后出生的用户
     * @param birthdate 生日日期
     * @return 用户列表
     */
    List<Profile> findByBirthdayAfter(LocalDate birthdate);
    
    /**
     * 根据性别查找用户
     * @param gender 性别
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Profile> findByGender(String gender, Pageable pageable);
    
    /**
     * 复杂条件查询用户
     * @param nameKeyword 姓名关键词
     * @param gender 性别
     * @return 符合条件的用户列表
     */
    @Query("SELECT p FROM Profile p WHERE (:nameKeyword IS NULL OR p.name LIKE %:nameKeyword%) " +
           "AND (:gender IS NULL OR p.gender = :gender)")
    List<Profile> findByNameAndGender(@Param("nameKeyword") String nameKeyword, @Param("gender") String gender);
    
    /**
     * 统计各性别的用户数量
     * @return 包含性别和数量的列表
     */
    @Query("SELECT p.gender, COUNT(p) FROM Profile p GROUP BY p.gender")
    List<Object[]> countByGenderGroups();
} 
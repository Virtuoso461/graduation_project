package com.example.backend.repository;

import com.example.backend.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    // 查询用户的所有收藏
    Page<Favorite> findByUserEmail(String userEmail, Pageable pageable);
    
    // 查询用户某个收藏夹下的收藏
    Page<Favorite> findByUserEmailAndFolderId(String userEmail, String folderId, Pageable pageable);
    
    // 查询用户收藏的特定类型资源
    Page<Favorite> findByUserEmailAndResourceType(String userEmail, String resourceType, Pageable pageable);
    
    // 组合查询（按收藏夹和资源类型）
    @Query("SELECT f FROM Favorite f WHERE f.userEmail = :userEmail " +
           "AND (:folderId IS NULL OR f.folderId = :folderId) " +
           "AND (:resourceType IS NULL OR f.resourceType = :resourceType)")
    Page<Favorite> findByFilters(
            @Param("userEmail") String userEmail,
            @Param("folderId") String folderId,
            @Param("resourceType") String resourceType,
            Pageable pageable
    );
    
    // 搜索收藏内容（按标题或描述）
    @Query("SELECT f FROM Favorite f WHERE f.userEmail = :userEmail " +
           "AND (f.resourceTitle LIKE CONCAT('%', :keyword, '%') OR f.resourceDescription LIKE CONCAT('%', :keyword, '%'))")
    Page<Favorite> searchFavorites(
            @Param("userEmail") String userEmail,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    
    // 检查资源是否已收藏
    Optional<Favorite> findByUserEmailAndResourceIdAndResourceType(
            String userEmail,
            Long resourceId,
            String resourceType
    );
    
    // 统计用户收藏总数
    long countByUserEmail(String userEmail);
    
    // 统计用户某收藏夹下的资源数
    long countByUserEmailAndFolderId(String userEmail, String folderId);
} 
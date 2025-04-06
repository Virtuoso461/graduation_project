package com.example.backend.repository;

import com.example.backend.entity.FavoriteFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {
    
    // 查询用户的所有收藏夹
    List<FavoriteFolder> findByUserEmailOrderByCreateTimeDesc(String userEmail);
    
    // 根据文件夹ID查找特定文件夹
    Optional<FavoriteFolder> findByFolderId(String folderId);
    
    // 根据文件夹ID和用户邮箱查找
    Optional<FavoriteFolder> findByFolderIdAndUserEmail(String folderId, String userEmail);
    
    // 根据名称和用户查找收藏夹
    Optional<FavoriteFolder> findByNameAndUserEmail(String name, String userEmail);
    
    // 统计用户收藏夹总数
    long countByUserEmail(String userEmail);
} 
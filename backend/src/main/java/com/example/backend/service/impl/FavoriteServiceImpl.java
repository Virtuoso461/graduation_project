package com.example.backend.service.impl;

import com.example.backend.entity.Favorite;
import com.example.backend.entity.FavoriteFolder;
import com.example.backend.repository.FavoriteRepository;
import com.example.backend.repository.FavoriteFolderRepository;
import com.example.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private FavoriteFolderRepository favoriteFolderRepository;

    @Override
    @Transactional
    public FavoriteFolder createFolder(FavoriteFolder folder) {
        return favoriteFolderRepository.save(folder);
    }

    @Override
    @Transactional
    public FavoriteFolder updateFolder(String folderId, FavoriteFolder folder) {
        FavoriteFolder existingFolder = favoriteFolderRepository.findByFolderId(folderId)
                .orElseThrow(() -> new NoSuchElementException("收藏夹不存在，ID: " + folderId));
        
        // 更新收藏夹信息
        existingFolder.setName(folder.getName());
        existingFolder.setDescription(folder.getDescription());
        existingFolder.setUpdateTime(folder.getUpdateTime());
        
        return favoriteFolderRepository.save(existingFolder);
    }

    @Override
    @Transactional
    public boolean deleteFolder(String folderId) {
        FavoriteFolder folder = favoriteFolderRepository.findByFolderId(folderId).orElse(null);
        
        if (folder != null) {
            // 先删除该收藏夹中的所有收藏
            Page<Favorite> favorites = favoriteRepository.findByUserEmailAndFolderId(
                    folder.getUserEmail(), folderId, Pageable.unpaged());
            
            favorites.forEach(favorite -> favoriteRepository.delete(favorite));
            
            // 再删除收藏夹
            favoriteFolderRepository.delete(folder);
            return true;
        }
        
        return false;
    }

    @Override
    public List<FavoriteFolder> getUserFolders(String userEmail) {
        return favoriteFolderRepository.findByUserEmailOrderByCreateTimeDesc(userEmail);
    }

    @Override
    public FavoriteFolder getFolder(String folderId) {
        return favoriteFolderRepository.findByFolderId(folderId).orElse(null);
    }

    @Override
    @Transactional
    public Favorite addFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public boolean removeFavorite(Long favoriteId) {
        if (favoriteRepository.existsById(favoriteId)) {
            favoriteRepository.deleteById(favoriteId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Favorite moveFavorite(Long favoriteId, String targetFolderId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NoSuchElementException("收藏不存在，ID: " + favoriteId));
        
        // 验证目标收藏夹是否存在
        if (targetFolderId != null && !targetFolderId.isEmpty()) {
            favoriteFolderRepository.findByFolderId(targetFolderId)
                    .orElseThrow(() -> new NoSuchElementException("目标收藏夹不存在，ID: " + targetFolderId));
        }
        
        favorite.setFolderId(targetFolderId);
        return favoriteRepository.save(favorite);
    }

    @Override
    public Page<Favorite> getUserFavorites(String userEmail, Map<String, Object> filters, Pageable pageable) {
        String folderId = (String) filters.get("folderId");
        String resourceType = (String) filters.get("resourceType");
        
        if (folderId != null && resourceType != null) {
            return favoriteRepository.findByFilters(userEmail, folderId, resourceType, pageable);
        } else if (folderId != null) {
            return favoriteRepository.findByUserEmailAndFolderId(userEmail, folderId, pageable);
        } else if (resourceType != null) {
            return favoriteRepository.findByUserEmailAndResourceType(userEmail, resourceType, pageable);
        } else {
            return favoriteRepository.findByUserEmail(userEmail, pageable);
        }
    }

    @Override
    public Page<Favorite> getFolderResources(String folderId, String userEmail, Pageable pageable) {
        return favoriteRepository.findByUserEmailAndFolderId(userEmail, folderId, pageable);
    }

    @Override
    public Page<Favorite> searchFavorites(String userEmail, String keyword, Pageable pageable) {
        return favoriteRepository.searchFavorites(userEmail, keyword, pageable);
    }

    @Override
    public boolean isResourceFavorited(String userEmail, Long resourceId, String resourceType) {
        return favoriteRepository.findByUserEmailAndResourceIdAndResourceType(
                userEmail, resourceId, resourceType).isPresent();
    }

    @Override
    @Transactional
    public Favorite updateLastAccessTime(Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NoSuchElementException("收藏不存在，ID: " + favoriteId));
        
        favorite.setLastAccessTime(LocalDateTime.now());
        return favoriteRepository.save(favorite);
    }
} 
package com.example.backend.service.impl;

import com.example.backend.entity.Favorite;
import com.example.backend.entity.FavoriteFolder;
import com.example.backend.repository.FavoriteRepository;
import com.example.backend.repository.FavoriteFolderRepository;
import com.example.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, FavoriteFolderRepository favoriteFolderRepository) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteFolderRepository = favoriteFolderRepository;
    }

    @Override
    @Transactional
    public FavoriteFolder createFolder(FavoriteFolder folder) {
        // 检查同名文件夹是否存在
        Optional<FavoriteFolder> existingFolder = favoriteFolderRepository.findByNameAndUserEmail(
                folder.getName(), folder.getUserEmail());
        
        if (existingFolder.isPresent()) {
            throw new IllegalArgumentException("同名收藏夹已存在");
        }
        
        return favoriteFolderRepository.save(folder);
    }

    @Override
    @Transactional
    public FavoriteFolder updateFolder(String folderId, FavoriteFolder folder) {
        FavoriteFolder existingFolder = favoriteFolderRepository.findByFolderId(folderId)
                .orElseThrow(() -> new NoSuchElementException("收藏夹不存在"));
        
        // 检查权限
        if (!existingFolder.getUserEmail().equals(folder.getUserEmail())) {
            throw new SecurityException("无权限修改该收藏夹");
        }
        
        // 检查同名文件夹是否存在（排除自身）
        Optional<FavoriteFolder> sameNameFolder = favoriteFolderRepository.findByNameAndUserEmail(
                folder.getName(), folder.getUserEmail());
        
        if (sameNameFolder.isPresent() && !sameNameFolder.get().getFolderId().equals(folderId)) {
            throw new IllegalArgumentException("同名收藏夹已存在");
        }
        
        // 更新字段
        existingFolder.setName(folder.getName());
        existingFolder.setDescription(folder.getDescription());
        existingFolder.setIcon(folder.getIcon());
        existingFolder.setUpdateTime(LocalDateTime.now());
        
        return favoriteFolderRepository.save(existingFolder);
    }

    @Override
    @Transactional
    public boolean deleteFolder(String folderId) {
        try {
            // 检查收藏夹是否存在
            if (!favoriteFolderRepository.findByFolderId(folderId).isPresent()) {
                return false;
            }
            
            // 获取收藏夹中的所有收藏
            Page<Favorite> favorites = favoriteRepository.findByUserEmailAndFolderId(
                    favoriteFolderRepository.findByFolderId(folderId).get().getUserEmail(),
                    folderId,
                    PageRequest.of(0, Integer.MAX_VALUE));
            
            // 将收藏夹中的收藏移动到默认收藏夹（folderId为null）
            for (Favorite favorite : favorites) {
                favorite.setFolderId(null);
                favoriteRepository.save(favorite);
            }
            
            // 删除收藏夹
            favoriteFolderRepository.delete(favoriteFolderRepository.findByFolderId(folderId).get());
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
        // 检查收藏夹是否存在
        if (favorite.getFolderId() != null) {
            FavoriteFolder folder = favoriteFolderRepository.findByFolderId(favorite.getFolderId()).orElse(null);
            if (folder == null || !folder.getUserEmail().equals(favorite.getUserEmail())) {
                throw new IllegalArgumentException("收藏夹不存在或无权限访问");
            }
        }
        
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public boolean removeFavorite(Long favoriteId) {
        try {
            if (favoriteRepository.existsById(favoriteId)) {
                favoriteRepository.deleteById(favoriteId);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public Favorite moveFavorite(Long favoriteId, String targetFolderId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NoSuchElementException("收藏不存在"));
        
        // 检查目标收藏夹是否存在
        if (targetFolderId != null) {
            FavoriteFolder folder = favoriteFolderRepository.findByFolderId(targetFolderId).orElse(null);
            if (folder == null || !folder.getUserEmail().equals(favorite.getUserEmail())) {
                throw new IllegalArgumentException("收藏夹不存在或无权限访问");
            }
        }
        
        // 更新收藏夹ID
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
    public Optional<Favorite> findByUserEmailAndResourceIdAndResourceType(String userEmail, Long resourceId, String resourceType) {
        return favoriteRepository.findByUserEmailAndResourceIdAndResourceType(userEmail, resourceId, resourceType);
    }

    @Override
    @Transactional
    public Favorite updateFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public Favorite updateLastAccessTime(Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NoSuchElementException("收藏不存在"));
        
        favorite.setLastAccessTime(LocalDateTime.now());
        
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public boolean batchMoveFavorites(List<Long> favoriteIds, String targetFolderId) {
        try {
            // 检查目标收藏夹是否存在
            if (targetFolderId != null) {
                FavoriteFolder folder = favoriteFolderRepository.findByFolderId(targetFolderId).orElse(null);
                if (folder == null) {
                    return false;
                }
            }
            
            // 批量移动收藏
            for (Long favoriteId : favoriteIds) {
                Optional<Favorite> favoriteOpt = favoriteRepository.findById(favoriteId);
                if (favoriteOpt.isPresent()) {
                    Favorite favorite = favoriteOpt.get();
                    favorite.setFolderId(targetFolderId);
                    favoriteRepository.save(favorite);
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean batchRemoveFavorites(List<Long> favoriteIds) {
        try {
            for (Long favoriteId : favoriteIds) {
                if (favoriteRepository.existsById(favoriteId)) {
                    favoriteRepository.deleteById(favoriteId);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Object> getUserFavoriteStatistics(String userEmail) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计收藏总数
        long totalCount = favoriteRepository.countByUserEmail(userEmail);
        statistics.put("totalCount", totalCount);
        
        // 统计收藏夹数量
        long folderCount = favoriteFolderRepository.countByUserEmail(userEmail);
        statistics.put("folderCount", folderCount);
        
        // 获取收藏夹及其资源数量
        List<Map<String, Object>> folders = new ArrayList<>();
        for (FavoriteFolder folder : favoriteFolderRepository.findByUserEmailOrderByCreateTimeDesc(userEmail)) {
            Map<String, Object> folderMap = new HashMap<>();
            folderMap.put("id", folder.getFolderId());
            folderMap.put("name", folder.getName());
            folderMap.put("count", favoriteRepository.countByUserEmailAndFolderId(userEmail, folder.getFolderId()));
            folders.add(folderMap);
        }
        statistics.put("folders", folders);
        
        // 统计未分类收藏数量
        long uncategorizedCount = favoriteRepository.countByUserEmailAndFolderId(userEmail, null);
        statistics.put("uncategorizedCount", uncategorizedCount);
        
        return statistics;
    }

    @Override
    public List<Favorite> getRecentFavorites(String userEmail, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "collectionTime"));
        return favoriteRepository.findByUserEmail(userEmail, pageable).getContent();
    }

    @Override
    public List<Favorite> getMostAccessedFavorites(String userEmail, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastAccessTime"));
        return favoriteRepository.findByUserEmail(userEmail, pageable).getContent();
    }

    @Override
    public Map<String, Object> getUserFavoritesPage(String userEmail, Map<String, Object> queryParams) {
        return Map.of();
    }
}

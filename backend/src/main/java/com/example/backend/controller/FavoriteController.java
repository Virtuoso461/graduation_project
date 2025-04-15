package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Favorite;
import com.example.backend.entity.FavoriteFolder;
import com.example.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 收藏功能控制器
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取当前登录用户
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 获取用户收藏夹列表
     */
    @GetMapping("/folders")
    public Result<List<Map<String, Object>>> getFolders() {
        String userEmail = getCurrentUserEmail();
        List<FavoriteFolder> folders = favoriteService.getUserFolders(userEmail);

        // 构建响应数据，包含每个收藏夹的资源计数
        List<Map<String, Object>> result = new ArrayList<>();
        for (FavoriteFolder folder : folders) {
            Map<String, Object> folderMap = new HashMap<>();
            folderMap.put("id", folder.getFolderId());
            folderMap.put("name", folder.getName());
            folderMap.put("description", folder.getDescription());

            // 获取收藏夹中的资源数量
            Page<Favorite> resources = favoriteService.getFolderResources(
                    folder.getFolderId(), userEmail, PageRequest.of(0, 1));
            folderMap.put("resourceCount", resources.getTotalElements());

            result.add(folderMap);
        }

        return Result.success(result);
    }

    /**
     * 创建收藏夹
     */
    @PostMapping("/folders")
    public Result<FavoriteFolder> createFolder(@RequestBody FavoriteFolder folder) {
        String userEmail = getCurrentUserEmail();

        // 设置用户信息
        folder.setUserEmail(userEmail);

        // 生成唯一的folder_id
        folder.setFolderId(UUID.randomUUID().toString());

        // 设置创建时间
        folder.setCreateTime(LocalDateTime.now());

        FavoriteFolder createdFolder = favoriteService.createFolder(folder);
        return Result.success(createdFolder);
    }

    /**
     * 更新收藏夹
     */
    @PutMapping("/folders/{folderId}")
    public Result<FavoriteFolder> updateFolder(
            @PathVariable String folderId,
            @RequestBody FavoriteFolder folder) {

        String userEmail = getCurrentUserEmail();
        FavoriteFolder existingFolder = favoriteService.getFolder(folderId);

        if (existingFolder == null) {
            return Result.failed("收藏夹不存在");
        }

        // 检查是否是收藏夹所有者
        if (!existingFolder.getUserEmail().equals(userEmail)) {
            return Result.failed("无权限修改他人收藏夹");
        }

        // 设置更新时间
        folder.setUpdateTime(LocalDateTime.now());

        FavoriteFolder updatedFolder = favoriteService.updateFolder(folderId, folder);
        return Result.success(updatedFolder);
    }

    /**
     * 删除收藏夹
     */
    @DeleteMapping("/folders/{folderId}")
    public Result<Boolean> deleteFolder(@PathVariable String folderId) {
        String userEmail = getCurrentUserEmail();
        FavoriteFolder existingFolder = favoriteService.getFolder(folderId);

        if (existingFolder == null) {
            return Result.failed("收藏夹不存在");
        }

        // 检查是否是收藏夹所有者
        if (!existingFolder.getUserEmail().equals(userEmail)) {
            return Result.failed("无权限删除他人收藏夹");
        }

        boolean success = favoriteService.deleteFolder(folderId);
        return success ? Result.success(true) : Result.failed("删除失败");
    }

    /**
     * 获取收藏夹中的资源
     */
    @GetMapping("/folders/{folderId}/resources")
    public Result<Map<String, Object>> getFolderResources(
            @PathVariable String folderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "collectionTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        String userEmail = getCurrentUserEmail();

        // 检查收藏夹是否存在且属于当前用户
        FavoriteFolder folder = favoriteService.getFolder(folderId);
        if (folder == null || !folder.getUserEmail().equals(userEmail)) {
            return Result.failed("收藏夹不存在或无权限访问");
        }

        // 创建分页参数
        Sort sort = Sort.by(
                direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 查询资源
        Page<Favorite> resourcesPage = favoriteService.getFolderResources(folderId, userEmail, pageable);

        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("resources", resourcesPage.getContent());
        response.put("currentPage", resourcesPage.getNumber());
        response.put("totalItems", resourcesPage.getTotalElements());
        response.put("totalPages", resourcesPage.getTotalPages());

        return Result.success(response);
    }

    /**
     * 获取所有收藏资源
     */
    @GetMapping("/resources")
    public Result<Map<String, Object>> getResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String folderId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "collectionTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        String userEmail = getCurrentUserEmail();

        // 创建分页参数
        Sort sort = Sort.by(
                direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 构建过滤条件
        Map<String, Object> filters = new HashMap<>();
        if (folderId != null && !folderId.isEmpty()) {
            filters.put("folderId", folderId);
        }
        if (resourceType != null && !resourceType.isEmpty()) {
            filters.put("resourceType", resourceType);
        }

        // 查询收藏
        Page<Favorite> favoritesPage;
        if (keyword != null && !keyword.isEmpty()) {
            favoritesPage = favoriteService.searchFavorites(userEmail, keyword, pageable);
        } else {
            favoritesPage = favoriteService.getUserFavorites(userEmail, filters, pageable);
        }

        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("resources", favoritesPage.getContent());
        response.put("currentPage", favoritesPage.getNumber());
        response.put("totalItems", favoritesPage.getTotalElements());
        response.put("totalPages", favoritesPage.getTotalPages());

        return Result.success(response);
    }

    /**
     * 添加资源到收藏
     */
    @PostMapping("/resources")
    public Result<Favorite> addToFavorites(@RequestBody Favorite favorite) {
        String userEmail = getCurrentUserEmail();

        // 设置用户信息
        favorite.setUserEmail(userEmail);

        // 设置收藏时间
        favorite.setCollectionTime(LocalDateTime.now());
        favorite.setLastAccessTime(LocalDateTime.now());

        // 检查资源是否已收藏
        if (favoriteService.isResourceFavorited(
                userEmail, favorite.getResourceId(), favorite.getResourceType())) {
            return Result.failed("该资源已收藏");
        }

        Favorite savedFavorite = favoriteService.addFavorite(favorite);
        return Result.success(savedFavorite);
    }

    /**
     * 从收藏中移除资源
     */
    @PostMapping("/resources/{resourceId}/remove")
    public Result<Boolean> removeFromFavorites(@PathVariable Long resourceId) {
        boolean success = favoriteService.removeFavorite(resourceId);
        return success ? Result.success(true) : Result.failed("移除收藏失败");
    }

    /**
     * 移动收藏到其他收藏夹
     */
    @PostMapping("/resources/{resourceId}/move")
    public Result<Favorite> moveResource(
            @PathVariable Long resourceId,
            @RequestBody Map<String, String> request) {

        String folderId = request.get("folderId");
        if (folderId == null) {
            return Result.failed("目标收藏夹ID不能为空");
        }

        Favorite updatedFavorite = favoriteService.moveFavorite(resourceId, folderId);
        return Result.success(updatedFavorite);
    }

    /**
     * 更新资源访问时间
     */
    @PostMapping("/resources/{resourceId}/access")
    public Result<Boolean> accessResource(@PathVariable Long resourceId) {
        Favorite favorite = favoriteService.updateLastAccessTime(resourceId);
        return favorite != null ? Result.success(true) : Result.failed("更新访问时间失败");
    }

    /**
     * 检查资源是否已收藏
     */
    @GetMapping("/resources/check")
    public Result<Boolean> checkFavorite(
            @RequestParam Long resourceId,
            @RequestParam String resourceType) {

        String userEmail = getCurrentUserEmail();
        boolean favorited = favoriteService.isResourceFavorited(userEmail, resourceId, resourceType);
        return Result.success(favorited);
    }

    /**
     * 批量移动收藏到其他收藏夹
     */
    @PostMapping("/resources/batch-move")
    public Result<Boolean> batchMoveResources(
            @RequestBody Map<String, Object> request) {

        String folderId = (String) request.get("folderId");
        List<Long> resourceIds = (List<Long>) request.get("resourceIds");

        if (folderId == null || resourceIds == null || resourceIds.isEmpty()) {
            return Result.validateFailed("目标收藏夹ID和资源ID列表不能为空");
        }

        boolean success = favoriteService.batchMoveFavorites(resourceIds, folderId);
        return success ? Result.success(true) : Result.failed("批量移动失败");
    }

    /**
     * 批量删除收藏
     */
    @PostMapping("/resources/batch-remove")
    public Result<Boolean> batchRemoveResources(
            @RequestBody Map<String, Object> request) {

        List<Long> resourceIds = (List<Long>) request.get("resourceIds");

        if (resourceIds == null || resourceIds.isEmpty()) {
            return Result.validateFailed("资源ID列表不能为空");
        }

        boolean success = favoriteService.batchRemoveFavorites(resourceIds);
        return success ? Result.success(true) : Result.failed("批量删除失败");
    }

    /**
     * 获取收藏统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getFavoriteStatistics() {
        String userEmail = getCurrentUserEmail();
        Map<String, Object> statistics = favoriteService.getUserFavoriteStatistics(userEmail);
        return Result.success(statistics);
    }

    /**
     * 获取最近收藏的资源
     */
    @GetMapping("/recent")
    public Result<List<Favorite>> getRecentFavorites(
            @RequestParam(defaultValue = "5") int limit) {

        String userEmail = getCurrentUserEmail();
        List<Favorite> recentFavorites = favoriteService.getRecentFavorites(userEmail, limit);
        return Result.success(recentFavorites);
    }

    /**
     * 获取最常访问的收藏资源
     */
    @GetMapping("/most-accessed")
    public Result<List<Favorite>> getMostAccessedFavorites(
            @RequestParam(defaultValue = "5") int limit) {

        String userEmail = getCurrentUserEmail();
        List<Favorite> mostAccessedFavorites = favoriteService.getMostAccessedFavorites(userEmail, limit);
        return Result.success(mostAccessedFavorites);
    }
}
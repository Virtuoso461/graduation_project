package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.ResourceQueryDTO;
import com.example.backend.dto.ResourceFavoriteDTO;
import com.example.backend.dto.FolderDTO;
import com.example.backend.entity.Favorite;
import com.example.backend.entity.FavoriteFolder;
import com.example.backend.entity.Resource;
import com.example.backend.service.FavoriteService;
import com.example.backend.service.ResourceService;
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
import java.util.stream.Collectors;

/**
 * 学生资源收藏控制器
 * 提供学生资源收藏相关的API
 */
@RestController
@RequestMapping("/api/student/resources")
public class StudentResourceController {

    private final ResourceService resourceService;
    private final FavoriteService favoriteService;

    @Autowired
    public StudentResourceController(ResourceService resourceService, FavoriteService favoriteService) {
        this.resourceService = resourceService;
        this.favoriteService = favoriteService;
    }

    /**
     * 获取当前登录用户的邮箱
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 获取可访问的资源列表
     *
     * @param queryDTO 查询参数DTO
     * @return 资源列表
     */
    @PostMapping
    public Result<Map<String, Object>> getResources(@RequestBody ResourceQueryDTO queryDTO) {
        // 创建分页参数
        Sort sort = Sort.by(
                queryDTO.getDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                queryDTO.getSortBy());
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);
        
        // 构建过滤条件
        Map<String, Object> filters = new HashMap<>();
        if (queryDTO.getType() != null && !queryDTO.getType().isEmpty()) {
            filters.put("type", queryDTO.getType());
        }
        if (queryDTO.getCategory() != null && !queryDTO.getCategory().isEmpty()) {
            filters.put("category", queryDTO.getCategory());
        }
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            filters.put("keyword", queryDTO.getKeyword());
        }
        
        // 查询资源
        Page<Resource> resourcesPage = resourceService.getResources(filters, pageable);
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("resources", resourcesPage.getContent());
        response.put("currentPage", resourcesPage.getNumber());
        response.put("totalItems", resourcesPage.getTotalElements());
        response.put("totalPages", resourcesPage.getTotalPages());
        
        return Result.success(response);
    }

    /**
     * 获取资源详情
     *
     * @param resourceId 资源ID
     * @return 资源详情
     */
    @GetMapping("/{resourceId}")
    public Result<Map<String, Object>> getResourceDetail(@PathVariable Long resourceId) {
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource == null) {
            return Result.failed("资源不存在");
        }
        
        // 增加查看次数
        resourceService.incrementViewCount(resourceId);
        
        // 检查资源是否已收藏
        String userEmail = getCurrentUserEmail();
        boolean isFavorited = favoriteService.isResourceFavorited(userEmail, resourceId, resource.getResourceType());
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("resource", resource);
        response.put("isFavorited", isFavorited);
        
        return Result.success(response);
    }

    /**
     * 收藏资源
     *
     * @param resourceId 资源ID
     * @param favoriteDTO 收藏信息
     * @return 收藏结果
     */
    @PostMapping("/{resourceId}/collect")
    public Result<Favorite> collectResource(
            @PathVariable Long resourceId,
            @RequestBody ResourceFavoriteDTO favoriteDTO) {
        
        String userEmail = getCurrentUserEmail();
        
        // 获取资源详情
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource == null) {
            return Result.failed("资源不存在");
        }
        
        // 检查资源是否已收藏
        if (favoriteService.isResourceFavorited(userEmail, resourceId, resource.getResourceType())) {
            return Result.failed("该资源已收藏");
        }
        
        // 创建收藏对象
        Favorite favorite = new Favorite();
        favorite.setResourceId(resourceId);
        favorite.setResourceType(resource.getResourceType());
        favorite.setUserEmail(userEmail);
        favorite.setResourceTitle(resource.getTitle());
        favorite.setResourceDescription(resource.getDescription());
        favorite.setResourceUrl(resource.getFileUrl() != null ? resource.getFileUrl() : resource.getExternalLink());
        favorite.setCollectionTime(LocalDateTime.now());
        favorite.setLastAccessTime(LocalDateTime.now());
        
        // 设置收藏夹
        if (favoriteDTO.getFolderId() != null) {
            favorite.setFolderId(favoriteDTO.getFolderId());
        }
        
        // 设置备注
        if (favoriteDTO.getNotes() != null) {
            favorite.setNotes(favoriteDTO.getNotes());
        }
        
        // 保存收藏
        Favorite savedFavorite = favoriteService.addFavorite(favorite);
        return Result.success(savedFavorite, "收藏成功");
    }

    /**
     * 取消收藏资源
     *
     * @param resourceId 资源ID
     * @return 取消收藏结果
     */
    @DeleteMapping("/{resourceId}/collect")
    public Result<Boolean> uncollectResource(@PathVariable Long resourceId) {
        String userEmail = getCurrentUserEmail();
        
        // 获取资源详情
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource == null) {
            return Result.failed("资源不存在");
        }
        
        // 查找收藏记录
        Optional<Favorite> favorite = favoriteService.findByUserEmailAndResourceIdAndResourceType(
                userEmail, resourceId, resource.getResourceType());
        
        if (!favorite.isPresent()) {
            return Result.failed("未找到收藏记录");
        }
        
        // 删除收藏
        boolean success = favoriteService.removeFavorite(favorite.get().getId());
        return success ? Result.success(true, "取消收藏成功") : Result.failed("取消收藏失败");
    }

    /**
     * 获取收藏的资源
     *
     * @param queryDTO 查询参数DTO
     * @return 收藏的资源列表
     */
    @PostMapping("/collections")
    public Result<Map<String, Object>> getCollections(@RequestBody ResourceQueryDTO queryDTO) {
        String userEmail = getCurrentUserEmail();
        
        // 创建分页参数
        Sort sort = Sort.by(
                queryDTO.getDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                queryDTO.getSortBy());
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);
        
        // 构建过滤条件
        Map<String, Object> filters = new HashMap<>();
        if (queryDTO.getFolderId() != null && !queryDTO.getFolderId().isEmpty()) {
            filters.put("folderId", queryDTO.getFolderId());
        }
        if (queryDTO.getResourceType() != null && !queryDTO.getResourceType().isEmpty()) {
            filters.put("resourceType", queryDTO.getResourceType());
        }
        
        // 查询收藏
        Page<Favorite> favoritesPage;
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            favoritesPage = favoriteService.searchFavorites(userEmail, queryDTO.getKeyword(), pageable);
        } else {
            favoritesPage = favoriteService.getUserFavorites(userEmail, filters, pageable);
        }
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("collections", favoritesPage.getContent());
        response.put("currentPage", favoritesPage.getNumber());
        response.put("totalItems", favoritesPage.getTotalElements());
        response.put("totalPages", favoritesPage.getTotalPages());
        
        return Result.success(response);
    }

    /**
     * 获取收藏夹列表
     *
     * @return 收藏夹列表
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
            folderMap.put("icon", folder.getIcon());
            folderMap.put("createTime", folder.getCreateTime());
            
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
     *
     * @param folderDTO 收藏夹对象
     * @return 创建后的收藏夹
     */
    @PostMapping("/folders")
    public Result<FavoriteFolder> createFolder(@RequestBody FolderDTO folderDTO) {
        String userEmail = getCurrentUserEmail();
        
        // 创建收藏夹对象
        FavoriteFolder folder = new FavoriteFolder();
        folder.setName(folderDTO.getName());
        folder.setDescription(folderDTO.getDescription());
        folder.setIcon(folderDTO.getIcon());
        
        // 设置用户信息
        folder.setUserEmail(userEmail);
        
        // 生成唯一的folder_id
        folder.setFolderId(UUID.randomUUID().toString());
        
        // 设置创建时间
        folder.setCreateTime(LocalDateTime.now());
        
        FavoriteFolder createdFolder = favoriteService.createFolder(folder);
        return Result.success(createdFolder, "收藏夹创建成功");
    }

    /**
     * 重命名收藏夹
     *
     * @param folderDTO 收藏夹信息
     * @return 更新后的收藏夹
     */
    @PutMapping("/folders")
    public Result<FavoriteFolder> renameFolder(@RequestBody FolderDTO folderDTO) {
        String userEmail = getCurrentUserEmail();
        
        String folderId = folderDTO.getFolderId();
        String newName = folderDTO.getName();
        String newDescription = folderDTO.getDescription();
        
        if (folderId == null || folderId.isEmpty()) {
            return Result.failed("收藏夹ID不能为空");
        }
        
        if (newName == null || newName.isEmpty()) {
            return Result.failed("收藏夹名称不能为空");
        }
        
        // 获取收藏夹
        FavoriteFolder folder = favoriteService.getFolder(folderId);
        if (folder == null || !folder.getUserEmail().equals(userEmail)) {
            return Result.failed("收藏夹不存在或无权限访问");
        }
        
        // 更新收藏夹
        folder.setName(newName);
        if (newDescription != null) {
            folder.setDescription(newDescription);
        }
        if (folderDTO.getIcon() != null) {
            folder.setIcon(folderDTO.getIcon());
        }
        folder.setUpdateTime(LocalDateTime.now());
        
        FavoriteFolder updatedFolder = favoriteService.updateFolder(folderId, folder);
        return Result.success(updatedFolder, "收藏夹更新成功");
    }

    /**
     * 移动资源到其他收藏夹
     *
     * @param resourceId 资源ID
     * @param request 请求参数
     * @return 移动结果
     */
    @PutMapping("/{resourceId}/move")
    public Result<Favorite> moveResource(
            @PathVariable Long resourceId,
            @RequestBody ResourceFavoriteDTO request) {
        
        String targetFolderId = request.getFolderId();
        if (targetFolderId == null) {
            return Result.failed("目标收藏夹ID不能为空");
        }
        
        // 移动收藏
        Favorite updatedFavorite = favoriteService.moveFavorite(resourceId, targetFolderId);
        return Result.success(updatedFavorite, "移动成功");
    }

    /**
     * 更新资源收藏备注
     *
     * @param resourceId 资源ID
     * @param request 请求参数
     * @return 更新结果
     */
    @PutMapping("/{resourceId}/notes")
    public Result<Favorite> updateNotes(
            @PathVariable Long resourceId,
            @RequestBody ResourceFavoriteDTO request) {
        
        String userEmail = getCurrentUserEmail();
        String notes = request.getNotes();
        
        // 获取资源详情
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource == null) {
            return Result.failed("资源不存在");
        }
        
        // 查找收藏记录
        Optional<Favorite> favoriteOpt = favoriteService.findByUserEmailAndResourceIdAndResourceType(
                userEmail, resourceId, resource.getResourceType());
        
        if (!favoriteOpt.isPresent()) {
            return Result.failed("未找到收藏记录");
        }
        
        // 更新备注
        Favorite favorite = favoriteOpt.get();
        favorite.setNotes(notes);
        
        Favorite updatedFavorite = favoriteService.updateFavorite(favorite);
        return Result.success(updatedFavorite, "备注更新成功");
    }

    /**
     * 获取热门资源
     *
     * @param queryDTO 查询参数DTO
     * @return 热门资源列表
     */
    @PostMapping("/hot")
    public Result<List<Resource>> getHotResources(@RequestBody ResourceQueryDTO queryDTO) {
        List<Resource> hotResources = resourceService.getHotResources(queryDTO.getLimit());
        return Result.success(hotResources);
    }

    /**
     * 获取最新资源
     *
     * @param queryDTO 查询参数DTO
     * @return 最新资源列表
     */
    @PostMapping("/latest")
    public Result<List<Resource>> getLatestResources(@RequestBody ResourceQueryDTO queryDTO) {
        List<Resource> latestResources = resourceService.getLatestResources(queryDTO.getLimit());
        return Result.success(latestResources);
    }

    /**
     * 记录资源访问
     *
     * @param resourceId 资源ID
     * @return 记录结果
     */
    @PostMapping("/{resourceId}/access")
    public Result<Boolean> accessResource(@PathVariable Long resourceId) {
        String userEmail = getCurrentUserEmail();
        
        // 获取资源详情
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource == null) {
            return Result.failed("资源不存在");
        }
        
        // 增加查看次数
        resourceService.incrementViewCount(resourceId);
        
        // 查找收藏记录
        Optional<Favorite> favoriteOpt = favoriteService.findByUserEmailAndResourceIdAndResourceType(
                userEmail, resourceId, resource.getResourceType());
        
        // 如果已收藏，更新访问时间
        if (favoriteOpt.isPresent()) {
            Favorite favorite = favoriteOpt.get();
            favoriteService.updateLastAccessTime(favorite.getId());
        }
        
        return Result.success(true);
    }
}

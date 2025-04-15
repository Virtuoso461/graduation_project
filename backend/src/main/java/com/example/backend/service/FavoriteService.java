package com.example.backend.service;

import com.example.backend.entity.Favorite;
import com.example.backend.entity.FavoriteFolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 创建收藏夹
     * @param folder 收藏夹对象
     * @return 创建后的收藏夹
     */
    FavoriteFolder createFolder(FavoriteFolder folder);

    /**
     * 更新收藏夹
     * @param folderId 收藏夹ID
     * @param folder 收藏夹更新数据
     * @return 更新后的收藏夹
     */
    FavoriteFolder updateFolder(String folderId, FavoriteFolder folder);

    /**
     * 删除收藏夹
     * @param folderId 收藏夹ID
     * @return 是否删除成功
     */
    boolean deleteFolder(String folderId);

    /**
     * 获取用户的所有收藏夹
     * @param userEmail 用户邮箱
     * @return 收藏夹列表
     */
    List<FavoriteFolder> getUserFolders(String userEmail);

    /**
     * 获取收藏夹详情
     * @param folderId 收藏夹ID
     * @return 收藏夹详情
     */
    FavoriteFolder getFolder(String folderId);

    /**
     * 添加收藏
     * @param favorite 收藏对象
     * @return 创建后的收藏
     */
    Favorite addFavorite(Favorite favorite);

    /**
     * 移除收藏
     * @param favoriteId 收藏ID
     * @return 是否删除成功
     */
    boolean removeFavorite(Long favoriteId);

    /**
     * 移动收藏到其他收藏夹
     * @param favoriteId 收藏ID
     * @param targetFolderId 目标收藏夹ID
     * @return 更新后的收藏
     */
    Favorite moveFavorite(Long favoriteId, String targetFolderId);

    /**
     * 查询用户的所有收藏
     * @param userEmail 用户邮箱
     * @param filters 过滤条件（收藏夹ID、资源类型等）
     * @param pageable 分页参数
     * @return 收藏分页数据
     */
    Page<Favorite> getUserFavorites(String userEmail, Map<String, Object> filters, Pageable pageable);

    /**
     * 查询收藏夹中的资源
     * @param folderId 收藏夹ID
     * @param userEmail 用户邮箱
     * @param pageable 分页参数
     * @return 收藏分页数据
     */
    Page<Favorite> getFolderResources(String folderId, String userEmail, Pageable pageable);

    /**
     * 搜索收藏
     * @param userEmail 用户邮箱
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 收藏分页数据
     */
    Page<Favorite> searchFavorites(String userEmail, String keyword, Pageable pageable);

    /**
     * 检查资源是否已收藏
     * @param userEmail 用户邮箱
     * @param resourceId 资源ID
     * @param resourceType 资源类型
     * @return 是否已收藏
     */
    boolean isResourceFavorited(String userEmail, Long resourceId, String resourceType);

    /**
     * 查找收藏记录
     * @param userEmail 用户邮箱
     * @param resourceId 资源ID
     * @param resourceType 资源类型
     * @return 收藏记录
     */
    Optional<Favorite> findByUserEmailAndResourceIdAndResourceType(String userEmail, Long resourceId, String resourceType);

    /**
     * 更新收藏记录
     * @param favorite 收藏记录
     * @return 更新后的收藏记录
     */
    Favorite updateFavorite(Favorite favorite);

    /**
     * 更新资源的最后访问时间
     * @param favoriteId 收藏ID
     * @return 更新后的收藏
     */
    Favorite updateLastAccessTime(Long favoriteId);

    /**
     * 批量移动收藏到其他收藏夹
     * @param favoriteIds 收藏 ID 列表
     * @param targetFolderId 目标收藏夹 ID
     * @return 是否成功
     */
    boolean batchMoveFavorites(List<Long> favoriteIds, String targetFolderId);

    /**
     * 批量删除收藏
     * @param favoriteIds 收藏 ID 列表
     * @return 是否成功
     */
    boolean batchRemoveFavorites(List<Long> favoriteIds);

    /**
     * 获取用户的收藏统计数据
     * @param userEmail 用户邮箱
     * @return 统计数据
     */
    Map<String, Object> getUserFavoriteStatistics(String userEmail);

    /**
     * 获取用户最近收藏的资源
     * @param userEmail 用户邮箱
     * @param limit 限制返回数量
     * @return 最近收藏的资源列表
     */
    List<Favorite> getRecentFavorites(String userEmail, int limit);

    /**
     * 获取用户最常访问的收藏资源
     * @param userEmail 用户邮箱
     * @param limit 限制返回数量
     * @return 最常访问的收藏资源列表
     */
    List<Favorite> getMostAccessedFavorites(String userEmail, int limit);

    /**
     * 获取用户的收藏分页列表，支持DTO方式请求
     * 
     * @param userEmail 用户邮箱
     * @param queryDTO 查询参数
     * @return 收藏分页数据Map
     */
    Map<String, Object> getUserFavoritesPage(String userEmail, Map<String, Object> queryParams);
}
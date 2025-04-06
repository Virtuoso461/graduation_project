package com.example.backend.service;

import com.example.backend.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 资源服务接口
 */
public interface ResourceService {
    
    /**
     * 获取资源列表（分页）
     * 
     * @param filters 过滤条件
     * @param pageable 分页参数
     * @return 资源分页结果
     */
    Page<Resource> getResources(Map<String, Object> filters, Pageable pageable);
    
    /**
     * 根据ID获取资源
     * 
     * @param id 资源ID
     * @return 资源详情
     */
    Resource getResourceById(Long id);
    
    /**
     * 创建新资源
     * 
     * @param resource 资源信息
     * @return 创建后的资源
     */
    Resource createResource(Resource resource);
    
    /**
     * 更新资源
     * 
     * @param id 资源ID
     * @param resource 资源信息
     * @return 更新后的资源
     */
    Resource updateResource(Long id, Resource resource);
    
    /**
     * 删除资源
     * 
     * @param id 资源ID
     * @return 操作是否成功
     */
    boolean deleteResource(Long id);
    
    /**
     * 获取热门资源
     * 
     * @param limit 数量限制
     * @return 热门资源列表
     */
    List<Resource> getHotResources(int limit);
    
    /**
     * 获取最新资源
     * 
     * @param limit 数量限制
     * @return 最新资源列表
     */
    List<Resource> getLatestResources(int limit);
    
    /**
     * 获取资源分类列表
     * 
     * @return 分类列表
     */
    List<String> getResourceCategories();
    
    /**
     * 获取资源类型列表
     * 
     * @return 类型列表
     */
    List<String> getResourceTypes();
    
    /**
     * 根据创建者获取资源
     * 
     * @param creatorEmail 创建者邮箱
     * @return 资源列表
     */
    List<Resource> getResourcesByCreator(String creatorEmail);
    
    /**
     * 根据课程ID获取资源
     * 
     * @param courseId 课程ID
     * @return 资源列表
     */
    List<Resource> getResourcesByCourse(Long courseId);
    
    /**
     * 获取资源下载URL
     * 
     * @param id 资源ID
     * @return 下载URL
     */
    String getResourceDownloadUrl(Long id);
    
    /**
     * 增加资源下载次数
     * 
     * @param id 资源ID
     * @return 是否成功
     */
    boolean incrementDownloadCount(Long id);
    
    /**
     * 增加资源查看次数
     * 
     * @param id 资源ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);
    
    /**
     * 搜索资源
     * 
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 资源分页结果
     */
    Page<Resource> searchResources(String keyword, Pageable pageable);
} 
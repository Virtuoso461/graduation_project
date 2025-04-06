package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Resource;
import com.example.backend.entity.Course;
import com.example.backend.service.CourseService;
import com.example.backend.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习资源控制器
 */
@RestController
@RequestMapping("/api")
public class ResourceController {
    
    private final ResourceService resourceService;
    private final CourseService courseService;
    
    @Autowired
    public ResourceController(ResourceService resourceService, CourseService courseService) {
        this.resourceService = resourceService;
        this.courseService = courseService;
    }
    
    /**
     * 获取当前登录用户
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    /**
     * 获取资源列表
     */
    @GetMapping("/resources")
    public Result<Map<String, Object>> getResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        // 创建分页参数
        Sort sort = Sort.by(
                direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 构建过滤条件
        Map<String, Object> filters = new HashMap<>();
        if (type != null && !type.isEmpty()) {
            filters.put("type", type);
        }
        if (category != null && !category.isEmpty()) {
            filters.put("category", category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            filters.put("keyword", keyword);
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
     */
    @GetMapping("/resources/{id}")
    public Result<Resource> getResourceDetail(@PathVariable Long id) {
        Resource resource = resourceService.getResourceById(id);
        if (resource == null) {
            return Result.failed("资源不存在");
        }
        return Result.success(resource);
    }
    
    /**
     * 获取资源分类
     */
    @GetMapping("/resources/categories")
    public Result<List<String>> getResourceCategories() {
        List<String> categories = resourceService.getResourceCategories();
        return Result.success(categories);
    }
    
    /**
     * 获取资源类型
     */
    @GetMapping("/resources/types")
    public Result<List<String>> getResourceTypes() {
        List<String> types = resourceService.getResourceTypes();
        return Result.success(types);
    }
    
    /**
     * 创建资源（教师）
     */
    @PostMapping("/resources")
    public Result<Resource> createResource(@RequestBody Resource resource) {
        String userEmail = getCurrentUserEmail();
        resource.setCreatorEmail(userEmail);
        
        Resource createdResource = resourceService.createResource(resource);
        return Result.success(createdResource);
    }
    
    /**
     * 更新资源（教师）
     */
    @PutMapping("/resources/{id}")
    public Result<Resource> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        String userEmail = getCurrentUserEmail();
        
        // 检查是否是资源创建者
        Resource existingResource = resourceService.getResourceById(id);
        if (existingResource == null) {
            return Result.failed("资源不存在");
        }
        
        if (!existingResource.getCreatorEmail().equals(userEmail)) {
            return Result.failed("无权限修改他人创建的资源");
        }
        
        resource.setId(id);
        Resource updatedResource = resourceService.updateResource(id, resource);
        return Result.success(updatedResource);
    }
    
    /**
     * 删除资源（教师）
     */
    @DeleteMapping("/resources/{id}")
    public Result<Boolean> deleteResource(@PathVariable Long id) {
        String userEmail = getCurrentUserEmail();
        
        // 检查是否是资源创建者
        Resource existingResource = resourceService.getResourceById(id);
        if (existingResource == null) {
            return Result.failed("资源不存在");
        }
        
        if (!existingResource.getCreatorEmail().equals(userEmail)) {
            return Result.failed("无权限删除他人创建的资源");
        }
        
        resourceService.deleteResource(id);
        return Result.success(true);
    }
    
    /**
     * 获取热门资源
     */
    @GetMapping("/resources/hot")
    public Result<List<Resource>> getHotResources(@RequestParam(defaultValue = "5") int limit) {
        List<Resource> hotResources = resourceService.getHotResources(limit);
        return Result.success(hotResources);
    }
    
    /**
     * 获取最新资源
     */
    @GetMapping("/resources/latest")
    public Result<List<Resource>> getLatestResources(@RequestParam(defaultValue = "5") int limit) {
        List<Resource> latestResources = resourceService.getLatestResources(limit);
        return Result.success(latestResources);
    }
    
    /**
     * 获取教师上传的资源
     */
    @GetMapping("/resources/creator")
    public Result<List<Resource>> getCreatorResources() {
        String creatorEmail = getCurrentUserEmail();
        List<Resource> resources = resourceService.getResourcesByCreator(creatorEmail);
        return Result.success(resources);
    }
    
    /**
     * 下载资源（增加下载次数）
     */
    @PostMapping("/resources/{id}/download")
    public Result<String> downloadResource(@PathVariable Long id) {
        String userEmail = getCurrentUserEmail();
        
        String downloadUrl = resourceService.getResourceDownloadUrl(id);
        
        // 更新下载次数
        resourceService.incrementDownloadCount(id);
        
        return Result.success(downloadUrl);
    }
    
    /**
     * 根据课程ID获取相关资源
     */
    @GetMapping("/resources/course/{courseId}")
    public Result<List<Resource>> getCourseResources(@PathVariable Long courseId) {
        List<Resource> resources = resourceService.getResourcesByCourse(courseId);
        return Result.success(resources);
    }
    
    /**
     * 获取所有课程列表
     * 
     * @return 所有课程列表
     */
    @GetMapping("/courses")
    public Result<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseService.getCourses(null);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.failed("获取课程列表失败: " + e.getMessage());
        }
    }
} 
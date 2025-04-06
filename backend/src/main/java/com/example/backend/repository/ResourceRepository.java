package com.example.backend.repository;

import com.example.backend.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资源数据访问接口
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    
    /**
     * 根据资源类型查询
     */
    Page<Resource> findByResourceType(String resourceType, Pageable pageable);
    
    /**
     * 根据分类查询
     */
    Page<Resource> findByCategory(String category, Pageable pageable);
    
    /**
     * 根据标题或描述模糊查询
     */
    @Query("SELECT r FROM Resource r WHERE r.title LIKE %:keyword% OR r.description LIKE %:keyword%")
    Page<Resource> searchByKeyword(String keyword, Pageable pageable);
    
    /**
     * 根据创建者查询
     */
    List<Resource> findByCreatorEmail(String creatorEmail);
    
    /**
     * 根据课程ID查询
     */
    List<Resource> findByCourseId(Long courseId);
    
    /**
     * 获取热门资源（按下载次数排序）
     */
    @Query("SELECT r FROM Resource r ORDER BY r.downloadCount DESC")
    List<Resource> findHotResources(Pageable pageable);
    
    /**
     * 获取最新资源（按创建时间排序）
     */
    @Query("SELECT r FROM Resource r ORDER BY r.createTime DESC")
    List<Resource> findLatestResources(Pageable pageable);
    
    /**
     * 获取所有资源分类
     */
    @Query("SELECT DISTINCT r.category FROM Resource r WHERE r.category IS NOT NULL")
    List<String> findAllCategories();
    
    /**
     * 获取所有资源类型
     */
    @Query("SELECT DISTINCT r.resourceType FROM Resource r WHERE r.resourceType IS NOT NULL")
    List<String> findAllResourceTypes();
} 
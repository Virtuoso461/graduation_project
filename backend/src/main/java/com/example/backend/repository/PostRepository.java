package com.example.backend.repository;

import com.example.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 根据分类查询帖子
    Page<Post> findByCategory(String category, Pageable pageable);
    
    // 根据作者邮箱查询帖子
    Page<Post> findByAuthorEmail(String authorEmail, Pageable pageable);
    
    // 搜索帖子（标题或内容包含关键词）
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);
    
    // 获取热门话题（按回复数排序）
    @Query("SELECT p FROM Post p ORDER BY p.replies DESC")
    List<Post> findHotTopics(Pageable pageable);
    
    // 获取最新帖子
    Page<Post> findAllByOrderByCreateTimeDesc(Pageable pageable);
    
    // 组合查询（分类+关键词）
    @Query("SELECT p FROM Post p WHERE (:category IS NULL OR p.category = :category) AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findPostsByFilter(
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );
} 
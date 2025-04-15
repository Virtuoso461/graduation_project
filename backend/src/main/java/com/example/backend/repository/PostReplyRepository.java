package com.example.backend.repository;

import com.example.backend.entity.PostReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostReplyRepository extends JpaRepository<PostReply, Long> {

    // 根据帖子ID查询所有一级回复（没有父回复的）
    List<PostReply> findByPostIdAndParentIdIsNullOrderByCreateTimeAsc(Long postId);

    // 根据父回复ID查询所有子回复
    List<PostReply> findByParentIdOrderByCreateTimeAsc(Long parentId);

    // 根据帖子ID查询所有回复，分页
    Page<PostReply> findByPostIdOrderByCreateTimeAsc(Long postId, Pageable pageable);

    // 根据用户邮箱查询所有回复
    Page<PostReply> findByAuthorEmailOrderByCreateTimeDesc(String authorEmail, Pageable pageable);

    // 根据用户邮箱查询所有回复（用于实现getRepliesByAuthor方法）
    Page<PostReply> findByAuthorEmail(String authorEmail, Pageable pageable);

    // 统计某个帖子的回复数
    long countByPostId(Long postId);

    // 根据帖子ID删除所有回复
    @Modifying
    @Transactional
    void deleteByPostId(Long postId);

    // 根据父回复ID查询所有子回复（不排序）
    List<PostReply> findByParentId(Long parentId);

    // 根据帖子ID查询所有回复（不分页）
    List<PostReply> findByPostIdOrderByCreateTimeAsc(Long postId);
}
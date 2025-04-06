package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Post;
import com.example.backend.entity.PostReply;
import com.example.backend.entity.Profile;
import com.example.backend.entity.User;
import com.example.backend.service.CommunityService;
import com.example.backend.service.ProfileService;
import com.example.backend.service.UserService;
import com.example.backend.vo.ProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区功能控制器
 */
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProfileService profileService;
    
    /**
     * 获取当前登录用户
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    /**
     * 获取帖子列表
     */
    @GetMapping("/posts")
    public Result<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sortBy) {
        
        // 创建分页参数
        Sort sort = Sort.by(Sort.Direction.DESC, 
                sortBy.equals("latest") ? "createTime" : 
                sortBy.equals("mostReplies") ? "replies" : 
                sortBy.equals("mostViews") ? "views" : "createTime");
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 构建过滤条件
        Map<String, Object> filters = new HashMap<>();
        if (category != null && !category.isEmpty()) {
            filters.put("category", category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            filters.put("keyword", keyword);
        }
        
        // 查询帖子
        Page<Post> postsPage = communityService.getPosts(filters, pageable);
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsPage.getContent());
        response.put("currentPage", postsPage.getNumber());
        response.put("totalItems", postsPage.getTotalElements());
        response.put("totalPages", postsPage.getTotalPages());
        
        return Result.success(response);
    }
    
    /**
     * 获取热门话题
     */
    @GetMapping("/hot-topics")
    public Result<List<Post>> getHotTopics(@RequestParam(defaultValue = "5") int limit) {
        List<Post> hotTopics = communityService.getHotTopics(limit);
        return Result.success(hotTopics);
    }
    
    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{postId}")
    public Result<Post> getPost(@PathVariable Long postId) {
        Post post = communityService.getPost(postId);
        if (post != null) {
            // 增加浏览量
            communityService.incrementViews(postId);
            return Result.success(post);
        }
        return Result.failed("帖子不存在");
    }
    
    /**
     * 创建帖子
     */
    @PostMapping("/posts")
    public Result<Post> createPost(@RequestBody Post post) {
        String userEmail = getCurrentUserEmail();
        
        // 设置作者信息
        post.setAuthorEmail(userEmail);
        
        // 获取用户资料中的名称和头像
        ProfileVO profileVO = profileService.getProfileByEmail(userEmail);
        if (profileVO != null) {
            post.setAuthorName(profileVO.getName() != null ? profileVO.getName() : profileVO.getUsername());
            post.setAuthorAvatar(profileVO.getAvatar());
        }
        
        // 设置创建时间
        post.setCreateTime(LocalDateTime.now());
        
        // 初始化统计数据
        post.setViews(0);
        post.setLikes(0);
        post.setReplies(0);
        
        Post createdPost = communityService.createPost(post);
        return Result.success(createdPost);
    }
    
    /**
     * 更新帖子
     */
    @PutMapping("/posts/{postId}")
    public Result<Post> updatePost(@PathVariable Long postId, @RequestBody Post post) {
        String userEmail = getCurrentUserEmail();
        Post existingPost = communityService.getPost(postId);
        
        if (existingPost == null) {
            return Result.failed("帖子不存在");
        }
        
        // 检查是否是帖子作者
        if (!existingPost.getAuthorEmail().equals(userEmail)) {
            return Result.failed("无权限修改他人帖子");
        }
        
        // 设置更新时间
        post.setUpdateTime(LocalDateTime.now());
        
        Post updatedPost = communityService.updatePost(postId, post);
        return Result.success(updatedPost);
    }
    
    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{postId}")
    public Result<Boolean> deletePost(@PathVariable Long postId) {
        String userEmail = getCurrentUserEmail();
        Post existingPost = communityService.getPost(postId);
        
        if (existingPost == null) {
            return Result.failed("帖子不存在");
        }
        
        // 检查是否是帖子作者
        if (!existingPost.getAuthorEmail().equals(userEmail)) {
            return Result.failed("无权限删除他人帖子");
        }
        
        boolean success = communityService.deletePost(postId);
        return success ? Result.success(true) : Result.failed("删除失败");
    }
    
    /**
     * 点赞/取消点赞帖子
     */
    @PostMapping("/posts/{postId}/like")
    public Result<Integer> likePost(@PathVariable Long postId, @RequestParam boolean like) {
        Post post = communityService.getPost(postId);
        if (post == null) {
            return Result.failed("帖子不存在");
        }
        
        // 增加或减少点赞数
        communityService.updateLikes(postId, like ? 1 : -1);
        
        // 返回更新后的点赞数
        post = communityService.getPost(postId);
        return Result.success(post.getLikes());
    }
    
    /**
     * 获取帖子回复
     */
    @GetMapping("/posts/{postId}/replies")
    public Result<List<Map<String, Object>>> getReplies(@PathVariable Long postId) {
        List<Map<String, Object>> replies = communityService.getRepliesWithChildren(postId);
        return Result.success(replies);
    }
    
    /**
     * 添加回复
     */
    @PostMapping("/posts/{postId}/replies")
    public Result<PostReply> addReply(
            @PathVariable Long postId,
            @RequestBody PostReply reply,
            @RequestParam(required = false) Long parentId) {
        
        String userEmail = getCurrentUserEmail();
        Post post = communityService.getPost(postId);
        
        if (post == null) {
            return Result.failed("帖子不存在");
        }
        
        // 设置回复信息
        reply.setPostId(postId);
        reply.setAuthorEmail(userEmail);
        reply.setParentId(parentId);
        
        // 获取用户资料中的名称和头像
        ProfileVO profileVO = profileService.getProfileByEmail(userEmail);
        if (profileVO != null) {
            reply.setAuthorName(profileVO.getName() != null ? profileVO.getName() : profileVO.getUsername());
            reply.setAuthorAvatar(profileVO.getAvatar());
        }
        // 设置创建时间
        reply.setCreateTime(LocalDateTime.now());
        
        // 保存回复
        PostReply createdReply = communityService.addReply(reply);
        
        // 更新帖子的回复数
        post.setReplies(post.getReplies() + 1);
        communityService.updatePost(postId, post);
        
        return Result.success(createdReply);
    }
    
    /**
     * 删除回复
     */
    @DeleteMapping("/replies/{replyId}")
    public Result<Boolean> deleteReply(@PathVariable Long replyId) {
        String userEmail = getCurrentUserEmail();
        
        // 获取回复详情
        PostReply reply = communityService.getReplies(null, PageRequest.of(0, 1))
                .getContent().stream()
                .filter(r -> r.getId().equals(replyId))
                .findFirst().orElse(null);
        
        if (reply == null) {
            return Result.failed("回复不存在");
        }
        
        // 检查是否是回复作者
        if (!reply.getAuthorEmail().equals(userEmail)) {
            return Result.failed("无权限删除他人回复");
        }
        
        boolean success = communityService.deleteReply(replyId);
        
        if (success) {
            // 更新帖子的回复数
            Post post = communityService.getPost(reply.getPostId());
            if (post != null) {
                post.setReplies(post.getReplies() - 1);
                communityService.updatePost(post.getId(), post);
            }
        }
        
        return success ? Result.success(true) : Result.failed("删除失败");
    }
    
    /**
     * 点赞/取消点赞回复
     */
    @PostMapping("/replies/{replyId}/like")
    public Result<Integer> likeReply(@PathVariable Long replyId, @RequestParam boolean like) {
        // 获取回复详情
        PostReply reply = communityService.getReplies(null, PageRequest.of(0, 1))
                .getContent().stream()
                .filter(r -> r.getId().equals(replyId))
                .findFirst().orElse(null);
        
        if (reply == null) {
            return Result.failed("回复不存在");
        }
        
        // 增加或减少点赞数
        communityService.updateReplyLikes(replyId, like ? 1 : -1);
        
        // 获取更新后的点赞数
        reply = communityService.getReplies(null, PageRequest.of(0, 1))
                .getContent().stream()
                .filter(r -> r.getId().equals(replyId))
                .findFirst().orElse(null);
        
        return Result.success(reply != null ? reply.getLikes() : 0);
    }
} 
package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.PostQueryDTO;
import com.example.backend.dto.ReplyDTO;
import com.example.backend.entity.Post;
import com.example.backend.entity.PostReply;
import com.example.backend.service.CommunityService;
import com.example.backend.service.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生社区控制器
 * 提供学生社区互动相关的API
 */
@RestController
@RequestMapping("/api/student/community")
public class StudentCommunityController {

    private final CommunityService communityService;
    private final ProfileService profileService;

    public StudentCommunityController(CommunityService communityService, ProfileService profileService) {
        this.communityService = communityService;
        this.profileService = profileService;
    }

    /**
     * 获取当前登录用户的邮箱
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 获取帖子列表
     *
     * @param queryDTO 查询参数DTO
     * @return 帖子列表
     */
    @PostMapping("/posts/query")
    public Result<Map<String, Object>> getPosts(@RequestBody PostQueryDTO queryDTO) {
        Map<String, Object> response = communityService.getPostsPage(
                queryDTO.getPage(), 
                queryDTO.getSize(), 
                queryDTO.getCategory(), 
                queryDTO.getKeyword(), 
                queryDTO.getSortBy()
        );
        return Result.success(response);
    }

    /**
     * 获取帖子详情
     *
     * @param postId 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/posts/{postId}")
    public Result<Map<String, Object>> getPostDetail(@PathVariable Long postId) {
        Map<String, Object> response = communityService.getPostWithReplies(postId);
        if (response == null) {
            return Result.failed("帖子不存在");
        }
        return Result.success(response);
    }

    /**
     * 创建帖子
     *
     * @param post 帖子对象
     * @return 创建后的帖子
     */
    @PostMapping("/posts")
    public Result<Post> createPost(@RequestBody Post post) {
        String userEmail = getCurrentUserEmail();
        
        // 设置作者信息
        post.setAuthorEmail(userEmail);
        
        // 获取用户资料中的名称和头像
        post = communityService.preparePost(post, userEmail);
        
        Post createdPost = communityService.createPost(post);
        return Result.success(createdPost, "帖子创建成功");
    }

    /**
     * 更新帖子
     *
     * @param postId 帖子ID
     * @param post 帖子对象
     * @return 更新后的帖子
     */
    @PutMapping("/posts/{postId}")
    public Result<Post> updatePost(@PathVariable Long postId, @RequestBody Post post) {
        String userEmail = getCurrentUserEmail();
        
        // 获取原帖子
        Post existingPost = communityService.getPost(postId);
        if (existingPost == null) {
            return Result.failed("帖子不存在");
        }
        
        // 验证是否为作者
        if (!existingPost.getAuthorEmail().equals(userEmail)) {
            return Result.forbidden("只有作者才能更新帖子");
        }
        
        // 设置更新信息
        post = communityService.preparePostUpdate(post, existingPost);
        
        Post updatedPost = communityService.updatePost(postId, post);
        return Result.success(updatedPost, "帖子更新成功");
    }

    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @return 删除结果
     */
    @DeleteMapping("/posts/{postId}")
    public Result<Boolean> deletePost(@PathVariable Long postId) {
        String userEmail = getCurrentUserEmail();
        
        // 获取原帖子
        Post existingPost = communityService.getPost(postId);
        if (existingPost == null) {
            return Result.failed("帖子不存在");
        }
        
        // 验证是否为作者
        if (!existingPost.getAuthorEmail().equals(userEmail)) {
            return Result.forbidden("只有作者才能删除帖子");
        }
        
        boolean success = communityService.deletePost(postId);
        return success ? Result.success(true, "帖子删除成功") : Result.failed("帖子删除失败");
    }

    /**
     * 回复帖子
     *
     * @param postId 帖子ID
     * @param replyDTO 回复DTO
     * @return 创建后的回复
     */
    @PostMapping("/posts/{postId}/replies")
    public Result<PostReply> addReply(
            @PathVariable Long postId,
            @RequestBody ReplyDTO replyDTO) {
        
        String userEmail = getCurrentUserEmail();
        Post post = communityService.getPost(postId);
        
        if (post == null) {
            return Result.failed("帖子不存在");
        }
        
        // 准备回复数据
        PostReply reply = new PostReply();
        reply.setContent(replyDTO.getContent());
        reply = communityService.preparePostReply(postId, reply, userEmail, replyDTO.getParentId());
        
        // 保存回复
        PostReply createdReply = communityService.addReply(reply);
        
        // 更新帖子回复数
        post.setReplies(post.getReplies() + 1);
        communityService.updatePost(postId, post);
        
        return Result.success(createdReply, "回复成功");
    }

    /**
     * 删除回复
     *
     * @param replyId 回复ID
     * @return 删除结果
     */
    @DeleteMapping("/replies/{replyId}")
    public Result<Boolean> deleteReply(@PathVariable Long replyId) {
        String userEmail = getCurrentUserEmail();
        
        // 使用service层获取回复详情
        PostReply reply = communityService.getReplyById(replyId);
        
        if (reply == null) {
            return Result.failed("回复不存在");
        }
        
        // 验证是否为作者
        if (!reply.getAuthorEmail().equals(userEmail)) {
            return Result.forbidden("只有作者才能删除回复");
        }
        
        boolean success = communityService.deleteReply(replyId);
        
        // 如果删除成功，更新帖子回复数
        if (success) {
            Post post = communityService.getPost(reply.getPostId());
            if (post != null) {
                post.setReplies(Math.max(0, post.getReplies() - 1));
                communityService.updatePost(post.getId(), post);
            }
        }
        
        return success ? Result.success(true, "回复删除成功") : Result.failed("回复删除失败");
    }

    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @return 点赞结果
     */
    @PostMapping("/posts/{postId}/like")
    public Result<Integer> likePost(@PathVariable Long postId) {
        Post post = communityService.getPost(postId);
        if (post == null) {
            return Result.failed("帖子不存在");
        }
        
        // 增加点赞数
        communityService.updateLikes(postId, 1);
        
        // 返回更新后的点赞数
        post = communityService.getPost(postId);
        return Result.success(post.getLikes(), "点赞成功");
    }

    /**
     * 取消点赞帖子
     *
     * @param postId 帖子ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/posts/{postId}/like")
    public Result<Integer> unlikePost(@PathVariable Long postId) {
        Post post = communityService.getPost(postId);
        if (post == null) {
            return Result.failed("帖子不存在");
        }
        
        // 减少点赞数
        communityService.updateLikes(postId, -1);
        
        // 返回更新后的点赞数
        post = communityService.getPost(postId);
        return Result.success(post.getLikes(), "取消点赞成功");
    }

    /**
     * 获取我的帖子
     *
     * @param queryDTO 查询参数DTO
     * @return 我的帖子列表
     */
    @PostMapping("/my-posts")
    public Result<Map<String, Object>> getMyPosts(@RequestBody PostQueryDTO queryDTO) {
        String userEmail = getCurrentUserEmail();
        Map<String, Object> response = communityService.getMyPostsPage(userEmail, queryDTO.getPage(), queryDTO.getSize());
        return Result.success(response);
    }

    /**
     * 获取我的回复
     *
     * @param queryDTO 查询参数DTO
     * @return 我的回复列表
     */
    @PostMapping("/my-replies")
    public Result<Map<String, Object>> getMyReplies(@RequestBody PostQueryDTO queryDTO) {
        String userEmail = getCurrentUserEmail();
        Map<String, Object> response = communityService.getMyRepliesPage(userEmail, queryDTO.getPage(), queryDTO.getSize());
        return Result.success(response);
    }

    /**
     * 获取热门帖子
     *
     * @param queryDTO 查询参数DTO
     * @return 热门帖子列表
     */
    @PostMapping("/hot-posts")
    public Result<List<Post>> getHotPosts(@RequestBody PostQueryDTO queryDTO) {
        List<Post> hotPosts = communityService.getHotTopics(queryDTO.getLimit());
        return Result.success(hotPosts);
    }

    /**
     * 获取社区分类
     *
     * @return 社区分类列表
     */
    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = communityService.getCategories();
        return Result.success(categories);
    }
}

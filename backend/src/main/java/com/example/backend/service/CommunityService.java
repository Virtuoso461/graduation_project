package com.example.backend.service;

import com.example.backend.entity.Post;
import com.example.backend.entity.PostReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 社区服务接口
 */
public interface CommunityService {
    
    /**
     * 创建帖子
     * @param post 帖子对象
     * @return 创建后的帖子
     */
    Post createPost(Post post);
    
    /**
     * 更新帖子
     * @param postId 帖子ID
     * @param post 帖子更新数据
     * @return 更新后的帖子
     */
    Post updatePost(Long postId, Post post);
    
    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情
     */
    Post getPost(Long postId);
    
    /**
     * 删除帖子
     * @param postId 帖子ID
     * @return 是否删除成功
     */
    boolean deletePost(Long postId);
    
    /**
     * 增加帖子浏览量
     * @param postId 帖子ID
     */
    void incrementViews(Long postId);
    
    /**
     * 点赞/取消点赞帖子
     * @param postId 帖子ID
     * @param increment 增加或减少1
     */
    void updateLikes(Long postId, int increment);
    
    /**
     * 分页查询帖子列表
     * @param filters 过滤条件（类别、关键词等）
     * @param pageable 分页参数
     * @return 帖子分页数据
     */
    Page<Post> getPosts(Map<String, Object> filters, Pageable pageable);
    
    /**
     * 获取热门话题
     * @param limit 数量限制
     * @return 热门话题列表
     */
    List<Post> getHotTopics(int limit);
    
    /**
     * 添加回复
     * @param reply 回复对象
     * @return 创建后的回复
     */
    PostReply addReply(PostReply reply);
    
    /**
     * 获取帖子的所有回复
     * @param postId 帖子ID
     * @param pageable 分页参数
     * @return 回复分页数据
     */
    Page<PostReply> getReplies(Long postId, Pageable pageable);
    
    /**
     * 获取帖子的所有回复（包括层级关系）
     * @param postId 帖子ID
     * @return 带层级的回复列表
     */
    List<Map<String, Object>> getRepliesWithChildren(Long postId);
    
    /**
     * 删除回复
     * @param replyId 回复ID
     * @return 是否删除成功
     */
    boolean deleteReply(Long replyId);
    
    /**
     * 点赞/取消点赞回复
     * @param replyId 回复ID
     * @param increment 增加或减少1
     */
    void updateReplyLikes(Long replyId, int increment);
} 
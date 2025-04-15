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
     * 查询帖子列表并返回完整分页响应
     * @param page 页码
     * @param size 每页大小
     * @param category 分类
     * @param keyword 关键词
     * @param sortBy 排序方式
     * @return 包含帖子列表和分页信息的响应数据
     */
    Map<String, Object> getPostsPage(int page, int size, String category, String keyword, String sortBy);

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
     * 获取帖子详情并包含回复
     * @param postId 帖子ID
     * @return 包含帖子和回复的Map
     */
    Map<String, Object> getPostWithReplies(Long postId);

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

    /**
     * 根据作者邮箱获取帖子
     * @param authorEmail 作者邮箱
     * @param pageable 分页参数
     * @return 帖子分页数据
     */
    Page<Post> getPostsByAuthor(String authorEmail, Pageable pageable);

    /**
     * 获取当前用户的帖子分页
     * @param userEmail 用户邮箱
     * @param page 页码
     * @param size 每页大小
     * @return 包含用户帖子和分页信息的Map
     */
    Map<String, Object> getMyPostsPage(String userEmail, int page, int size);

    /**
     * 根据作者邮箱获取回复
     * @param authorEmail 作者邮箱
     * @param pageable 分页参数
     * @return 回复分页数据
     */
    Page<PostReply> getRepliesByAuthor(String authorEmail, Pageable pageable);
    
    /**
     * 获取当前用户的回复分页
     * @param userEmail 用户邮箱
     * @param page 页码
     * @param size 每页大小
     * @return 包含用户回复和分页信息的Map
     */
    Map<String, Object> getMyRepliesPage(String userEmail, int page, int size);
    
    /**
     * 获取社区分类
     * @return 分类列表
     */
    List<Map<String, Object>> getCategories();
    
    /**
     * 准备帖子回复所需数据
     * @param postId 帖子ID
     * @param reply 回复对象
     * @param userEmail 用户邮箱
     * @param parentId 父回复ID (可选)
     * @return 已准备好的回复对象
     */
    PostReply preparePostReply(Long postId, PostReply reply, String userEmail, Long parentId);
    
    /**
     * 准备新帖子数据
     * @param post 帖子对象
     * @param userEmail 用户邮箱
     * @return 已准备好的帖子对象
     */
    Post preparePost(Post post, String userEmail);
    
    /**
     * 准备更新帖子数据
     * @param post 更新的帖子数据
     * @param existingPost 现有的帖子数据
     * @return 已准备好更新的帖子对象
     */
    Post preparePostUpdate(Post post, Post existingPost);
    
    /**
     * 根据ID获取回复
     * @param replyId 回复ID
     * @return 回复对象
     */
    PostReply getReplyById(Long replyId);
}
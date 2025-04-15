package com.example.backend.service.impl;

import com.example.backend.entity.Post;
import com.example.backend.entity.PostReply;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.PostReplyRepository;
import com.example.backend.service.CommunityService;
import com.example.backend.service.ProfileService;
import com.example.backend.vo.ProfileVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {

    private final PostRepository postRepository;
    private final PostReplyRepository postReplyRepository;
    private final ProfileService profileService;

    public CommunityServiceImpl(PostRepository postRepository, PostReplyRepository postReplyRepository, ProfileService profileService) {
        this.postRepository = postRepository;
        this.postReplyRepository = postReplyRepository;
        this.profileService = profileService;
    }

    @Override
    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long postId, Post post) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("帖子不存在，ID: " + postId));

        // 更新帖子信息
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setCategory(post.getCategory());
        existingPost.setUpdateTime(post.getUpdateTime());
        existingPost.setImages(post.getImages());

        return postRepository.save(existingPost);
    }

    @Override
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    @Transactional
    public boolean deletePost(Long postId) {
        try {
            if (postRepository.existsById(postId)) {
                // 先删除帖子的所有回复
                postReplyRepository.deleteByPostId(postId);

                // 再删除帖子
                postRepository.deleteById(postId);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public void incrementViews(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("帖子不存在，ID: " + postId));
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void updateLikes(Long postId, int increment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("帖子不存在，ID: " + postId));

        int newLikes = post.getLikes() + increment;
        if (newLikes < 0) {
            newLikes = 0;
        }

        post.setLikes(newLikes);
        postRepository.save(post);
    }

    @Override
    public Page<Post> getPosts(Map<String, Object> filters, Pageable pageable) {
        String category = (String) filters.get("category");
        String keyword = (String) filters.get("keyword");

        if (category != null && keyword != null) {
            return postRepository.findPostsByFilter(category, keyword, pageable);
        } else if (category != null) {
            return postRepository.findByCategory(category, pageable);
        } else if (keyword != null) {
            return postRepository.searchPosts(keyword, pageable);
        } else {
            return postRepository.findAll(pageable);
        }
    }
    
    @Override
    public Map<String, Object> getPostsPage(int page, int size, String category, String keyword, String sortBy) {
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
        Page<Post> postsPage = getPosts(filters, pageable);
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsPage.getContent());
        response.put("currentPage", postsPage.getNumber());
        response.put("totalItems", postsPage.getTotalElements());
        response.put("totalPages", postsPage.getTotalPages());
        
        return response;
    }

    @Override
    public List<Post> getHotTopics(int limit) {
        return postRepository.findHotTopics(PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public PostReply addReply(PostReply reply) {
        return postReplyRepository.save(reply);
    }

    @Override
    public Page<PostReply> getReplies(Long postId, Pageable pageable) {
        if (postId != null) {
            return postReplyRepository.findByPostIdOrderByCreateTimeAsc(postId, pageable);
        }
        return postReplyRepository.findAll(pageable);
    }

    @Override
    public List<Map<String, Object>> getRepliesWithChildren(Long postId) {
        // 获取所有一级回复
        List<PostReply> parentReplies = postReplyRepository.findByPostIdAndParentIdIsNullOrderByCreateTimeAsc(postId);

        // 构建层级结构
        return parentReplies.stream().map(parent -> {
            Map<String, Object> replyWithChildren = new HashMap<>();
            replyWithChildren.put("reply", parent);

            // 获取子回复
            List<PostReply> children = postReplyRepository.findByParentIdOrderByCreateTimeAsc(parent.getId());
            replyWithChildren.put("children", children);

            return replyWithChildren;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getPostWithReplies(Long postId) {
        Post post = getPost(postId);
        if (post == null) {
            return null;
        }
        
        // 增加浏览量
        incrementViews(postId);
        
        // 获取帖子回复
        List<Map<String, Object>> replies = getRepliesWithChildren(postId);
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("replies", replies);
        
        return response;
    }

    @Override
    @Transactional
    public boolean deleteReply(Long replyId) {
        try {
            if (postReplyRepository.existsById(replyId)) {
                // 先删除所有子回复
                List<PostReply> childReplies = postReplyRepository.findByParentId(replyId);
                for (PostReply childReply : childReplies) {
                    postReplyRepository.deleteById(childReply.getId());
                }

                // 再删除当前回复
                postReplyRepository.deleteById(replyId);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public void updateReplyLikes(Long replyId, int increment) {
        PostReply reply = postReplyRepository.findById(replyId)
                .orElseThrow(() -> new NoSuchElementException("回复不存在，ID: " + replyId));

        int newLikes = reply.getLikes() + increment;
        if (newLikes < 0) {
            newLikes = 0;
        }

        reply.setLikes(newLikes);
        postReplyRepository.save(reply);
    }

    @Override
    public Page<Post> getPostsByAuthor(String authorEmail, Pageable pageable) {
        return postRepository.findByAuthorEmail(authorEmail, pageable);
    }
    
    @Override
    public Map<String, Object> getMyPostsPage(String userEmail, int page, int size) {
        // 创建分页参数
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 查询我的帖子
        Page<Post> postsPage = getPostsByAuthor(userEmail, pageable);
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsPage.getContent());
        response.put("currentPage", postsPage.getNumber());
        response.put("totalItems", postsPage.getTotalElements());
        response.put("totalPages", postsPage.getTotalPages());
        
        return response;
    }

    @Override
    public Page<PostReply> getRepliesByAuthor(String authorEmail, Pageable pageable) {
        return postReplyRepository.findByAuthorEmail(authorEmail, pageable);
    }
    
    @Override
    public Map<String, Object> getMyRepliesPage(String userEmail, int page, int size) {
        // 创建分页参数
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 查询我的回复
        Page<PostReply> repliesPage = getRepliesByAuthor(userEmail, pageable);
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("replies", repliesPage.getContent());
        response.put("currentPage", repliesPage.getNumber());
        response.put("totalItems", repliesPage.getTotalElements());
        response.put("totalPages", repliesPage.getTotalPages());
        
        return response;
    }
    
    @Override
    public List<Map<String, Object>> getCategories() {
        // 这里可以从数据库中获取分类，但为简化实现，直接返回固定分类
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[] categoryNames = {"讨论", "问答", "分享", "资源", "公告", "活动"};
        String[] categoryIcons = {"chat", "question", "share", "file", "announcement", "event"};
        String[] categoryColors = {"#4CAF50", "#2196F3", "#FF9800", "#9C27B0", "#F44336", "#009688"};
        
        for (int i = 0; i < categoryNames.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", i + 1);
            category.put("name", categoryNames[i]);
            category.put("icon", categoryIcons[i]);
            category.put("color", categoryColors[i]);
            categories.add(category);
        }
        
        return categories;
    }
    
    @Override
    public PostReply preparePostReply(Long postId, PostReply reply, String userEmail, Long parentId) {
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
        
        // 初始化点赞数
        reply.setLikes(0);
        
        return reply;
    }

    @Override
    public Post preparePost(Post post, String userEmail) {
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
        
        return post;
    }

    @Override
    public Post preparePostUpdate(Post post, Post existingPost) {
        // 设置更新时间
        post.setUpdateTime(LocalDateTime.now());
        
        // 保留原帖子的其他信息
        post.setId(existingPost.getId());
        post.setAuthorEmail(existingPost.getAuthorEmail());
        post.setAuthorName(existingPost.getAuthorName());
        post.setAuthorAvatar(existingPost.getAuthorAvatar());
        post.setCreateTime(existingPost.getCreateTime());
        post.setViews(existingPost.getViews());
        post.setLikes(existingPost.getLikes());
        post.setReplies(existingPost.getReplies());
        
        return post;
    }

    @Override
    public PostReply getReplyById(Long replyId) {
        return postReplyRepository.findById(replyId).orElse(null);
    }
}
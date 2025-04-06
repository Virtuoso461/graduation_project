package com.example.backend.service.impl;

import com.example.backend.entity.Post;
import com.example.backend.entity.PostReply;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.PostReplyRepository;
import com.example.backend.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostReplyRepository postReplyRepository;

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
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
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
    @Transactional
    public boolean deleteReply(Long replyId) {
        if (postReplyRepository.existsById(replyId)) {
            postReplyRepository.deleteById(replyId);
            return true;
        }
        return false;
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
}
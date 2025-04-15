package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.AIGuidanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI指导建议控制器
 * 提供AI学习指导和建议功能
 */
@RestController
@RequestMapping("/api/ai-guidance")
public class AIGuidanceController {

    @Autowired
    private AIGuidanceService aiGuidanceService;

    /**
     * 获取AI学习建议
     * 
     * @param question 学生提问
     * @return AI回答
     */
    @PostMapping("/ask")
    public Result<Map<String, Object>> getGuidance(@RequestBody Map<String, String> requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            String question = requestBody.get("question");
            if (question == null || question.trim().isEmpty()) {
                return Result.validateFailed("问题不能为空");
            }
            
            String answer = aiGuidanceService.getGuidance(userEmail, question);
            
            Map<String, Object> response = new HashMap<>();
            response.put("question", question);
            response.put("answer", answer);
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.failed("获取AI指导失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学习建议历史记录
     * 
     * @param limit 限制返回数量
     * @return 历史记录列表
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getGuidanceHistory(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            List<Map<String, Object>> history = aiGuidanceService.getGuidanceHistory(userEmail, limit);
            
            return Result.success(history);
        } catch (Exception e) {
            return Result.failed("获取历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学习建议主题
     * 
     * @return 主题列表
     */
    @GetMapping("/topics")
    public Result<List<String>> getGuidanceTopics() {
        try {
            List<String> topics = aiGuidanceService.getGuidanceTopics();
            
            return Result.success(topics);
        } catch (Exception e) {
            return Result.failed("获取主题列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据主题获取学习建议
     * 
     * @param topic 主题
     * @return 学习建议
     */
    @GetMapping("/topic/{topic}")
    public Result<Map<String, Object>> getGuidanceByTopic(@PathVariable String topic) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            Map<String, Object> guidance = aiGuidanceService.getGuidanceByTopic(userEmail, topic);
            
            return Result.success(guidance);
        } catch (Exception e) {
            return Result.failed("获取主题建议失败: " + e.getMessage());
        }
    }
}

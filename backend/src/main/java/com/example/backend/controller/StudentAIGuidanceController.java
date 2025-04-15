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
 * 学生AI指导建议控制器
 * 提供学生AI学习指导和建议功能
 */
@RestController
@RequestMapping("/api/student/ai-guidance")
public class StudentAIGuidanceController {

    private final AIGuidanceService aiGuidanceService;

    @Autowired
    public StudentAIGuidanceController(AIGuidanceService aiGuidanceService) {
        this.aiGuidanceService = aiGuidanceService;
    }

    /**
     * 获取当前登录用户的邮箱
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 提交问题
     *
     * @param requestBody 请求体，包含问题内容
     * @return AI回答
     */
    @PostMapping("/question")
    public Result<Map<String, Object>> submitQuestion(@RequestBody Map<String, String> requestBody) {
        try {
            String userEmail = getCurrentUserEmail();
            
            String question = requestBody.get("question");
            if (question == null || question.trim().isEmpty()) {
                return Result.validateFailed("问题不能为空");
            }
            
            // 可以添加主题字段，用于分类问题
            String topic = requestBody.get("topic");
            
            // 获取AI回答
            String answer = aiGuidanceService.getGuidance(userEmail, question);
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("question", question);
            response.put("answer", answer);
            response.put("timestamp", java.time.LocalDateTime.now());
            if (topic != null && !topic.isEmpty()) {
                response.put("topic", topic);
            }
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.failed("提交问题失败: " + e.getMessage());
        }
    }

    /**
     * 获取历史问答记录
     *
     * @param limit 限制返回数量
     * @return 历史记录列表
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getQuestionHistory(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            String userEmail = getCurrentUserEmail();
            
            // 获取历史记录
            List<Map<String, Object>> history = aiGuidanceService.getGuidanceHistory(userEmail, limit);
            
            return Result.success(history);
        } catch (Exception e) {
            return Result.failed("获取历史记录失败: " + e.getMessage());
        }
    }
}

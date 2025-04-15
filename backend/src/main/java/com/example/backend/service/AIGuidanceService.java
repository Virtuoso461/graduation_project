package com.example.backend.service;

import java.util.List;
import java.util.Map;

/**
 * AI指导建议服务接口
 */
public interface AIGuidanceService {
    
    /**
     * 获取AI学习建议
     * 
     * @param userEmail 用户邮箱
     * @param question 学生提问
     * @return AI回答
     */
    String getGuidance(String userEmail, String question);
    
    /**
     * 获取学习建议历史记录
     * 
     * @param userEmail 用户邮箱
     * @param limit 限制返回数量
     * @return 历史记录列表
     */
    List<Map<String, Object>> getGuidanceHistory(String userEmail, int limit);
    
    /**
     * 获取学习建议主题
     * 
     * @return 主题列表
     */
    List<String> getGuidanceTopics();
    
    /**
     * 根据主题获取学习建议
     * 
     * @param userEmail 用户邮箱
     * @param topic 主题
     * @return 学习建议
     */
    Map<String, Object> getGuidanceByTopic(String userEmail, String topic);
}

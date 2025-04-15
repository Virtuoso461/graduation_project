package com.example.backend.service.impl;

import com.example.backend.service.AIGuidanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI指导建议服务实现类
 */
@Service
public class AIGuidanceServiceImpl implements AIGuidanceService {
    
    // 模拟数据：用户的历史记录
    private final Map<String, List<Map<String, Object>>> userHistories = new HashMap<>();
    
    // 模拟数据：学习建议主题
    private final List<String> topics = Arrays.asList(
            "学习方法", "时间管理", "考试技巧", "记忆方法", "专注力提升",
            "笔记技巧", "阅读技巧", "写作技巧", "演讲技巧", "团队协作"
    );


    // 模拟数据：主题建议
    private final Map<String, String> topicGuidance = new HashMap<>();
    
    public AIGuidanceServiceImpl() {
        // 初始化主题建议
        topicGuidance.put("学习方法", "有效的学习方法包括：\n1. 分散学习：将学习内容分散到多个时间段，而不是一次性学习大量内容。\n2. 主动回忆：尝试回忆所学内容，而不是反复阅读。\n3. 教授他人：向他人解释所学内容，加深理解。\n4. 多样化学习：使用不同的学习资源和方法。\n5. 实践应用：将所学知识应用到实际问题中。");
        topicGuidance.put("时间管理", "有效的时间管理技巧包括：\n1. 设定明确的目标：知道你想要完成什么。\n2. 优先级排序：区分重要和紧急的任务。\n3. 使用时间块：将时间分配给特定的任务。\n4. 避免多任务处理：专注于一个任务，完成后再进行下一个。\n5. 定期回顾和调整：评估你的时间管理策略，并根据需要进行调整。");
        topicGuidance.put("考试技巧", "有效的考试技巧包括：\n1. 提前准备：不要临时抱佛脚。\n2. 了解考试格式：熟悉题型和评分标准。\n3. 时间管理：合理分配各部分的时间。\n4. 先易后难：先回答你确定的问题。\n5. 检查答案：留出时间检查你的答案。");
        topicGuidance.put("记忆方法", "有效的记忆方法包括：\n1. 间隔重复：定期复习所学内容。\n2. 联想记忆：将新信息与已知信息联系起来。\n3. 可视化：创建心理图像来帮助记忆。\n4. 记忆宫殿：将信息与特定位置联系起来。\n5. 分块：将大量信息分解成小块。");
        topicGuidance.put("专注力提升", "提升专注力的方法包括：\n1. 消除干扰：创建一个无干扰的学习环境。\n2. 番茄工作法：工作25分钟，休息5分钟。\n3. 正念练习：培养当下的意识。\n4. 充分休息：确保充足的睡眠和休息。\n5. 健康饮食和运动：保持身体健康。");
    }
    
    @Override
    public String getGuidance(String userEmail, String question) {
        // 这里可以集成实际的AI服务，如OpenAI API
        // 目前使用模拟数据
        
        // 保存到历史记录
        Map<String, Object> record = new HashMap<>();
        record.put("question", question);
        record.put("timestamp", LocalDateTime.now());
        
        // 根据问题关键词生成回答
        String answer = generateAnswer(question);
        record.put("answer", answer);
        
        // 添加到用户历史记录
        userHistories.computeIfAbsent(userEmail, k -> new ArrayList<>()).add(record);
        
        return answer;
    }
    
    @Override
    public List<Map<String, Object>> getGuidanceHistory(String userEmail, int limit) {
        List<Map<String, Object>> history = userHistories.getOrDefault(userEmail, new ArrayList<>());
        
        // 按时间倒序排序
        history.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("timestamp");
            LocalDateTime timeB = (LocalDateTime) b.get("timestamp");
            return timeB.compareTo(timeA);
        });
        
        // 限制返回数量
        return history.size() <= limit ? history : history.subList(0, limit);
    }
    
    @Override
    public List<String> getGuidanceTopics() {
        return topics;
    }
    
    @Override
    public Map<String, Object> getGuidanceByTopic(String userEmail, String topic) {
        Map<String, Object> result = new HashMap<>();
        result.put("topic", topic);
        
        // 获取主题建议
        String guidance = topicGuidance.getOrDefault(topic, "暂无该主题的建议");
        result.put("guidance", guidance);
        
        // 添加相关资源
        List<Map<String, Object>> resources = getResourcesByTopic(topic);
        result.put("resources", resources);
        
        return result;
    }
    
    /**
     * 根据问题生成回答
     * 
     * @param question 问题
     * @return 回答
     */
    private String generateAnswer(String question) {
        question = question.toLowerCase();
        
        if (question.contains("学习方法") || question.contains("如何学习")) {
            return "有效的学习方法包括：\n1. 分散学习：将学习内容分散到多个时间段，而不是一次性学习大量内容。\n2. 主动回忆：尝试回忆所学内容，而不是反复阅读。\n3. 教授他人：向他人解释所学内容，加深理解。\n4. 多样化学习：使用不同的学习资源和方法。\n5. 实践应用：将所学知识应用到实际问题中。";
        } else if (question.contains("时间管理") || question.contains("安排时间")) {
            return "有效的时间管理技巧包括：\n1. 设定明确的目标：知道你想要完成什么。\n2. 优先级排序：区分重要和紧急的任务。\n3. 使用时间块：将时间分配给特定的任务。\n4. 避免多任务处理：专注于一个任务，完成后再进行下一个。\n5. 定期回顾和调整：评估你的时间管理策略，并根据需要进行调整。";
        } else if (question.contains("考试") || question.contains("测试")) {
            return "有效的考试技巧包括：\n1. 提前准备：不要临时抱佛脚。\n2. 了解考试格式：熟悉题型和评分标准。\n3. 时间管理：合理分配各部分的时间。\n4. 先易后难：先回答你确定的问题。\n5. 检查答案：留出时间检查你的答案。";
        } else if (question.contains("记忆") || question.contains("遗忘")) {
            return "有效的记忆方法包括：\n1. 间隔重复：定期复习所学内容。\n2. 联想记忆：将新信息与已知信息联系起来。\n3. 可视化：创建心理图像来帮助记忆。\n4. 记忆宫殿：将信息与特定位置联系起来。\n5. 分块：将大量信息分解成小块。";
        } else if (question.contains("专注") || question.contains("注意力") || question.contains("集中精力")) {
            return "提升专注力的方法包括：\n1. 消除干扰：创建一个无干扰的学习环境。\n2. 番茄工作法：工作25分钟，休息5分钟。\n3. 正念练习：培养当下的意识。\n4. 充分休息：确保充足的睡眠和休息。\n5. 健康饮食和运动：保持身体健康。";
        } else {
            return "感谢您的提问。作为AI学习助手，我可以帮助您解答关于学习方法、时间管理、考试技巧、记忆方法、专注力提升等方面的问题。请尝试提出更具体的问题，我会尽力提供有用的建议。";
        }
    }
    
    /**
     * 获取主题相关资源
     * 
     * @param topic 主题
     * @return 资源列表
     */
    private List<Map<String, Object>> getResourcesByTopic(String topic) {
        List<Map<String, Object>> resources = new ArrayList<>();
        
        // 模拟数据
        if ("学习方法".equals(topic)) {
            Map<String, Object> resource1 = new HashMap<>();
            resource1.put("title", "如何学习：大脑的运作原理");
            resource1.put("type", "书籍");
            resource1.put("author", "Barbara Oakley");
            resources.add(resource1);
            
            Map<String, Object> resource2 = new HashMap<>();
            resource2.put("title", "高效学习的10个原则");
            resource2.put("type", "文章");
            resource2.put("url", "https://example.com/learning-principles");
            resources.add(resource2);
        } else if ("时间管理".equals(topic)) {
            Map<String, Object> resource1 = new HashMap<>();
            resource1.put("title", "番茄工作法：专注工作25分钟");
            resource1.put("type", "技巧");
            resource1.put("url", "https://example.com/pomodoro-technique");
            resources.add(resource1);
            
            Map<String, Object> resource2 = new HashMap<>();
            resource2.put("title", "时间管理矩阵：区分重要和紧急");
            resource2.put("type", "工具");
            resource2.put("url", "https://example.com/time-management-matrix");
            resources.add(resource2);
        }
        
        return resources;
    }
}

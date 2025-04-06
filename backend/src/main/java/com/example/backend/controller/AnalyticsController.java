package com.example.backend.controller;

import com.example.backend.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学习分析控制器
 * 提供学习数据分析相关的API接口
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    /**
     * 获取学习进度
     * 
     * @param email 用户邮箱
     * @return 学习进度数据
     */
    @GetMapping("/progress")
    public Result<Map<String, Object>> getLearningProgress(@RequestParam String email) {
        try {
            // 模拟学习进度数据
            Map<String, Object> progress = new HashMap<>();
            
            // 课程完成率
            progress.put("courseCompletionRate", 65); // 百分比
            
            // 总课程数量
            progress.put("totalCourses", 10);
            
            // 已完成课程数量
            progress.put("completedCourses", 3);
            
            // 进行中课程数量
            progress.put("inProgressCourses", 4);
            
            // 未开始课程数量
            progress.put("notStartedCourses", 3);
            
            // 课程进度详情
            List<Map<String, Object>> courseProgress = new ArrayList<>();
            
            Map<String, Object> course1 = new HashMap<>();
            course1.put("id", "1");
            course1.put("title", "Java编程基础");
            course1.put("progress", 75); // 百分比
            course1.put("status", "进行中");
            courseProgress.add(course1);
            
            Map<String, Object> course2 = new HashMap<>();
            course2.put("id", "2");
            course2.put("title", "Web开发进阶");
            course2.put("progress", 30); // 百分比
            course2.put("status", "进行中");
            courseProgress.add(course2);
            
            Map<String, Object> course3 = new HashMap<>();
            course3.put("id", "3");
            course3.put("title", "数据结构与算法");
            course3.put("progress", 0); // 百分比
            course3.put("status", "未开始");
            courseProgress.add(course3);
            
            Map<String, Object> course4 = new HashMap<>();
            course4.put("id", "4");
            course4.put("title", "Python入门教程");
            course4.put("progress", 100); // 百分比
            course4.put("status", "已完成");
            courseProgress.add(course4);
            
            progress.put("courseProgressDetails", courseProgress);
            
            return Result.success(progress);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
    
    /**
     * 获取学习时长统计
     * 
     * @param email 用户邮箱
     * @param period 时间段（week/month/year）
     * @return 学习时长统计数据
     */
    @GetMapping("/study-time")
    public Result<Map<String, Object>> getStudyTimeStats(
            @RequestParam String email,
            @RequestParam(defaultValue = "week") String period) {
        try {
            // 模拟学习时长数据
            Map<String, Object> studyTimeStats = new HashMap<>();
            
            // 总学习时长（小时）
            studyTimeStats.put("totalHours", 35.5);
            
            // 学习时长趋势
            List<Map<String, Object>> trend = new ArrayList<>();
            
            // 根据请求的时间段生成不同的趋势数据
            if ("week".equals(period)) {
                // 一周内的每日学习时长
                String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                double[] hours = {2.5, 1.8, 3.2, 1.5, 4.0, 2.0, 1.5};
                
                for (int i = 0; i < days.length; i++) {
                    Map<String, Object> day = new HashMap<>();
                    day.put("date", days[i]);
                    day.put("hours", hours[i]);
                    trend.add(day);
                }
            } else if ("month".equals(period)) {
                // 一个月内的每周学习时长
                String[] weeks = {"第1周", "第2周", "第3周", "第4周"};
                double[] hours = {10.5, 8.6, 9.2, 7.2};
                
                for (int i = 0; i < weeks.length; i++) {
                    Map<String, Object> week = new HashMap<>();
                    week.put("date", weeks[i]);
                    week.put("hours", hours[i]);
                    trend.add(week);
                }
            } else if ("year".equals(period)) {
                // 一年内的每月学习时长
                String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
                double[] hours = {20.5, 18.6, 25.2, 30.1, 28.5, 15.3, 10.2, 12.8, 22.5, 30.2, 35.5, 28.0};
                
                for (int i = 0; i < months.length; i++) {
                    Map<String, Object> month = new HashMap<>();
                    month.put("date", months[i]);
                    month.put("hours", hours[i]);
                    trend.add(month);
                }
            }
            
            studyTimeStats.put("trend", trend);
            
            // 学习时长分布（按课程）
            List<Map<String, Object>> distribution = new ArrayList<>();
            
            Map<String, Object> course1 = new HashMap<>();
            course1.put("id", "1");
            course1.put("title", "Java编程基础");
            course1.put("hours", 12.5);
            course1.put("percentage", 35.2); // 百分比
            distribution.add(course1);
            
            Map<String, Object> course2 = new HashMap<>();
            course2.put("id", "2");
            course2.put("title", "Web开发进阶");
            course2.put("hours", 8.3);
            course2.put("percentage", 23.4); // 百分比
            distribution.add(course2);
            
            Map<String, Object> course3 = new HashMap<>();
            course3.put("id", "4");
            course3.put("title", "Python入门教程");
            course3.put("hours", 14.7);
            course3.put("percentage", 41.4); // 百分比
            distribution.add(course3);
            
            studyTimeStats.put("distribution", distribution);
            
            return Result.success(studyTimeStats);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
    
    /**
     * 获取考试成绩统计
     * 
     * @param email 用户邮箱
     * @return 考试成绩统计数据
     */
    @GetMapping("/exam-scores")
    public Result<Map<String, Object>> getExamScoreStats(@RequestParam String email) {
        try {
            // 模拟考试成绩数据
            Map<String, Object> examScoreStats = new HashMap<>();
            
            // 平均分
            examScoreStats.put("averageScore", 85.6);
            
            // 最高分
            examScoreStats.put("highestScore", 98);
            
            // 最低分
            examScoreStats.put("lowestScore", 72);
            
            // 考试数量
            examScoreStats.put("examCount", 8);
            
            // 及格率
            examScoreStats.put("passRate", 100); // 百分比
            
            // 成绩趋势
            List<Map<String, Object>> trend = new ArrayList<>();
            
            String[] exams = {"Java基础测试", "Web开发测试", "数据库测试", "算法基础测试", 
                               "网络编程测试", "设计模式测试", "Python基础测试", "前端框架测试"};
            int[] scores = {86, 92, 78, 72, 85, 88, 93, 91};
            
            for (int i = 0; i < exams.length; i++) {
                Map<String, Object> exam = new HashMap<>();
                exam.put("name", exams[i]);
                exam.put("score", scores[i]);
                trend.add(exam);
            }
            
            examScoreStats.put("trend", trend);
            
            // 成绩分布
            Map<String, Integer> distribution = new HashMap<>();
            distribution.put("优秀(90-100)", 3);
            distribution.put("良好(80-89)", 3);
            distribution.put("中等(70-79)", 2);
            distribution.put("及格(60-69)", 0);
            distribution.put("不及格(<60)", 0);
            
            examScoreStats.put("distribution", distribution);
            
            return Result.success(examScoreStats);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
    
    /**
     * 获取知识点掌握情况
     * 
     * @param email 用户邮箱
     * @return 知识点掌握情况数据
     */
    @GetMapping("/knowledge-points")
    public Result<Map<String, Object>> getKnowledgePointMastery(@RequestParam String email) {
        try {
            // 模拟知识点掌握情况数据
            Map<String, Object> knowledgePointMastery = new HashMap<>();
            
            // 总知识点数量
            knowledgePointMastery.put("totalPoints", 120);
            
            // 已掌握知识点数量
            knowledgePointMastery.put("masteredPoints", 87);
            
            // 掌握率
            knowledgePointMastery.put("masteryRate", 72.5); // 百分比
            
            // 知识领域掌握情况
            List<Map<String, Object>> domainMastery = new ArrayList<>();
            
            Map<String, Object> domain1 = new HashMap<>();
            domain1.put("name", "Java编程");
            domain1.put("masteryRate", 85); // 百分比
            domain1.put("pointsCount", 35);
            domain1.put("masteredCount", 30);
            domainMastery.add(domain1);
            
            Map<String, Object> domain2 = new HashMap<>();
            domain2.put("name", "Web开发");
            domain2.put("masteryRate", 68); // 百分比
            domain2.put("pointsCount", 28);
            domain2.put("masteredCount", 19);
            domainMastery.add(domain2);
            
            Map<String, Object> domain3 = new HashMap<>();
            domain3.put("name", "数据库");
            domain3.put("masteryRate", 75); // 百分比
            domain3.put("pointsCount", 20);
            domain3.put("masteredCount", 15);
            domainMastery.add(domain3);
            
            Map<String, Object> domain4 = new HashMap<>();
            domain4.put("name", "算法");
            domain4.put("masteryRate", 62); // 百分比
            domain4.put("pointsCount", 25);
            domain4.put("masteredCount", 15);
            domainMastery.add(domain4);
            
            Map<String, Object> domain5 = new HashMap<>();
            domain5.put("name", "Python");
            domain5.put("masteryRate", 70); // 百分比
            domain5.put("pointsCount", 12);
            domain5.put("masteredCount", 8);
            domainMastery.add(domain5);
            
            knowledgePointMastery.put("domainMastery", domainMastery);
            
            // 推荐学习的知识点
            List<Map<String, Object>> recommendedPoints = new ArrayList<>();
            
            Map<String, Object> point1 = new HashMap<>();
            point1.put("id", "kp-1");
            point1.put("name", "设计模式 - 工厂模式");
            point1.put("domain", "Java编程");
            point1.put("difficulty", "中级");
            recommendedPoints.add(point1);
            
            Map<String, Object> point2 = new HashMap<>();
            point2.put("id", "kp-2");
            point2.put("name", "JavaScript - Promise");
            point2.put("domain", "Web开发");
            point2.put("difficulty", "中级");
            recommendedPoints.add(point2);
            
            Map<String, Object> point3 = new HashMap<>();
            point3.put("id", "kp-3");
            point3.put("name", "数据库索引优化");
            point3.put("domain", "数据库");
            point3.put("difficulty", "高级");
            recommendedPoints.add(point3);
            
            Map<String, Object> point4 = new HashMap<>();
            point4.put("id", "kp-4");
            point4.put("name", "动态规划算法");
            point4.put("domain", "算法");
            point4.put("difficulty", "高级");
            recommendedPoints.add(point4);
            
            Map<String, Object> point5 = new HashMap<>();
            point5.put("id", "kp-5");
            point5.put("name", "Python - 装饰器");
            point5.put("domain", "Python");
            point5.put("difficulty", "中级");
            recommendedPoints.add(point5);
            
            knowledgePointMastery.put("recommendedPoints", recommendedPoints);
            
            return Result.success(knowledgePointMastery);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
} 
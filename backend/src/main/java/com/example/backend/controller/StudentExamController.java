package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.common.ResultCode;
import com.example.backend.dto.ExamAnswerDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;
import com.example.backend.service.ExamAnswerService;
import com.example.backend.service.ExamService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生考试控制器
 * 处理学生考试相关的请求
 */
@RestController
@RequestMapping("/api/student/exams")
public class StudentExamController {

    private final ExamService examService;
    private final ExamAnswerService examAnswerService;
    private final UserService userService;

    @Autowired
    public StudentExamController(ExamService examService, ExamAnswerService examAnswerService, UserService userService) {
        this.examService = examService;
        this.examAnswerService = examAnswerService;
        this.userService = userService;
    }

    /**
     * 获取当前登录用户
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        return userService.findByUsername(email).getId();
    }

    /**
     * 获取可参加的考试列表
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getAvailableExams() {
        try {
            List<Exam> exams = examService.getAvailableExams();
            
            // 转换为前端需要的格式
            List<Map<String, Object>> examList = exams.stream().map(exam -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", exam.getId());
                map.put("title", exam.getTitle());
                map.put("courseName", exam.getCourseName());
                map.put("duration", exam.getDuration());
                map.put("startTime", exam.getStartTime());
                map.put("endTime", exam.getEndTime());
                map.put("category", exam.getCategory());
                map.put("difficulty", exam.getDifficulty());
                map.put("questionCount", exam.getQuestionCount());
                map.put("totalScore", exam.getTotalScore());
                
                // 检查当前用户是否已完成该考试
                boolean completed = examService.getExamResult(exam.getId(), getCurrentUserEmail()) != null;
                map.put("completed", completed);
                
                // 计算剩余时间（分钟）
                long currentTime = new Date().getTime();
                long endTime = exam.getEndTime().getTime();
                long remainingTime = (endTime - currentTime) / (60 * 1000); // 转换为分钟
                map.put("remainingTime", Math.max(0, remainingTime));
                
                return map;
            }).collect(Collectors.toList());
            
            return Result.success(examList);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取考试列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试详情
     */
    @GetMapping("/{examId}")
    public Result<Map<String, Object>> getExamDetail(@PathVariable Long examId) {
        try {
            Exam exam = examService.getExamById(examId);
            if (exam == null) {
                return Result.error(ResultCode.FAILED.getCode(), "考试不存在");
            }
            
            // 检查考试是否已发布
            if (!exam.getIsPublished()) {
                return Result.error(ResultCode.FAILED.getCode(), "考试尚未发布");
            }
            
            // 检查考试是否已结束
            Date now = new Date();
            if (now.after(exam.getEndTime())) {
                return Result.error(ResultCode.FAILED.getCode(), "考试已结束");
            }
            
            // 转换为前端需要的格式
            Map<String, Object> examDetail = new HashMap<>();
            examDetail.put("id", exam.getId());
            examDetail.put("title", exam.getTitle());
            examDetail.put("description", exam.getDescription());
            examDetail.put("courseName", exam.getCourseName());
            examDetail.put("duration", exam.getDuration());
            examDetail.put("startTime", exam.getStartTime());
            examDetail.put("endTime", exam.getEndTime());
            examDetail.put("totalScore", exam.getTotalScore());
            examDetail.put("passingScore", exam.getPassingScore());
            examDetail.put("category", exam.getCategory());
            examDetail.put("difficulty", exam.getDifficulty());
            examDetail.put("questionCount", exam.getQuestionCount());
            
            // 检查当前用户是否已完成该考试
            ExamResult examResult = examService.getExamResult(examId, getCurrentUserEmail());
            examDetail.put("completed", examResult != null);
            
            // 如果已完成，添加考试结果信息
            if (examResult != null) {
                Map<String, Object> resultInfo = new HashMap<>();
                resultInfo.put("score", examResult.getScore());
                resultInfo.put("correctCount", examResult.getCorrectCount());
                resultInfo.put("totalCount", examResult.getTotalCount());
                resultInfo.put("correctRate", examResult.getCorrectRate());
                resultInfo.put("submitTime", examResult.getSubmitTime());
                resultInfo.put("timeSpent", examResult.getTimeSpent());
                examDetail.put("resultInfo", resultInfo);
            }
            
            // 计算剩余时间（分钟）
            long currentTime = new Date().getTime();
            long endTime = exam.getEndTime().getTime();
            long remainingTime = (endTime - currentTime) / (60 * 1000); // 转换为分钟
            examDetail.put("remainingTime", Math.max(0, remainingTime));
            
            return Result.success(examDetail);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取考试详情失败: " + e.getMessage());
        }
    }

    /**
     * 开始考试
     */
    @PostMapping("/{examId}/start")
    public Result<Map<String, Object>> startExam(@PathVariable Long examId) {
        try {
            Exam exam = examService.getExamById(examId);
            if (exam == null) {
                return Result.error(ResultCode.FAILED.getCode(), "考试不存在");
            }
            
            // 检查考试是否已发布
            if (!exam.getIsPublished()) {
                return Result.error(ResultCode.FAILED.getCode(), "考试尚未发布");
            }
            
            // 检查考试是否已开始
            Date now = new Date();
            if (now.before(exam.getStartTime())) {
                return Result.error(ResultCode.FAILED.getCode(), "考试尚未开始");
            }
            
            // 检查考试是否已结束
            if (now.after(exam.getEndTime())) {
                return Result.error(ResultCode.FAILED.getCode(), "考试已结束");
            }
            
            // 检查用户是否已完成考试
            String userEmail = getCurrentUserEmail();
            if (examService.getExamResult(examId, userEmail) != null) {
                return Result.error(ResultCode.FAILED.getCode(), "您已完成该考试，不能重复参加");
            }
            
            // 记录开始考试的时间（创建初始状态的ExamResult）
            ExamResult startResult = examService.startExam(examId, userEmail);
            
            // 获取考试题目
            List<Map<String, Object>> questions = examService.getExamQuestions(examId);
            
            // 组装返回数据
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("examId", examId);
            resultData.put("title", exam.getTitle());
            resultData.put("duration", exam.getDuration());
            resultData.put("totalScore", exam.getTotalScore());
            resultData.put("questions", questions);
            resultData.put("startTime", new Date());
            
            return Result.success(resultData);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "开始考试失败: " + e.getMessage());
        }
    }

    /**
     * 提交考试答案
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitExamAnswers(@RequestBody ExamAnswerDTO answerDTO) {
        try {
            // 验证必要字段
            if (answerDTO.getExamId() == null) {
                return Result.error(ResultCode.FAILED.getCode(), "考试ID不能为空");
            }
            
            if (answerDTO.getAnswers() == null || answerDTO.getAnswers().isEmpty()) {
                return Result.error(ResultCode.FAILED.getCode(), "答案不能为空");
            }
            
            // 获取当前用户
            Long userId = getCurrentUserId();
            
            // 处理考试提交
            Map<String, Object> result = examAnswerService.processExamSubmission(userId, answerDTO);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "提交考试答案失败: " + e.getMessage());
        }
    }

    /**
     * 上传考试附件
     */
    @PostMapping("/{examId}/upload")
    public Result<Map<String, Object>> uploadExamAttachment(
            @PathVariable Long examId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error(ResultCode.FAILED.getCode(), "上传文件不能为空");
            }
            
            // 上传文件并获取URL
            String fileUrl = examService.uploadExamFile(examId, file);
            
            // 将文件URL关联到当前用户的考试提交
            examService.addExamAttachment(examId, getCurrentUserEmail(), fileUrl);
            
            Map<String, Object> result = new HashMap<>();
            result.put("examId", examId);
            result.put("fileUrl", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("uploadTime", new Date());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "上传考试附件失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试结果
     */
    @GetMapping("/{examId}/result")
    public Result<Map<String, Object>> getExamResult(@PathVariable Long examId) {
        try {
            // 获取考试结果
            ExamResult result = examService.getExamResult(examId, getCurrentUserEmail());
            if (result == null) {
                return Result.error(ResultCode.FAILED.getCode(), "未找到考试结果");
            }
            
            // 获取考试详细信息
            Exam exam = examService.getExamById(examId);
            
            // 组装返回数据
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("examId", examId);
            resultData.put("title", result.getExamTitle());
            resultData.put("courseName", result.getCourseName());
            resultData.put("score", result.getScore());
            resultData.put("totalScore", exam != null ? exam.getTotalScore() : 100);
            resultData.put("passingScore", exam != null ? exam.getPassingScore() : 60);
            resultData.put("correctCount", result.getCorrectCount());
            resultData.put("totalCount", result.getTotalCount());
            resultData.put("correctRate", result.getCorrectRate());
            resultData.put("submitTime", result.getSubmitTime());
            resultData.put("timeSpent", result.getTimeSpent());
            resultData.put("passed", result.getScore() >= (exam != null ? exam.getPassingScore() : 60));
            
            // 获取考试排名信息
            Map<String, Object> rankingInfo = examService.getExamRanking(examId, getCurrentUserEmail());
            resultData.put("ranking", rankingInfo);
            
            // 获取错题信息（可选）
            // 获取用户所有答案的表现
            Long userId = getCurrentUserId();
            Map<String, Object> performance = examAnswerService.getUserExamPerformance(examId, userId);
            resultData.put("performance", performance);
            
            return Result.success(resultData);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取考试结果失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试历史
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getExamHistory() {
        try {
            // 获取用户完成的所有考试
            List<ExamResult> completedExams = examService.getCompletedExams(getCurrentUserEmail());
            
            // 转换为前端需要的格式
            List<Map<String, Object>> historyList = completedExams.stream().map(result -> {
                Map<String, Object> map = new HashMap<>();
                map.put("examId", result.getExamId());
                map.put("title", result.getExamTitle());
                map.put("courseName", result.getCourseName());
                map.put("score", result.getScore());
                map.put("correctCount", result.getCorrectCount());
                map.put("totalCount", result.getTotalCount());
                map.put("correctRate", result.getCorrectRate());
                map.put("submitTime", result.getSubmitTime());
                map.put("timeSpent", result.getTimeSpent());
                map.put("status", result.getStatus());
                
                // 获取考试详情，判断是否通过
                Exam exam = examService.getExamById(result.getExamId());
                if (exam != null) {
                    map.put("totalScore", exam.getTotalScore());
                    map.put("passingScore", exam.getPassingScore());
                    map.put("passed", result.getScore() >= exam.getPassingScore());
                }
                
                return map;
            }).collect(Collectors.toList());
            
            return Result.success(historyList);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取考试历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getExamStatistics() {
        try {
            // 获取用户的考试统计数据
            Map<String, Object> stats = examService.getExamStats(getCurrentUserEmail());
            
            // 获取用户ID
            Long userId = getCurrentUserId();
            
            // 获取用户的答题统计数据
            Map<String, Object> answerStats = examAnswerService.getUserAnswerStatistics(userId);
            
            // 组合统计数据
            Map<String, Object> combinedStats = new HashMap<>();
            combinedStats.putAll(stats);
            combinedStats.putAll(answerStats);
            
            // 添加详细统计信息
            combinedStats.put("detailedStats", examService.getDetailedExamStats(getCurrentUserEmail()));
            
            return Result.success(combinedStats);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取考试统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取错题集
     */
    @GetMapping("/wrong-questions")
    public Result<List<Map<String, Object>>> getWrongQuestions() {
        try {
            // 获取用户ID
            Long userId = getCurrentUserId();
            
            // 获取用户的错题集
            List<Map<String, Object>> wrongQuestions = examService.getStudentWrongQuestions(getCurrentUserEmail());
            
            return Result.success(wrongQuestions);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取错题集失败: " + e.getMessage());
        }
    }

    /**
     * 获取知识点掌握情况
     */
    @GetMapping("/knowledge-points")
    public Result<Map<String, Object>> getKnowledgePointsMastery() {
        try {
            Long userId = getCurrentUserId();
            Map<String, Object> masteryData = examAnswerService.getUserKnowledgePointsMastery(userId);
            return Result.success(masteryData);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取知识点掌握情况失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试排名
     */
    @GetMapping("/{examId}/ranking")
    public Result<Map<String, Object>> getExamRanking(@PathVariable Long examId) {
        try {
            String userEmail = getCurrentUserEmail();
            
            // 检查用户是否参加了该考试
            ExamResult userResult = examService.getExamResult(examId, userEmail);
            if (userResult == null) {
                return Result.error(ResultCode.FAILED.getCode(), "您尚未参加此考试");
            }
            
            // 获取考试排名信息
            Map<String, Object> rankingInfo = examService.getExamRanking(examId, userEmail);
            
            return Result.success(rankingInfo);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED.getCode(), "获取考试排名失败: " + e.getMessage());
        }
    }
}

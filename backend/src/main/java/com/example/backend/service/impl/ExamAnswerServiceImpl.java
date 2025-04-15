package com.example.backend.service.impl;

import com.example.backend.dto.ExamAnswerDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamAnswer;
import com.example.backend.entity.ExamResult;
import com.example.backend.repository.ExamAnswerRepository;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.ExamResultRepository;
import com.example.backend.service.ExamAnswerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * 考试答案服务实现类
 */
@Service
public class ExamAnswerServiceImpl implements ExamAnswerService {

    private final ExamAnswerRepository examAnswerRepository;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExamAnswerServiceImpl(
            ExamAnswerRepository examAnswerRepository,
            ExamRepository examRepository,
            ExamResultRepository examResultRepository,
            ObjectMapper objectMapper) {
        this.examAnswerRepository = examAnswerRepository;
        this.examRepository = examRepository;
        this.examResultRepository = examResultRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ExamAnswer saveAnswer(ExamAnswer examAnswer) {
        return examAnswerRepository.save(examAnswer);
    }

    @Override
    @Transactional
    public int saveAnswers(Long examId, Long userId, List<Map<String, Object>> answers) {
        int savedCount = 0;
        
        for (Map<String, Object> answerMap : answers) {
            try {
                ExamAnswer answer = new ExamAnswer();
                answer.setExamId(examId);
                answer.setUserId(userId);
                
                // 从答案Map中提取数据
                if (answerMap.containsKey("questionId")) {
                    answer.setQuestionId(Long.valueOf(answerMap.get("questionId").toString()));
                }
                
                if (answerMap.containsKey("questionType")) {
                    answer.setQuestionType(answerMap.get("questionType").toString());
                }
                
                if (answerMap.containsKey("userAnswer")) {
                    Object userAnswerObj = answerMap.get("userAnswer");
                    if (userAnswerObj instanceof String) {
                        answer.setUserAnswer((String) userAnswerObj);
                    } else {
                        // 如果不是字符串，尝试将其转换为JSON
                        answer.setUserAnswer(objectMapper.writeValueAsString(userAnswerObj));
                    }
                }
                
                if (answerMap.containsKey("correctAnswer")) {
                    Object correctAnswerObj = answerMap.get("correctAnswer");
                    if (correctAnswerObj instanceof String) {
                        answer.setCorrectAnswer((String) correctAnswerObj);
                    } else {
                        // 如果不是字符串，尝试将其转换为JSON
                        answer.setCorrectAnswer(objectMapper.writeValueAsString(correctAnswerObj));
                    }
                }
                
                if (answerMap.containsKey("isCorrect")) {
                    answer.setIsCorrect(Boolean.valueOf(answerMap.get("isCorrect").toString()));
                }
                
                if (answerMap.containsKey("score")) {
                    answer.setScore(Double.valueOf(answerMap.get("score").toString()));
                }
                
                if (answerMap.containsKey("knowledgePoint")) {
                    answer.setKnowledgePoint(answerMap.get("knowledgePoint").toString());
                }
                
                // 设置创建时间
                answer.setAnswerTime(LocalDateTime.now());
                
                // 保存答案
                examAnswerRepository.save(answer);
                savedCount++;
            } catch (Exception e) {
                // 记录错误但继续处理
                System.err.println("Error saving answer: " + e.getMessage());
            }
        }
        
        return savedCount;
    }

    @Override
    @Transactional
    public Map<String, Object> processExamSubmission(Long userId, ExamAnswerDTO answerDTO) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 保存答案
        int savedAnswers = saveAnswers(answerDTO.getExamId(), userId, answerDTO.getAnswers());
        
        // 2. 如果需要自动评分，执行自动评分
        Map<String, Object> gradeResult = new HashMap<>();
        if (answerDTO.getAutoGrade() != null && answerDTO.getAutoGrade()) {
            gradeResult = autoGradeExam(answerDTO.getExamId(), userId);
        } else {
            // 否则只计算已评分的部分
            gradeResult = calculatePartialScore(answerDTO.getExamId(), userId);
        }
        
        // 3. 创建或更新考试结果
        saveExamResult(userId, answerDTO, gradeResult);
        
        // 4. 组装返回结果
        result.put("examId", answerDTO.getExamId());
        result.put("savedAnswersCount", savedAnswers);
        result.put("totalScore", gradeResult.get("totalScore"));
        result.put("correctCount", gradeResult.get("correctCount"));
        result.put("totalCount", gradeResult.get("totalCount"));
        result.put("correctRate", gradeResult.get("correctRate"));
        result.put("timeSpent", answerDTO.getTimeSpent());
        result.put("submittedAt", new Date());
        
        return result;
    }

    /**
     * 保存考试结果
     */
    private void saveExamResult(Long userId, ExamAnswerDTO answerDTO, Map<String, Object> gradeResult) {
        // 查找是否已有结果记录
        Optional<ExamResult> existingResult = examResultRepository.findByExamIdAndUserEmail(
                answerDTO.getExamId(), 
                getUserEmailById(userId) // 这里应该有一个方法来获取用户Email
        );
        
        ExamResult examResult;
        if (existingResult.isPresent()) {
            examResult = existingResult.get();
        } else {
            examResult = new ExamResult();
            examResult.setExamId(answerDTO.getExamId());
            examResult.setUserEmail(getUserEmailById(userId));
            
            // 获取考试信息
            Exam exam = examRepository.findById(answerDTO.getExamId()).orElse(null);
            if (exam != null) {
                examResult.setExamTitle(exam.getTitle());
                examResult.setCourseName(exam.getCourseName());
            }
        }
        
        // 更新结果数据
        examResult.setScore((Integer) gradeResult.get("totalScore"));
        examResult.setCorrectCount((Integer) gradeResult.get("correctCount"));
        examResult.setTotalCount((Integer) gradeResult.get("totalCount"));
        examResult.setCorrectRate((Double) gradeResult.get("correctRate"));
        examResult.setTimeSpent(answerDTO.getTimeSpent());
        examResult.setSubmitTime(new Date());
        examResult.setStatus("已完成");
        
        // 保存结果
        examResultRepository.save(examResult);
    }

    /**
     * 根据用户ID获取邮箱
     * 这里简化处理，实际应该通过UserRepository获取
     */
    private String getUserEmailById(Long userId) {
        // 实际实现应该查询用户表
        return "user" + userId + "@example.com";
    }

    /**
     * 计算部分评分（针对未自动评分的情况）
     */
    private Map<String, Object> calculatePartialScore(Long examId, Long userId) {
        List<ExamAnswer> answers = examAnswerRepository.findByExamIdAndUserId(examId, userId)
                .map(List::of)
                .orElse(List.of());
        
        int correctCount = 0;
        double totalScore = 0.0;
        
        for (ExamAnswer answer : answers) {
            if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                correctCount++;
            }
            if (answer.getScore() != null) {
                totalScore += answer.getScore();
            }
        }
        
        int totalCount = answers.size();
        double correctRate = totalCount > 0 ? (double) correctCount / totalCount : 0.0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", (int) totalScore);
        result.put("correctCount", correctCount);
        result.put("totalCount", totalCount);
        result.put("correctRate", correctRate);
        
        return result;
    }

    @Override
    public List<ExamAnswer> getAnswersByExamAndUser(Long examId, Long userId) {
        return examAnswerRepository.findByExamIdAndUserId(examId, userId)
                .map(List::of)
                .orElse(new ArrayList<>());
    }

    @Override
    public List<ExamAnswer> getAnswersByExam(Long examId) {
        return examAnswerRepository.findByExamId(examId);
    }

    @Override
    public List<ExamAnswer> getAnswersByUser(Long userId) {
        return examAnswerRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Map<String, Object> autoGradeExam(Long examId, Long userId) {
        List<ExamAnswer> answers = getAnswersByExamAndUser(examId, userId);
        
        int correctCount = 0;
        double totalScore = 0.0;
        
        for (ExamAnswer answer : answers) {
            // 只自动评分客观题（选择题、判断题等）
            if (isObjectiveQuestion(answer.getQuestionType())) {
                boolean isCorrect = checkAnswerCorrectness(answer);
                answer.setIsCorrect(isCorrect);
                
                // 如果是正确的，分配分数（假设每道题分值相同）
                double score = isCorrect ? 10.0 : 0.0; // 默认每题10分
                answer.setScore(score);
                
                if (isCorrect) {
                    correctCount++;
                    totalScore += score;
                }
                
                // 标记为已评分
                answer.setGradedTime(LocalDateTime.now());
                
                // 保存更新
                examAnswerRepository.save(answer);
            }
        }
        
        int totalCount = answers.size();
        double correctRate = totalCount > 0 ? (double) correctCount / totalCount : 0.0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", (int) totalScore);
        result.put("correctCount", correctCount);
        result.put("totalCount", totalCount);
        result.put("correctRate", correctRate);
        
        return result;
    }

    /**
     * 判断是否为客观题
     */
    private boolean isObjectiveQuestion(String questionType) {
        if (questionType == null) {
            return false;
        }
        
        return questionType.equals("单选题") || 
               questionType.equals("多选题") || 
               questionType.equals("判断题") || 
               questionType.equals("填空题");
    }

    /**
     * 检查答案正确性
     */
    private boolean checkAnswerCorrectness(ExamAnswer answer) {
        if (answer.getUserAnswer() == null || answer.getCorrectAnswer() == null) {
            return false;
        }
        
        String userAnswer = answer.getUserAnswer().trim();
        String correctAnswer = answer.getCorrectAnswer().trim();
        
        // 根据题目类型进行不同的比较
        switch (answer.getQuestionType()) {
            case "单选题":
            case "判断题":
            case "填空题":
                return userAnswer.equals(correctAnswer);
                
            case "多选题":
                // 对多选题，需要进行更复杂的比较
                try {
                    // 假设多选题答案是JSON数组
                    List<String> userChoices = objectMapper.readValue(userAnswer, List.class);
                    List<String> correctChoices = objectMapper.readValue(correctAnswer, List.class);
                    
                    // 检查用户选择的是否与正确答案完全匹配
                    return userChoices.size() == correctChoices.size() && 
                           userChoices.containsAll(correctChoices);
                } catch (JsonProcessingException e) {
                    // 解析失败，尝试直接比较字符串
                    return userAnswer.equals(correctAnswer);
                }
                
            default:
                // 主观题或其他类型，需要人工评分
                return false;
        }
    }

    @Override
    @Transactional
    public ExamAnswer gradeAnswer(Long examAnswerId, Boolean isCorrect, Double score, String comments) {
        ExamAnswer answer = examAnswerRepository.findById(examAnswerId)
                .orElseThrow(() -> new IllegalArgumentException("答案不存在"));
        
        answer.setIsCorrect(isCorrect);
        answer.setScore(score);
        answer.setComments(comments);
        answer.setGradedTime(LocalDateTime.now());
        
        return examAnswerRepository.save(answer);
    }

    @Override
    public Map<String, Object> getUserAnswerStatistics(Long userId) {
        List<ExamAnswer> userAnswers = examAnswerRepository.findByUserId(userId);
        
        int totalAnswers = userAnswers.size();
        int correctAnswers = 0;
        double totalScore = 0.0;
        
        // 统计每种题型的正确数和总数
        Map<String, Integer> typeCorrectCount = new HashMap<>();
        Map<String, Integer> typeTotalCount = new HashMap<>();
        
        for (ExamAnswer answer : userAnswers) {
            if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                correctAnswers++;
            }
            
            if (answer.getScore() != null) {
                totalScore += answer.getScore();
            }
            
            // 按题型统计
            String questionType = answer.getQuestionType();
            if (questionType != null) {
                typeTotalCount.put(questionType, typeTotalCount.getOrDefault(questionType, 0) + 1);
                
                if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                    typeCorrectCount.put(questionType, typeCorrectCount.getOrDefault(questionType, 0) + 1);
                }
            }
        }
        
        // 计算每种题型的正确率
        Map<String, Double> typeCorrectRates = new HashMap<>();
        for (String type : typeTotalCount.keySet()) {
            int correct = typeCorrectCount.getOrDefault(type, 0);
            int total = typeTotalCount.get(type);
            double rate = total > 0 ? (double) correct / total : 0.0;
            typeCorrectRates.put(type, rate);
        }
        
        // 组装结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalAnswers", totalAnswers);
        result.put("correctAnswers", correctAnswers);
        result.put("correctRate", totalAnswers > 0 ? (double) correctAnswers / totalAnswers : 0.0);
        result.put("totalScore", totalScore);
        result.put("averageScore", totalAnswers > 0 ? totalScore / totalAnswers : 0.0);
        result.put("typeCorrectRates", typeCorrectRates);
        result.put("typeTotalCounts", typeTotalCount);
        
        return result;
    }

    @Override
    public Map<String, Object> getUserExamPerformance(Long examId, Long userId) {
        List<ExamAnswer> answers = getAnswersByExamAndUser(examId, userId);
        
        int totalQuestions = answers.size();
        int correctQuestions = 0;
        double totalScore = 0.0;
        
        // 按题型分类统计
        Map<String, Integer> typeCorrectCount = new HashMap<>();
        Map<String, Integer> typeTotalCount = new HashMap<>();
        
        for (ExamAnswer answer : answers) {
            if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                correctQuestions++;
            }
            
            if (answer.getScore() != null) {
                totalScore += answer.getScore();
            }
            
            // 按题型统计
            String questionType = answer.getQuestionType();
            if (questionType != null) {
                typeTotalCount.put(questionType, typeTotalCount.getOrDefault(questionType, 0) + 1);
                
                if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                    typeCorrectCount.put(questionType, typeCorrectCount.getOrDefault(questionType, 0) + 1);
                }
            }
        }
        
        // 获取考试信息
        Exam exam = examRepository.findById(examId).orElse(null);
        
        // 组装结果
        Map<String, Object> result = new HashMap<>();
        result.put("examId", examId);
        if (exam != null) {
            result.put("examTitle", exam.getTitle());
            result.put("courseName", exam.getCourseName());
            result.put("totalPossibleScore", exam.getTotalScore());
            result.put("passingScore", exam.getPassingScore());
        }
        
        result.put("userId", userId);
        result.put("totalQuestions", totalQuestions);
        result.put("correctQuestions", correctQuestions);
        result.put("correctRate", totalQuestions > 0 ? (double) correctQuestions / totalQuestions : 0.0);
        result.put("userScore", totalScore);
        result.put("typePerformance", calculateTypePerformance(typeCorrectCount, typeTotalCount));
        
        // 获取排名信息（可选）
        // 这里需要查询所有参加此考试的用户成绩，按分数排序
        
        return result;
    }

    /**
     * 计算各类型题目的表现
     */
    private List<Map<String, Object>> calculateTypePerformance(
            Map<String, Integer> typeCorrectCount, 
            Map<String, Integer> typeTotalCount) {
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (String type : typeTotalCount.keySet()) {
            int correct = typeCorrectCount.getOrDefault(type, 0);
            int total = typeTotalCount.get(type);
            double rate = total > 0 ? (double) correct / total : 0.0;
            
            Map<String, Object> typePerf = new HashMap<>();
            typePerf.put("type", type);
            typePerf.put("correctCount", correct);
            typePerf.put("totalCount", total);
            typePerf.put("correctRate", rate);
            
            result.add(typePerf);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserWeakKnowledgePoints(Long userId, int limit) {
        // 获取用户所有答案
        List<ExamAnswer> answers = examAnswerRepository.findByUserId(userId);
        
        // 按知识点分组统计
        Map<String, Integer> knowledgePointCorrectCount = new HashMap<>();
        Map<String, Integer> knowledgePointTotalCount = new HashMap<>();
        
        for (ExamAnswer answer : answers) {
            String knowledgePoint = answer.getKnowledgePoint();
            if (knowledgePoint != null && !knowledgePoint.isEmpty()) {
                knowledgePointTotalCount.put(knowledgePoint, 
                        knowledgePointTotalCount.getOrDefault(knowledgePoint, 0) + 1);
                
                if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                    knowledgePointCorrectCount.put(knowledgePoint, 
                            knowledgePointCorrectCount.getOrDefault(knowledgePoint, 0) + 1);
                }
            }
        }
        
        // 计算每个知识点的正确率，并按正确率升序排序
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (String point : knowledgePointTotalCount.keySet()) {
            int correct = knowledgePointCorrectCount.getOrDefault(point, 0);
            int total = knowledgePointTotalCount.get(point);
            double rate = total > 0 ? (double) correct / total : 0.0;
            
            Map<String, Object> pointData = new HashMap<>();
            pointData.put("knowledgePoint", point);
            pointData.put("correctCount", correct);
            pointData.put("totalCount", total);
            pointData.put("correctRate", rate);
            
            result.add(pointData);
        }
        
        // 按正确率升序排序，取前limit个
        return result.stream()
                .sorted((a, b) -> Double.compare(
                        (Double) a.get("correctRate"), 
                        (Double) b.get("correctRate")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public double getExamDifficultyFactor(Long examId) {
        List<ExamAnswer> answers = examAnswerRepository.findByExamId(examId);
        
        if (answers.isEmpty()) {
            return 0.0; // 没有答案数据
        }
        
        long correctCount = answers.stream()
                .filter(a -> a.getIsCorrect() != null && a.getIsCorrect())
                .count();
        
        // 难度系数 = 1 - 正确率
        // 正确率越高，难度越低
        return 1.0 - ((double) correctCount / answers.size());
    }

    @Override
    public List<Map<String, Object>> getMostDifficultQuestions(Long examId, int limit) {
        List<ExamAnswer> allAnswers = examAnswerRepository.findByExamId(examId);
        
        // 按题目ID分组
        Map<Long, List<ExamAnswer>> answersByQuestion = allAnswers.stream()
                .collect(Collectors.groupingBy(ExamAnswer::getQuestionId));
        
        // 计算每个题目的正确率
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map.Entry<Long, List<ExamAnswer>> entry : answersByQuestion.entrySet()) {
            Long questionId = entry.getKey();
            List<ExamAnswer> questionAnswers = entry.getValue();
            
            long correctCount = questionAnswers.stream()
                    .filter(a -> a.getIsCorrect() != null && a.getIsCorrect())
                    .count();
            
            double correctRate = (double) correctCount / questionAnswers.size();
            
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("questionId", questionId);
            questionData.put("correctCount", correctCount);
            questionData.put("totalCount", questionAnswers.size());
            questionData.put("correctRate", correctRate);
            
            // 添加题目类型和知识点（假设所有答案对应同一道题，类型和知识点都一样）
            if (!questionAnswers.isEmpty()) {
                ExamAnswer sampleAnswer = questionAnswers.get(0);
                questionData.put("questionType", sampleAnswer.getQuestionType());
                questionData.put("knowledgePoint", sampleAnswer.getKnowledgePoint());
                
                // 正确答案（用于教师参考）
                questionData.put("correctAnswer", sampleAnswer.getCorrectAnswer());
            }
            
            result.add(questionData);
        }
        
        // 按正确率升序排序，取前limit个
        return result.stream()
                .sorted((a, b) -> Double.compare(
                        (Double) a.get("correctRate"), 
                        (Double) b.get("correctRate")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAnswersByExam(Long examId) {
        examAnswerRepository.deleteByExamId(examId);
    }

    @Override
    @Transactional
    public void deleteAnswersByUser(Long userId) {
        List<ExamAnswer> userAnswers = examAnswerRepository.findByUserId(userId);
        examAnswerRepository.deleteAll(userAnswers);
    }

    @Override
    public Map<String, Object> getUserKnowledgePointsMastery(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取用户所有答题记录
        List<ExamAnswer> answers = examAnswerRepository.findByUserId(userId);
        
        // 按知识点分组统计
        Map<String, List<ExamAnswer>> answersByKnowledgePoint = answers.stream()
            .filter(a -> a.getKnowledgePoint() != null && !a.getKnowledgePoint().isEmpty())
            .collect(Collectors.groupingBy(ExamAnswer::getKnowledgePoint));
        
        // 计算每个知识点的掌握情况
        List<Map<String, Object>> knowledgePoints = new ArrayList<>();
        for (Map.Entry<String, List<ExamAnswer>> entry : answersByKnowledgePoint.entrySet()) {
            String knowledgePoint = entry.getKey();
            List<ExamAnswer> pointAnswers = entry.getValue();
            
            // 计算正确率
            long correctCount = pointAnswers.stream()
                .filter(a -> a.getIsCorrect() != null && a.getIsCorrect())
                .count();
            int totalCount = pointAnswers.size();
            double correctRate = totalCount > 0 ? (double) correctCount / totalCount : 0;
            
            // 创建知识点信息
            Map<String, Object> pointInfo = new HashMap<>();
            pointInfo.put("knowledgePoint", knowledgePoint);
            pointInfo.put("correctCount", correctCount);
            pointInfo.put("totalCount", totalCount);
            pointInfo.put("correctRate", correctRate);
            pointInfo.put("masteryLevel", getMasteryLevel(correctRate));
            
            knowledgePoints.add(pointInfo);
        }
        
        // 按掌握程度排序（从低到高）
        knowledgePoints.sort(Comparator.comparing(p -> (Double) p.get("correctRate")));
        
        result.put("knowledgePoints", knowledgePoints);
        result.put("totalKnowledgePoints", knowledgePoints.size());
        
        // 计算整体掌握情况
        double overallCorrectRate = answers.isEmpty() ? 0 : 
            (double) answers.stream()
                .filter(a -> a.getIsCorrect() != null && a.getIsCorrect())
                .count() / answers.size();
        result.put("overallCorrectRate", overallCorrectRate);
        result.put("overallMasteryLevel", getMasteryLevel(overallCorrectRate));
        
        return result;
    }
    
    /**
     * 根据正确率确定掌握程度
     */
    private String getMasteryLevel(double correctRate) {
        if (correctRate >= 0.9) {
            return "精通";
        } else if (correctRate >= 0.75) {
            return "熟练";
        } else if (correctRate >= 0.6) {
            return "掌握";
        } else if (correctRate >= 0.4) {
            return "基本掌握";
        } else {
            return "需要加强";
        }
    }
} 
package com.example.backend.service.impl;

import com.example.backend.dto.ExamStatsDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;
import com.example.backend.entity.ExamSubmission;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.ExamResultRepository;
import com.example.backend.repository.ExamSubmissionRepository;
import com.example.backend.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamSubmissionRepository examSubmissionRepository;

    @Autowired
    public ExamServiceImpl(ExamRepository examRepository,
                          ExamResultRepository examResultRepository,
                          ExamSubmissionRepository examSubmissionRepository) {
        this.examRepository = examRepository;
        this.examResultRepository = examResultRepository;
        this.examSubmissionRepository = examSubmissionRepository;
    }

    @Override
    @Transactional
    public Exam createExam(Exam exam) {
        // 设置创建时间
        if (exam.getStartTime() == null) {
            exam.setStartTime(new Date());
        }
        
        // 设置createdAt字段
        if (exam.getCreatedAt() == null) {
            exam.setCreatedAt(new Date());
        }
        
        return examRepository.save(exam);
    }

    @Override
    @Transactional
    public Exam updateExam(Long examId, Exam updatedExam) {
        Exam existingExam = getExamById(examId);
        if (existingExam != null) {
            // 更新基本信息
            existingExam.setTitle(updatedExam.getTitle());
            existingExam.setDescription(updatedExam.getDescription());
            existingExam.setCourseName(updatedExam.getCourseName());
            existingExam.setDuration(updatedExam.getDuration());
            existingExam.setStartTime(updatedExam.getStartTime());
            existingExam.setEndTime(updatedExam.getEndTime());
            existingExam.setTotalScore(updatedExam.getTotalScore());
            existingExam.setPassingScore(updatedExam.getPassingScore());
            existingExam.setIsPublished(updatedExam.getIsPublished());
            existingExam.setCategory(updatedExam.getCategory());
            existingExam.setDifficulty(updatedExam.getDifficulty());
            existingExam.setQuestionCount(updatedExam.getQuestionCount());

            return examRepository.save(existingExam);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getStudentWrongQuestions(String userEmail) {
        return List.of();
    }

    @Override
    public Map<String, Object> getDetailedExamStats(String userEmail) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTeacherExamStatistics(Long teacherId) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getExamDetailStatistics(Long examId) {
        return Map.of();
    }

    @Override
    public void batchDeleteExams(List<Long> examIds) {

    }

    @Override
    public void batchPublishExams(List<Long> examIds) {

    }

    @Override
    @Transactional
    public void deleteExam(Long examId) {
        examRepository.deleteById(examId);
    }

    @Override
    public Exam getExamById(Long examId) {
        return examRepository.findById(examId).orElse(null);
    }

    @Override
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Override
    public List<Exam> getExamsByCreator(String creatorEmail) {
        return examRepository.findByCreatorEmail(creatorEmail);
    }

    @Override
    public List<Exam> getAvailableExams() {
        return examRepository.findAvailableExams(new Date());
    }

    @Override
    @Transactional
    public ExamResult submitExamResult(ExamResult examResult) {
        // 设置提交时间
        examResult.setSubmitTime(new Date());

        // 计算正确率
        if (examResult.getTotalCount() != null && examResult.getTotalCount() > 0) {
            double correctRate = (double) examResult.getCorrectCount() / examResult.getTotalCount();
            examResult.setCorrectRate(correctRate);
        }

        // 获取考试标题和课程名称
        Exam exam = examRepository.findById(examResult.getExamId()).orElse(null);
        if (exam != null) {
            examResult.setExamTitle(exam.getTitle());
            examResult.setCourseName(exam.getCourseName());
        }

        return examResultRepository.save(examResult);
    }

    @Override
    public ExamResult getExamResult(Long examId, String userEmail) {
        Optional<ExamResult> result = examResultRepository.findByExamIdAndUserEmail(examId, userEmail);
        return result.orElse(null);
    }

    @Override
    public List<ExamResult> getCompletedExams(String userEmail) {
        return examResultRepository.findByUserEmail(userEmail);
    }

    @Override
    public Map<String, Object> getExamStats(String userEmail) {
        // 获取用户完成的考试
        List<ExamResult> completedExams = getCompletedExams(userEmail);
        
        // 创建统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExams", completedExams.size());
        stats.put("completedExams", completedExams.size());
        
        // 计算总分和平均分
        int totalScore = 0;
        double averageScore = 0.0;
        double averageCorrectRate = 0.0;
        int bestScore = 0;
        String bestExamTitle = "";
        
        for (ExamResult result : completedExams) {
            totalScore += result.getScore();
            if (result.getScore() > bestScore) {
                bestScore = result.getScore();
                bestExamTitle = result.getExamTitle();
            }
            // 这里假设正确率数据存储在result对象中，实际项目中可能需要计算
            if (result.getCorrectRate() != null) {
                averageCorrectRate += result.getCorrectRate();
            }
        }
        
        if (!completedExams.isEmpty()) {
            averageScore = (double) totalScore / completedExams.size();
            averageCorrectRate = averageCorrectRate / completedExams.size();
        }
        
        stats.put("totalScore", totalScore);
        stats.put("averageScore", averageScore);
        stats.put("averageCorrectRate", averageCorrectRate);
        stats.put("bestScore", bestScore);
        stats.put("bestExamTitle", bestExamTitle);
        
        // 考试分类分布统计（此处简化处理）
        stats.put("categoryDistribution", new HashMap<String, Integer>());
        
        return stats;
    }

    @Override
    public List<ExamResult> getExamResultsByDateRange(String userEmail, Date startDate, Date endDate) {
        return examResultRepository.findByUserEmailAndDateRange(userEmail, startDate, endDate);
    }

    @Override
    public long countAllExams() {
        return examRepository.count();
    }

    @Override
    public long calculateExamGrowthRate() {
        // 计算过去30天内创建的考试数量
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        long recentExams = examRepository.countByCreatedAtAfter(thirtyDaysAgo);

        // 计算30天前的考试总数
        long totalExams = countAllExams();
        long oldExams = totalExams - recentExams;

        // 计算增长率
        if (oldExams > 0) {
            return (recentExams * 100) / oldExams;
        } else {
            return recentExams > 0 ? 100 : 0; // 如果之前没有考试，但现在有，则增长率为100%
        }
    }

    @Override
    public Map<String, Object> createExam(Long teacherId, Map<String, Object> examData) {
        // 实现创建考试的逻辑
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getTeacherExams(Long teacherId, String keyword, int page, int size) {
        // 实现获取教师考试的逻辑
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getPendingExams(Long teacherId, int page, int size) {
        // 实现获取待批改考试的逻辑
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> getCourseExams(Long courseId) {
        // 实现获取课程考试的逻辑
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getExamDetail(Long examId) {
        // 实现获取考试详情的逻辑
        return new HashMap<>();
    }

    @Override
    public boolean isTeacherExam(Long teacherId, Long examId) {
        // 实现验证考试是否属于该教师的逻辑
        return false;
    }

    @Override
    public String uploadExamFile(Long examId, org.springframework.web.multipart.MultipartFile file) {
        // 实现上传考试文件的逻辑
        return "";
    }

    @Override
    public void gradeExamSubmission(Long submissionId, Map<String, Object> gradingData) {
        // 实现批改考试提交的逻辑
    }

    @Override
    public List<Map<String, Object>> getExamQuestions(Long examId) {
        // 实现获取考试题目的逻辑
        return new ArrayList<>(); // 暂时返回空列表，需要实现实际逻辑
    }

    @Override
    public ExamResult startExam(Long examId, String userEmail) {
        // 实现开始考试的逻辑
        return null; // 暂时返回null，需要实现实际逻辑
    }

    @Override
    public ExamResult submitExamAnswers(Long examId, String userEmail, List<Map<String, Object>> answers) {
        // 实现提交考试答案的逻辑
        return null; // 暂时返回null，需要实现实际逻辑
    }

    @Override
    public void addExamAttachment(Long examId, String userEmail, String fileUrl) {
        // 实现添加考试附件的逻辑
    }

    @Override
    public Map<String, Object> getExamSubmissions(Long examId, int page, int size) {
        Map<String, Object> result = new HashMap<>();

        // 创建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "submittedAt"));

        // 查询考试提交
        Page<ExamSubmission> submissionsPage = examSubmissionRepository.findByExamId(examId, pageable);

        // 转换为前端需要的格式
        List<Map<String, Object>> submissions = submissionsPage.getContent().stream()
                .map(this::convertSubmissionToMap)
                .collect(Collectors.toList());

        result.put("submissions", submissions);
        result.put("currentPage", submissionsPage.getNumber() + 1);
        result.put("totalItems", submissionsPage.getTotalElements());
        result.put("totalPages", submissionsPage.getTotalPages());

        return result;
    }

    @Override
    public Map<String, Object> getSubmissionDetail(Long submissionId) {
        ExamSubmission submission = examSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new NoSuchElementException("提交不存在"));

        return convertSubmissionToMap(submission);
    }

    @Override
    public Map<String, Object> getTeacherExamRanking(Long examId) {
        Map<String, Object> result = new HashMap<>();

        // 获取考试信息
        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) {
            return result;
        }

        // 获取所有提交
        List<ExamSubmission> submissions = examSubmissionRepository.findByExamId(examId);

        // 按分数排序
        List<Map<String, Object>> ranking = submissions.stream()
                .filter(s -> s.getIsGraded() && s.getTotalScore() != null)
                .sorted(Comparator.comparing(ExamSubmission::getTotalScore).reversed())
                .map(s -> {
                    Map<String, Object> item = new HashMap<>();
                    // 添加空值检查，防止空指针异常
                    if (s.getStudent() != null) {
                        item.put("studentId", s.getStudent().getId());
                        item.put("studentName", s.getStudent().getUsername());
                    } else {
                        // 如果关联的学生对象为空，使用studentId
                        item.put("studentId", s.getStudentId());
                        item.put("studentName", "未知学生");
                    }
                    item.put("score", s.getTotalScore());
                    item.put("submittedAt", s.getSubmittedAt());
                    return item;
                })
                .collect(Collectors.toList());

        // 添加排名信息
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).put("rank", i + 1);
        }

        result.put("ranking", ranking);
        result.put("examTitle", exam.getTitle());
        result.put("totalScore", exam.getTotalScore());
        result.put("passingScore", exam.getPassingScore());
        result.put("totalStudents", submissions.size());

        // 计算统计数据
        double averageScore = ranking.stream()
                .mapToDouble(item -> ((Number) item.get("score")).doubleValue())
                .average()
                .orElse(0);

        long passCount = ranking.stream()
                .filter(item -> ((Number) item.get("score")).doubleValue() >= exam.getPassingScore())
                .count();

        result.put("averageScore", averageScore);
        result.put("passRate", ranking.isEmpty() ? 0 : (double) passCount / ranking.size());

        return result;
    }

    @Override
    public boolean canTeacherGradeSubmission(Long teacherId, Long submissionId) {
        // 获取提交信息
        ExamSubmission submission = examSubmissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            return false;
        }

        // 获取测试信息
        Exam exam = submission.getExam();
        if (exam == null) {
            // 如果关联的考试为空，使用examId查询考试
            exam = examRepository.findById(submission.getExamId()).orElse(null);
            if (exam == null) {
                return false;
            }
        }

        // 验证测试是否属于该教师
        return exam.getTeacherId().equals(teacherId);
    }

    /**
     * 将ExamSubmission转换为Map
     *
     * @param submission 考试提交
     * @return Map格式的提交信息
     */
    private Map<String, Object> convertSubmissionToMap(ExamSubmission submission) {
        Map<String, Object> submissionMap = new HashMap<>();
        submissionMap.put("id", submission.getId());
        
        // 添加对exam的空值检查
        if (submission.getExam() != null) {
            submissionMap.put("examId", submission.getExam().getId());
            submissionMap.put("examTitle", submission.getExam().getTitle());
        } else {
            submissionMap.put("examId", submission.getExamId());
            submissionMap.put("examTitle", "未知考试");
        }
        
        // 添加对student的空值检查
        if (submission.getStudent() != null) {
            submissionMap.put("studentId", submission.getStudent().getId());
            submissionMap.put("studentName", submission.getStudent().getUsername());
        } else {
            submissionMap.put("studentId", submission.getStudentId());
            submissionMap.put("studentName", "未知学生");
        }
        
        submissionMap.put("answers", submission.getAnswers());
        submissionMap.put("fileUrl", submission.getFileUrl());
        submissionMap.put("totalScore", submission.getTotalScore());
        submissionMap.put("comments", submission.getComments());
        submissionMap.put("isGraded", submission.getIsGraded());
        submissionMap.put("submittedAt", submission.getSubmittedAt());
        submissionMap.put("gradedAt", submission.getGradedAt());
        submissionMap.put("startedAt", submission.getStartedAt());
        return submissionMap;
    }

    @Override
    public Map<String, Object> getStudentExamStatistics(String userEmail) {
        // 实现获取学生考试统计数据的逻辑
        return new HashMap<>(); // 暂时返回空数据，需要实现实际逻辑
    }




    @Override
    public Map<String, Object> getStudentKnowledgePoints(String userEmail) {
        // 实现获取学生知识点掌握情况的逻辑
        return new HashMap<>(); // 暂时返回空数据，需要实现实际逻辑
    }

    @Override
    public Map<String, Object> getExamRanking(Long examId, String userEmail) {
        // 实现获取考试排名的逻辑
        return new HashMap<>(); // 暂时返回空数据，需要实现实际逻辑
    }

    @Override
    public double calculateAverageScore(String userEmail) {
        // 实现计算平均成绩的逻辑
        return 0.0; // 暂时返回0.0，需要实现实际逻辑
    }

    @Override
    public int countCompletedExams(String userEmail) {
        // 实现统计已完成的考试数量的逻辑
        return 0; // 暂时返回0，需要实现实际逻辑
    }

    @Override
    public void publishExam(Long examId) {
        // 实现发布考试的逻辑
    }

    @Override
    public long countPendingExams(Long teacherId) {
        // 实现统计教师待批改考试数量的逻辑
        return 0;
    }

    @Override
    public Map<String, Object> updateExam(Long examId, Map<String, Object> examData) {
        // 获取考试
        Exam exam = getExamById(examId);
        if (exam == null) {
            throw new IllegalArgumentException("考试不存在");
        }
        
        // 更新考试信息
        if (examData.containsKey("title")) {
            exam.setTitle((String) examData.get("title"));
        }
        if (examData.containsKey("description")) {
            exam.setDescription((String) examData.get("description"));
        }
        if (examData.containsKey("courseName")) {
            exam.setCourseName((String) examData.get("courseName"));
        }
        if (examData.containsKey("duration")) {
            exam.setDuration((Integer) examData.get("duration"));
        }
        if (examData.containsKey("startTime")) {
            exam.setStartTime((Date) examData.get("startTime"));
        }
        if (examData.containsKey("endTime")) {
            exam.setEndTime((Date) examData.get("endTime"));
        }
        if (examData.containsKey("totalScore")) {
            exam.setTotalScore((Integer) examData.get("totalScore"));
        }
        if (examData.containsKey("passingScore")) {
            exam.setPassingScore((Integer) examData.get("passingScore"));
        }
        if (examData.containsKey("category")) {
            exam.setCategory((String) examData.get("category"));
        }
        if (examData.containsKey("difficulty")) {
            exam.setDifficulty((String) examData.get("difficulty"));
        }
        if (examData.containsKey("isPublished")) {
            exam.setIsPublished((Boolean) examData.get("isPublished"));
        }
        
        // 保存更新后的考试
        Exam updatedExam = updateExam(examId, exam);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", updatedExam.getId());
        result.put("title", updatedExam.getTitle());
        result.put("description", updatedExam.getDescription());
        result.put("courseName", updatedExam.getCourseName());
        result.put("duration", updatedExam.getDuration());
        result.put("startTime", updatedExam.getStartTime());
        result.put("endTime", updatedExam.getEndTime());
        result.put("totalScore", updatedExam.getTotalScore());
        result.put("passingScore", updatedExam.getPassingScore());
        result.put("category", updatedExam.getCategory());
        result.put("difficulty", updatedExam.getDifficulty());
        result.put("isPublished", updatedExam.getIsPublished());
        
        return result;
    }

    @Override
    public String exportExamResults(Long examId) {
        // 验证考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("考试不存在"));
        
        // 获取考试的所有结果
        List<ExamResult> results = examResultRepository.findByExamId(examId);
        
        if (results.isEmpty()) {
            throw new IllegalStateException("该考试暂无学生成绩可导出");
        }
        
        // 在实际应用中，这里应该实现导出为Excel或CSV文件的逻辑
        // 例如使用Apache POI或OpenCSV库创建文件，并上传到文件服务器
        
        // 为简化实现，这里返回一个模拟的文件URL
        // 实际项目中，应该生成文件并返回访问URL
        String fileName = "exam_" + examId + "_results_" + System.currentTimeMillis() + ".xlsx";
        String fileUrl = "/downloads/exams/" + fileName;
        
        // 记录导出日志信息
        System.out.println("导出考试结果: 考试ID=" + examId + ", 考试标题=" + exam.getTitle() + ", 结果数量=" + results.size());
        
        return fileUrl;
    }
}
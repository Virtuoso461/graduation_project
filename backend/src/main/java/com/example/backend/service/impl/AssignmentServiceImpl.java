package com.example.backend.service.impl;

import com.example.backend.dto.AssignmentDTO;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.AssignmentSubmission;
import com.example.backend.entity.User;
import com.example.backend.repository.AssignmentRepository;
import com.example.backend.repository.AssignmentSubmissionRepository;
import com.example.backend.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Override
    @Transactional
    public Assignment createAssignment(AssignmentDTO assignmentDTO, User teacher) {
        Assignment assignment = assignmentDTO.toEntity();
        assignment.setEmail(teacher.getUsername());
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    @Override
    public List<Assignment> findAllAssignments() {
        return assignmentRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Assignment> getAssignmentsByEmail(String email) {
        return assignmentRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    @Override
    public Map<String, Object> getAssignmentStats(String email) {
        Map<String, Object> stats = new HashMap<>();

        // 获取不同状态的作业数量
        long pendingCount = assignmentRepository.countByEmailAndStatus(email, "PENDING");
        long submittedCount = assignmentRepository.countByEmailAndStatus(email, "SUBMITTED");
        long gradedCount = assignmentRepository.countByEmailAndStatus(email, "GRADED");

        // 获取待完成的作业
        List<Assignment> pendingAssignments = assignmentRepository.findByEmailAndStatusOrderByDueDateAsc(email, "PENDING");

        stats.put("pendingCount", pendingCount);
        stats.put("submittedCount", submittedCount);
        stats.put("gradedCount", gradedCount);
        stats.put("totalCount", pendingCount + submittedCount + gradedCount);
        stats.put("pendingAssignments", pendingAssignments);

        return stats;
    }

    @Override
    public Assignment findAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作业不存在"));
    }

    @Override
    @Transactional
    public Assignment updateAssignment(Long id, Assignment updatedAssignment) {
        Assignment assignment = findAssignmentById(id);

        assignment.setTitle(updatedAssignment.getTitle());
        assignment.setDescription(updatedAssignment.getDescription());
        assignment.setDueDate(updatedAssignment.getDueDate());
        assignment.setUpdatedAt(LocalDateTime.now());

        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    @Override
    public long countAllAssignments() {
        return assignmentRepository.count();
    }

    @Override
    public long calculateAssignmentGrowthRate() {
        // 计算过去30天内创建的作业数量
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentAssignments = assignmentRepository.countByCreatedAtAfter(thirtyDaysAgo);

        // 计算30天前的作业总数
        long totalAssignments = countAllAssignments();
        long oldAssignments = totalAssignments - recentAssignments;

        // 计算增长率
        if (oldAssignments > 0) {
            return (recentAssignments * 100) / oldAssignments;
        } else {
            return recentAssignments > 0 ? 100 : 0; // 如果之前没有作业，但现在有，则增长率为100%
        }
    }

    @Override
    public Map<String, Object> createAssignment(Long teacherId, Map<String, Object> assignmentData) {
        // 实现创建作业的逻辑
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getTeacherAssignments(Long teacherId, String keyword, int page, int size) {
        // 实现获取教师作业的逻辑
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getPendingAssignments(Long teacherId, int page, int size) {
        // 实现获取待批改作业的逻辑
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> getCourseAssignments(Long courseId) {
        // 实现获取课程作业的逻辑
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getAssignmentDetail(Long assignmentId) {
        // 实现获取作业详情的逻辑
        return new HashMap<>();
    }

    @Override
    public boolean isTeacherAssignment(Long teacherId, Long assignmentId) {
        // 实现验证作业是否属于该教师的逻辑
        return false;
    }

    @Override
    public String uploadAssignmentFile(Long assignmentId, org.springframework.web.multipart.MultipartFile file) {
        // 实现上传作业文件的逻辑
        return "";
    }

    @Override
    public void gradeAssignmentSubmission(Long submissionId, Map<String, Object> gradingData) {
        // 实现批改作业提交的逻辑
    }

    @Override
    public boolean canTeacherGradeSubmission(Long teacherId, Long submissionId) {
        // 实现验证教师是否可以批改该提交的逻辑
        return false;
    }

    @Override
    public long countPendingAssignments(Long teacherId) {
        // 实现统计教师待批改作业数量的逻辑
        return 0;
    }

    @Override
    public com.example.backend.entity.AssignmentSubmission getStudentSubmission(Long assignmentId, Long studentId) {
        // 实现获取学生的单个作业提交的逻辑
        return null; // 暂时返回null，需要实现实际逻辑
    }

    @Override
    public com.example.backend.entity.AssignmentSubmission submitAssignment(Long assignmentId, Long studentId, String content, String fileUrl) {
        // 实现提交作业的逻辑
        return null; // 暂时返回null，需要实现实际逻辑
    }

    @Override
    public List<Assignment> getPendingAssignments(String email) {
        // 实现获取学生的待完成作业的逻辑
        return new ArrayList<>(); // 暂时返回空列表，需要实现实际逻辑
    }

    @Override
    public List<com.example.backend.entity.AssignmentSubmission> getCompletedAssignments(String email) {
        // 实现获取学生的已完成作业的逻辑
        return new ArrayList<>(); // 暂时返回空列表，需要实现实际逻辑
    }

    @Override
    public List<com.example.backend.entity.AssignmentSubmission> getAssignmentSubmissionsByStatus(String email, String status) {
        // 实现获取学生的作业提交情况的逻辑
        return new ArrayList<>(); // 暂时返回空列表，需要实现实际逻辑
    }

    @Override
    public List<com.example.backend.entity.AssignmentSubmission> getAllAssignmentSubmissions(String email) {
        // 实现获取学生的所有作业提交的逻辑
        return new ArrayList<>(); // 暂时返回空列表，需要实现实际逻辑
    }

    @Override
    public int countCompletedAssignments(String email) {
        // 实现统计学生已完成的作业数量的逻辑
        return 0; // 暂时返回0，需要实现实际逻辑
    }

    @Override
    public int countPendingAssignments(String email) {
        // 实现统计学生待完成的作业数量的逻辑
        return 0; // 暂时返回0，需要实现实际逻辑
    }

    @Override
    public Map<String, Object> getTeacherAssignmentStatistics(Long teacherId) {
        // 实现获取教师的作业统计数据的逻辑
        return new HashMap<>(); // 暂时返回空数据，需要实现实际逻辑
    }

    @Override
    public void batchDeleteAssignments(List<Long> assignmentIds) {
        // 实现批量删除作业的逻辑
    }

    @Override
    public void batchPublishAssignments(List<Long> assignmentIds) {
        // 实现批量发布作业的逻辑
    }

    @Override
    public Map<String, Object> updateAssignment(Long assignmentId, Map<String, Object> assignmentData) {
        // 获取现有作业
        Assignment assignment = findAssignmentById(assignmentId);

        // 更新作业信息
        if (assignmentData.containsKey("title")) {
            assignment.setTitle((String) assignmentData.get("title"));
        }

        if (assignmentData.containsKey("description")) {
            assignment.setDescription((String) assignmentData.get("description"));
        }

        if (assignmentData.containsKey("dueDate")) {
            String dueDateStr = (String) assignmentData.get("dueDate");
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                assignment.setDueDate(LocalDateTime.parse(dueDateStr));
            }
        }

        if (assignmentData.containsKey("courseId")) {
            Object courseIdObj = assignmentData.get("courseId");
            if (courseIdObj != null) {
                if (courseIdObj instanceof Number) {
                    // 如果是数字类型
                    assignment.setCourseId(((Number) courseIdObj).longValue());
                } else if (courseIdObj instanceof String) {
                    // 如果是字符串类型
                    try {
                        assignment.setCourseId(Long.parseLong((String) courseIdObj));
                    } catch (NumberFormatException e) {
                        // 忽略无效的课程ID
                    }
                }
            }
        }

        if (assignmentData.containsKey("status")) {
            assignment.setStatus((String) assignmentData.get("status"));
        }

        if (assignmentData.containsKey("maxScore")) {
            Object maxScoreObj = assignmentData.get("maxScore");
            if (maxScoreObj != null) {
                if (maxScoreObj instanceof Number) {
                    assignment.setMaxScore(((Number) maxScoreObj).intValue());
                } else if (maxScoreObj instanceof String) {
                    try {
                        assignment.setMaxScore(Integer.parseInt((String) maxScoreObj));
                    } catch (NumberFormatException e) {
                        // 忽略无效的最高分
                    }
                }
            }
        }

        // 更新时间
        assignment.setUpdatedAt(LocalDateTime.now());

        // 保存更新后的作业
        assignment = assignmentRepository.save(assignment);

        // 转换为Map返回
        Map<String, Object> result = new HashMap<>();
        result.put("id", assignment.getId());
        result.put("title", assignment.getTitle());
        result.put("description", assignment.getDescription());
        result.put("dueDate", assignment.getDueDate() != null ? assignment.getDueDate().toString() : null);
        result.put("courseId", assignment.getCourseId());
        result.put("status", assignment.getStatus());
        result.put("maxScore", assignment.getMaxScore());
        result.put("createdAt", assignment.getCreatedAt() != null ? assignment.getCreatedAt().toString() : null);
        result.put("updatedAt", assignment.getUpdatedAt() != null ? assignment.getUpdatedAt().toString() : null);

        return result;
    }

    @Override
    public Map<String, Object> getAssignmentSubmissions(Long assignmentId, int page, int size) {
        Map<String, Object> result = new HashMap<>();

        // 创建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "submittedAt"));

        // 查询作业提交
        Page<AssignmentSubmission> submissionsPage = assignmentSubmissionRepository.findByAssignmentId(assignmentId, pageable);

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
        AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new NoSuchElementException("提交不存在"));

        return convertSubmissionToMap(submission);
    }

    /**
     * 将AssignmentSubmission转换为Map
     *
     * @param submission 作业提交
     * @return Map格式的提交信息
     */
    private Map<String, Object> convertSubmissionToMap(AssignmentSubmission submission) {
        Map<String, Object> submissionMap = new HashMap<>();
        submissionMap.put("id", submission.getId());
        submissionMap.put("assignmentId", submission.getAssignment().getId());
        submissionMap.put("assignmentTitle", submission.getAssignment().getTitle());
        submissionMap.put("studentId", submission.getStudent().getId());
        submissionMap.put("studentName", submission.getStudent().getUsername());
        submissionMap.put("content", submission.getContent());
        submissionMap.put("fileUrl", submission.getFileUrl());
        submissionMap.put("score", submission.getScore());
        submissionMap.put("feedback", submission.getFeedback());
        submissionMap.put("isGraded", submission.getIsGraded());
        submissionMap.put("submittedAt", submission.getSubmittedAt());
        submissionMap.put("gradedAt", submission.getGradedAt());
        return submissionMap;
    }
}
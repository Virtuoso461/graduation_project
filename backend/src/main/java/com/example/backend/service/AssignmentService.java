package com.example.backend.service;

import com.example.backend.dto.AssignmentDTO;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.AssignmentSubmission;
import com.example.backend.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 作业服务接口
 * 提供作业相关的业务逻辑操作
 */
public interface AssignmentService {

    /**
     * 创建新作业（基础方法）
     * @param assignmentDTO 作业DTO
     * @param teacher 教师用户
     * @return 创建的作业
     */
    Assignment createAssignment(AssignmentDTO assignmentDTO, User teacher);

    /**
     * 更新作业
     * @param id 作业ID
     * @param assignment 更新后的作业
     * @return 更新后的作业
     */
    Assignment updateAssignment(Long id, Assignment assignment);

    /**
     * 更新作业（教师端接口，通过Map传递数据）
     *
     * @param assignmentId 作业ID
     * @param assignmentData 作业数据
     * @return 更新后的作业详情
     */
    Map<String, Object> updateAssignment(Long assignmentId, Map<String, Object> assignmentData);

    /**
     * 删除作业
     * @param id 作业ID
     */
    void deleteAssignment(Long id);

    /**
     * 根据ID查找作业
     * @param id 作业ID
     * @return 作业对象
     */
    Assignment findAssignmentById(Long id);

    /**
     * 获取所有作业（学生用）
     * @return 作业列表
     */
    List<Assignment> findAllAssignments();

    /**
     * 获取学生的所有作业
     *
     * @param email 学生邮箱
     * @return 作业列表
     */
    List<Assignment> getAssignmentsByEmail(String email);

    /**
     * 获取学生的作业统计数据
     *
     * @param email 学生邮箱
     * @return 统计数据
     */
    Map<String, Object> getAssignmentStats(String email);

    /**
     * 获取学生的作业提交情况
     *
     * @param email 学生邮箱
     * @param status 状态（pending-待完成，completed-已完成）
     * @return 作业提交列表
     */
    List<AssignmentSubmission> getAssignmentSubmissionsByStatus(String email, String status);

    /**
     * 获取学生的所有作业提交
     *
     * @param email 学生邮箱
     * @return 作业提交列表
     */
    List<AssignmentSubmission> getAllAssignmentSubmissions(String email);

    /**
     * 获取学生的单个作业提交
     *
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @return 作业提交
     */
    AssignmentSubmission getStudentSubmission(Long assignmentId, Long studentId);

    /**
     * 提交作业
     *
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @param content 提交内容
     * @param fileUrl 文件URL
     * @return 提交结果
     */
    AssignmentSubmission submitAssignment(Long assignmentId, Long studentId, String content, String fileUrl);

    /**
     * 获取学生的待完成作业
     *
     * @param email 学生邮箱
     * @return 待完成作业列表
     */
    List<Assignment> getPendingAssignments(String email);

    /**
     * 获取学生的已完成作业
     *
     * @param email 学生邮箱
     * @return 已完成作业列表
     */
    List<AssignmentSubmission> getCompletedAssignments(String email);

    /**
     * 统计学生已完成的作业数量
     *
     * @param email 学生邮箱
     * @return 已完成作业数量
     */
    int countCompletedAssignments(String email);

    /**
     * 统计学生待完成的作业数量
     *
     * @param email 学生邮箱
     * @return 待完成作业数量
     */
    int countPendingAssignments(String email);

    /**
     * 获取作业提交列表
     *
     * @param assignmentId 作业ID
     * @param page 页码
     * @param size 每页数量
     * @return 提交列表
     */
    Map<String, Object> getAssignmentSubmissions(Long assignmentId, int page, int size);

    /**
     * 获取提交详情
     *
     * @param submissionId 提交ID
     * @return 提交详情
     */
    Map<String, Object> getSubmissionDetail(Long submissionId);

    /**
     * 创建新作业（教师端接口，通过Map传递数据）
     *
     * @param teacherId 教师ID
     * @param assignmentData 作业数据
     * @return 创建的作业详情
     */
    Map<String, Object> createAssignment(Long teacherId, Map<String, Object> assignmentData);

    /**
     * 教师获取所有作业（带分页）
     *
     * @param teacherId 教师ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的作业列表
     */
    Map<String, Object> getTeacherAssignments(Long teacherId, String keyword, int page, int size);

    /**
     * 获取教师待批改的作业列表（带分页）
     *
     * @param teacherId 教师ID
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的待批改作业列表
     */
    Map<String, Object> getPendingAssignments(Long teacherId, int page, int size);

    /**
     * 获取课程的所有作业
     *
     * @param courseId 课程ID
     * @return 作业列表
     */
    List<Map<String, Object>> getCourseAssignments(Long courseId);

    /**
     * 获取作业详情
     *
     * @param assignmentId 作业ID
     * @return 作业详情
     */
    Map<String, Object> getAssignmentDetail(Long assignmentId);

    /**
     * 验证作业是否属于该教师
     *
     * @param teacherId 教师ID
     * @param assignmentId 作业ID
     * @return 是否属于该教师
     */
    boolean isTeacherAssignment(Long teacherId, Long assignmentId);

    /**
     * 上传作业文件
     *
     * @param assignmentId 作业ID
     * @param file 文件
     * @return 文件URL
     */
    String uploadAssignmentFile(Long assignmentId, MultipartFile file);

    /**
     * 批改作业提交
     *
     * @param submissionId 提交ID
     * @param gradingData 评分数据
     */
    void gradeAssignmentSubmission(Long submissionId, Map<String, Object> gradingData);

    /**
     * 验证教师是否可以批改该提交
     *
     * @param teacherId 教师ID
     * @param submissionId 提交ID
     * @return 是否可以批改
     */
    boolean canTeacherGradeSubmission(Long teacherId, Long submissionId);

    /**
     * 统计教师待批改作业数量
     *
     * @param teacherId 教师ID
     * @return 待批改作业数量
     */
    long countPendingAssignments(Long teacherId);

    /**
     * 统计所有作业数量（管理员使用）
     *
     * @return 作业总数
     */
    long countAllAssignments();

    /**
     * 计算作业数量增长率（管理员使用）
     *
     * @return 增长率百分比
     */
    long calculateAssignmentGrowthRate();

    /**
     * 获取教师的作业统计数据
     *
     * @param teacherId 教师ID
     * @return 统计数据
     */
    Map<String, Object> getTeacherAssignmentStatistics(Long teacherId);

    /**
     * 批量删除作业
     *
     * @param assignmentIds 作业ID列表
     */
    void batchDeleteAssignments(List<Long> assignmentIds);

    /**
     * 批量发布作业
     *
     * @param assignmentIds 作业ID列表
     */
    void batchPublishAssignments(List<Long> assignmentIds);
}
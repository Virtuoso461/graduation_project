package com.example.backend.service;

import com.example.backend.dto.ExamStatsDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 考试服务接口
 * 提供考试相关的业务逻辑操作
 */
public interface ExamService {

    /**
     * 创建考试
     *
     * @param exam 考试对象
     * @return 创建后的考试对象
     */
    Exam createExam(Exam exam);

    /**
     * 更新考试
     *
     * @param examId 考试ID
     * @param updatedExam 更新后的考试对象
     * @return 更新后的考试对象
     */
    Exam updateExam(Long examId, Exam updatedExam);

    /**
     * 删除考试
     *
     * @param examId 考试ID
     */
    void deleteExam(Long examId);

    /**
     * 根据ID获取考试
     *
     * @param examId 考试ID
     * @return 考试对象
     */
    Exam getExamById(Long examId);

    /**
     * 获取所有考试
     *
     * @return 考试列表
     */
    List<Exam> getAllExams();

    /**
     * 根据创建者邮箱获取考试
     *
     * @param creatorEmail 创建者邮箱
     * @return 考试列表
     */
    List<Exam> getExamsByCreator(String creatorEmail);

    /**
     * 获取可参加的考试
     *
     * @return 可参加的考试列表
     */
    List<Exam> getAvailableExams();

    /**
     * 提交考试结果
     *
     * @param examResult 考试结果对象
     * @return 提交后的考试结果
     */
    ExamResult submitExamResult(ExamResult examResult);

    /**
     * 获取考试结果
     *
     * @param examId 考试ID
     * @param userEmail 用户邮箱
     * @return 考试结果
     */
    ExamResult getExamResult(Long examId, String userEmail);

    /**
     * 获取用户完成的所有考试
     *
     * @param userEmail 用户邮箱
     * @return 完成的考试结果列表
     */
    List<ExamResult> getCompletedExams(String userEmail);

    /**
     * 获取考试统计数据
     *
     * @param userEmail 用户邮箱
     * @return 考试统计数据
     */
    Map<String, Object> getExamStats(String userEmail);

    /**
     * 根据日期范围获取考试结果
     *
     * @param userEmail 用户邮箱
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 考试结果列表
     */
    List<ExamResult> getExamResultsByDateRange(String userEmail, Date startDate, Date endDate);

    /**
     * 获取考试题目
     *
     * @param examId 考试ID
     * @return 题目列表
     */
    List<Map<String, Object>> getExamQuestions(Long examId);

    /**
     * 开始考试
     *
     * @param examId 考试ID
     * @param userEmail 用户邮箱
     * @return 考试结果对象（初始状态）
     */
    ExamResult startExam(Long examId, String userEmail);

    /**
     * 提交考试答案
     *
     * @param examId 考试ID
     * @param userEmail 用户邮箱
     * @param answers 答案列表
     * @return 考试结果
     */
    ExamResult submitExamAnswers(Long examId, String userEmail, List<Map<String, Object>> answers);

    /**
     * 添加考试附件
     *
     * @param examId 考试ID
     * @param userEmail 用户邮箱
     * @param fileUrl 文件URL
     */
    void addExamAttachment(Long examId, String userEmail, String fileUrl);

    /**
     * 获取学生考试统计数据
     *
     * @param userEmail 学生邮箱
     * @return 统计数据
     */
    Map<String, Object> getStudentExamStatistics(String userEmail);

    /**
     * 获取学生错题集
     *
     * @param userEmail 学生邮箱
     * @return 错题列表
     */
    List<Map<String, Object>> getStudentWrongQuestions(String userEmail);

    /**
     * 获取学生知识点掌握情况
     *
     * @param userEmail 学生邮箱
     * @return 知识点掌握情况
     */
    Map<String, Object> getStudentKnowledgePoints(String userEmail);

    /**
     * 获取考试排名
     *
     * @param examId 考试ID
     * @param userEmail 用户邮箱
     * @return 排名数据
     */
    Map<String, Object> getExamRanking(Long examId, String userEmail);

    /**
     * 统计学生已完成的考试数量
     *
     * @param userEmail 学生邮箱
     * @return 已完成考试数量
     */
    int countCompletedExams(String userEmail);

    /**
     * 计算学生的平均考试成绩
     *
     * @param userEmail 学生邮箱
     * @return 平均成绩
     */
    double calculateAverageScore(String userEmail);

    /**
     * 获取学生的考试统计数据（详细版本）
     *
     * @param userEmail 学生邮箱
     * @return 统计数据
     */
    Map<String, Object> getDetailedExamStats(String userEmail);

    // 教师相关方法

    /**
     * 获取教师的考试统计数据
     *
     * @param teacherId 教师ID
     * @return 统计数据
     */
    Map<String, Object> getTeacherExamStatistics(Long teacherId);

    /**
     * 获取单个考试的详细统计数据
     *
     * @param examId 考试ID
     * @return 统计数据
     */
    Map<String, Object> getExamDetailStatistics(Long examId);

    /**
     * 批量删除考试
     *
     * @param examIds 考试ID列表
     */
    void batchDeleteExams(List<Long> examIds);

    /**
     * 批量发布考试
     *
     * @param examIds 考试ID列表
     */
    void batchPublishExams(List<Long> examIds);

    /**
     * 导出考试成绩
     *
     * @param examId 考试ID
     * @return 导出文件的URL
     */
    String exportExamResults(Long examId);

    /**
     * 检查考试是否属于教师
     *
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @return 是否属于教师
     */
    boolean isTeacherExam(Long teacherId, Long examId);

    /**
     * 创建新考试（教师端接口）
     *
     * @param teacherId 教师ID
     * @param examData 考试数据
     * @return 创建的考试
     */
    Map<String, Object> createExam(Long teacherId, Map<String, Object> examData);

    /**
     * 教师获取所有考试（带分页）
     *
     * @param teacherId 教师ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的考试列表
     */
    Map<String, Object> getTeacherExams(Long teacherId, String keyword, int page, int size);

    /**
     * 获取待批改的考试列表
     *
     * @param teacherId 教师ID
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的待批改考试列表
     */
    Map<String, Object> getPendingExams(Long teacherId, int page, int size);

    /**
     * 获取课程的所有考试
     *
     * @param courseId 课程ID
     * @return 考试列表
     */
    List<Map<String, Object>> getCourseExams(Long courseId);

    /**
     * 获取考试详情
     *
     * @param examId 考试ID
     * @return 考试详情
     */
    Map<String, Object> getExamDetail(Long examId);

    /**
     * 上传考试文件
     *
     * @param examId 考试ID
     * @param file 文件
     * @return 文件URL
     */
    String uploadExamFile(Long examId, MultipartFile file);

    /**
     * 批改考试提交
     *
     * @param submissionId 提交ID
     * @param gradingData 评分数据
     */
    void gradeExamSubmission(Long submissionId, Map<String, Object> gradingData);

    /**
     * 验证教师是否可以批改该提交
     *
     * @param teacherId 教师ID
     * @param submissionId 提交ID
     * @return 是否可以批改
     */
    boolean canTeacherGradeSubmission(Long teacherId, Long submissionId);

    /**
     * 发布考试
     *
     * @param examId 考试ID
     */
    void publishExam(Long examId);

    /**
     * 统计教师待批改考试数量
     *
     * @param teacherId 教师ID
     * @return 待批改考试数量
     */
    long countPendingExams(Long teacherId);

    /**
     * 获取考试提交列表
     *
     * @param examId 考试ID
     * @param page 页码
     * @param size 每页数量
     * @return 提交列表
     */
    Map<String, Object> getExamSubmissions(Long examId, int page, int size);

    /**
     * 获取提交详情
     *
     * @param submissionId 提交ID
     * @return 提交详情
     */
    Map<String, Object> getSubmissionDetail(Long submissionId);

    /**
     * 获取考试排名（教师视角）
     *
     * @param examId 考试ID
     * @return 排名数据
     */
    Map<String, Object> getTeacherExamRanking(Long examId);

    /**
     * 更新考试（教师端）
     *
     * @param examId 考试ID
     * @param examData 考试数据
     * @return 更新后的考试
     */
    Map<String, Object> updateExam(Long examId, Map<String, Object> examData);

    // 管理员相关方法

    /**
     * 统计所有考试数量（管理员使用）
     *
     * @return 考试总数
     */
    long countAllExams();

    /**
     * 计算考试数量增长率（管理员使用）
     *
     * @return 增长率百分比
     */
    long calculateExamGrowthRate();
}
package com.example.backend.service;

import java.util.List;
import java.util.Map;

/**
 * 学生管理服务接口
 */
public interface StudentService {

    /**
     * 获取教师的所有学生
     *
     * @param teacherId 教师ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的学生列表
     */
    Map<String, Object> getTeacherStudents(Long teacherId, String keyword, int page, int size);

    /**
     * 获取课程的所有学生
     *
     * @param courseId 课程ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的学生列表
     */
    Map<String, Object> getCourseStudents(Long courseId, String keyword, int page, int size);

    /**
     * 验证学生是否在教师的课程中
     *
     * @param teacherId 教师ID
     * @param studentId 学生ID
     * @return 是否在教师课程中
     */
    boolean isTeacherStudent(Long teacherId, Long studentId);

    /**
     * 获取学生详细信息
     *
     * @param studentId 学生ID
     * @param teacherId 教师ID
     * @return 学生详细信息
     */
    Map<String, Object> getStudentDetail(Long studentId, Long teacherId);

    /**
     * 获取学生成绩
     *
     * @param studentId 学生ID
     * @param teacherId 教师ID
     * @return 学生各课程成绩
     */
    List<Map<String, Object>> getStudentGrades(Long studentId, Long teacherId);

    /**
     * 添加学生备注
     *
     * @param studentId 学生ID
     * @param teacherId 教师ID
     * @param note 备注内容
     */
    void addStudentNote(Long studentId, Long teacherId, String note);

    /**
     * 统计学生已选课程数量
     *
     * @param email 学生邮箱
     * @return 已选课程数量
     */
    int countEnrolledCourses(String email);

    /**
     * 获取学生学习时长统计
     *
     * @param email 学生邮箱
     * @return 学习时长统计数据
     */
    Map<String, Object> getStudyTimeStats(String email);

    /**
     * 修改密码
     *
     * @param email 学生邮箱
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(String email, String oldPassword, String newPassword);

    /**
     * 获取学生学习进度
     *
     * @param email 学生邮箱
     * @param courseId 课程ID
     * @return 学习进度数据
     */
    Map<String, Object> getStudyProgress(String email, Long courseId);

    /**
     * 获取学生在特定课程的学习进度
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学习进度数据
     */
    Map<String, Object> getStudentCourseProgress(Long studentId, Long courseId);

    /**
     * 获取学生在所有课程的总体学习进度
     *
     * @param studentId 学生ID
     * @param teacherId 教师ID
     * @return 学习进度数据
     */
    Map<String, Object> getStudentOverallProgress(Long studentId, Long teacherId);

    /**
     * 获取学生在特定课程的作业提交情况
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 作业提交列表
     */
    List<Map<String, Object>> getStudentCourseAssignments(Long studentId, Long courseId);

    /**
     * 获取学生在所有课程的作业提交情况
     *
     * @param studentId 学生ID
     * @param teacherId 教师ID
     * @return 作业提交列表
     */
    List<Map<String, Object>> getStudentAllAssignments(Long studentId, Long teacherId);

    /**
     * 获取学生在特定课程的考试成绩
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 考试成绩列表
     */
    List<Map<String, Object>> getStudentCourseExams(Long studentId, Long courseId);

    /**
     * 获取学生在所有课程的考试成绩
     *
     * @param studentId 学生ID
     * @param teacherId 教师ID
     * @return 考试成绩列表
     */
    List<Map<String, Object>> getStudentAllExams(Long studentId, Long teacherId);

    /**
     * 创建学生分组
     *
     * @param teacherId 教师ID
     * @param groupData 分组数据
     * @return 创建的分组
     */
    Map<String, Object> createStudentGroup(Long teacherId, Map<String, Object> groupData);

    /**
     * 获取学生分组列表
     *
     * @param teacherId 教师ID
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页数量
     * @return 分组列表
     */
    Map<String, Object> getStudentGroups(Long teacherId, String keyword, int page, int size);

    /**
     * 更新分组信息
     *
     * @param groupId 分组ID
     * @param groupData 分组数据
     * @return 更新后的分组
     */
    Map<String, Object> updateStudentGroup(Long groupId, Map<String, Object> groupData);

    /**
     * 删除分组
     *
     * @param groupId 分组ID
     */
    void deleteStudentGroup(Long groupId);

    /**
     * 验证分组是否属于教师
     *
     * @param teacherId 教师ID
     * @param groupId 分组ID
     * @return 是否属于教师
     */
    boolean isTeacherGroup(Long teacherId, Long groupId);
}

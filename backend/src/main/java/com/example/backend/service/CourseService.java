package com.example.backend.service;

import com.example.backend.entity.Course;
import com.example.backend.entity.CourseCategory;
import com.example.backend.entity.CourseChapter;
import com.example.backend.entity.CourseEnrollment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 课程服务接口
 * 提供课程相关的业务逻辑操作
 */
public interface CourseService {

    /**
     * 获取所有已审核通过的课程
     *
     * @return 已审核通过的课程列表
     */
    List<Course> getApprovedCourses();

    /**
     * 获取用户已选修的课程
     *
     * @param userEmail 用户邮箱
     * @return 选修记录列表
     */
    List<CourseEnrollment> getEnrolledCourses(String userEmail);

    /**
     * 判断用户是否已选修某课程
     *
     * @param userEmail 用户邮箱
     * @param courseId 课程ID
     * @return 是否已选修
     */
    boolean isEnrolled(String userEmail, Long courseId);

    /**
     * 用户选修课程
     *
     * @param userEmail 用户邮箱
     * @param courseId 课程ID
     * @return 选修记录
     */
    CourseEnrollment enrollCourse(String userEmail, Long courseId);

    /**
     * 获取用户已完成的课程
     *
     * @param userEmail 用户邮箱
     * @return 已完成的选修记录列表
     */
    List<CourseEnrollment> getCompletedCourses(String userEmail);

    /**
     * 获取用户学习中的课程
     *
     * @param userEmail 用户邮箱
     * @return 学习中的选修记录列表
     */
    List<CourseEnrollment> getInProgressCourses(String userEmail);

    /**
     * 更新课程学习进度
     *
     * @param userEmail 用户邮箱
     * @param courseId 课程ID
     * @param progress 进度百分比
     * @param chapterId 当前章节ID
     * @param completed 是否完成
     * @return 更新后的选修记录
     */
    CourseEnrollment updateCourseProgress(String userEmail, Long courseId, Integer progress,
                                         Long chapterId, Boolean completed);

    /**
     * 获取课程章节列表
     *
     * @param courseId 课程ID
     * @return 章节列表，每个章节以Map形式返回
     */
    List<Map<String, Object>> getCourseChapters(Long courseId);

    /**
     * 获取章节详情
     *
     * @param chapterId 章节ID
     * @return 章节对象
     */
    CourseChapter getChapterById(Long chapterId);

    /**
     * 记录学习时间
     *
     * @param userEmail 用户邮箱
     * @param courseId 课程ID
     * @param minutes 学习时间（分钟）
     * @param chapterId 章节ID
     * @return 学习时间记录结果
     */
    Map<String, Object> recordStudyTime(String userEmail, Long courseId, Integer minutes, Long chapterId);

    /**
     * 获取课程统计数据
     *
     * @param courseId 课程ID
     * @return 统计数据
     */
    Map<String, Object> getCourseStatistics(Long courseId);

    /**
     * 获取用户课程统计数据
     *
     * @param userEmail 用户邮箱
     * @return 统计数据
     */
    Map<String, Object> getCourseStatistics(String userEmail);

    /**
     * 获取学生课程统计数据
     *
     * @param userEmail 学生邮箱
     * @return 统计数据
     */
    Map<String, Object> getStudentCourseStatistics(String userEmail);

    /**
     * 获取课程成绩单
     *
     * @param userEmail 用户邮箱
     * @return 成绩单数据
     */
    List<Map<String, Object>> getCourseTranscript(String userEmail);

    /**
     * 获取课程推荐
     *
     * @param userEmail 用户邮箱
     * @param limit 限制条数
     * @return 推荐课程列表
     */
    List<Map<String, Object>> getRecommendedCourses(String userEmail, Integer limit);

    /**
     * 获取课程列表
     *
     * @param userId 可选的用户ID，如果提供则返回用户已选课程，否则返回所有课程
     * @return 课程列表
     */
    List<Course> getCourses(Long userId);

    /**
     * 根据ID获取课程详情
     *
     * @param id 课程ID
     * @return 课程对象
     */
    Course getCourseById(Long id);

    /**
     * 更新课程信息
     *
     * @param course 课程对象
     * @return 更新后的课程对象
     */
    Course updateCourse(Course course);

    /**
     * 创建新课程
     *
     * @param course 课程对象
     * @return 创建后的课程对象
     */
    Course createCourse(Course course);

    /**
     * 删除课程
     *
     * @param id 课程ID
     */
    void deleteCourse(Long id);

    /**
     * 获取所有课程分类
     *
     * @return 分类列表
     */
    List<CourseCategory> getAllCategories();

    /**
     * 根据ID获取课程分类
     *
     * @param id 分类ID
     * @return 分类对象
     */
    CourseCategory getCategoryById(Long id);

    /**
     * 创建课程分类
     *
     * @param category 分类对象
     * @return 创建后的分类对象
     */
    CourseCategory createCategory(CourseCategory category);

    /**
     * 更新课程分类
     *
     * @param category 分类对象
     * @return 更新后的分类对象
     */
    CourseCategory updateCategory(CourseCategory category);

    /**
     * 删除课程分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 检查分类是否被课程使用
     *
     * @param categoryId 分类ID
     * @return 是否被使用
     */
    boolean isCategoryInUse(Long categoryId);

    /* 以下是教师模块所需方法 */

    /**
     * 教师创建新课程
     *
     * @param teacherId 教师ID
     * @param courseData 课程数据
     * @return 创建的课程对象
     */
    Course createCourse(Long teacherId, Course courseData);

    /**
     * 更新课程信息
     *
     * @param courseId 课程ID
     * @param courseData 课程数据
     * @return 更新后的课程对象
     */
    Course updateCourse(Long courseId, Course courseData);

    /**
     * 获取教师的所有课程
     *
     * @param teacherId 教师ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的课程列表
     */
    Map<String, Object> getTeacherCourses(Long teacherId, String keyword, int page, int size);

    /**
     * 判断课程是否属于教师
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 是否属于教师
     */
    boolean isTeacherCourse(Long teacherId, Long courseId);

    /**
     * 上传课程封面图片
     *
     * @param courseId 课程ID
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadCourseCover(Long courseId, MultipartFile file);

    /**
     * 上传课程资料
     *
     * @param courseId 课程ID
     * @param file 资料文件
     * @return 资料信息
     */
    Map<String, Object> uploadCourseMaterial(Long courseId, MultipartFile file);

    /**
     * 获取课程资料列表
     *
     * @param courseId 课程ID
     * @return 资料列表
     */
    List<Map<String, Object>> getCourseMaterials(Long courseId);

    /**
     * 删除课程资料
     *
     * @param courseId 课程ID
     * @param materialId 资料ID
     */
    void deleteCourseMaterial(Long courseId, Long materialId);

    /**
     * 获取课程学生列表
     *
     * @param courseId 课程ID
     * @return 学生列表
     */
    List<Map<String, Object>> getCourseStudents(Long courseId);

    /**
     * 获取最近的课程活动
     *
     * @param teacherId 教师ID
     * @param limit 限制条数
     * @return 活动列表
     */
    List<Map<String, Object>> getRecentCourseActivities(Long teacherId, int limit);

    /**
     * 统计教师课程总数
     *
     * @param teacherId 教师ID
     * @return 课程总数
     */
    long countTeacherCourses(Long teacherId);

    /**
     * 统计教师所有课程的学生总数
     *
     * @param teacherId 教师ID
     * @return 学生总数
     */
    long countTeacherStudents(Long teacherId);

    /**
     * 计算教师课程完成率
     *
     * @param teacherId 教师ID
     * @return 完成率百分比
     */
    int calculateCourseCompletionRate(Long teacherId);

    /**
     * 计算教师学生增长率
     *
     * @param teacherId 教师ID
     * @return 增长率百分比
     */
    int calculateStudentGrowthRate(Long teacherId);


    /* 以下是管理员模块所需方法 */

    /**
     * 获取待审核课程列表
     *
     * @return 待审核课程列表
     */
    List<Course> getPendingApprovalCourses();

    /**
     * 获取课程审核详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    Map<String, Object> getCourseApprovalDetail(Long courseId);

    /**
     * 批准课程
     *
     * @param courseId 课程ID
     * @param comments 批准备注
     */
    void approveCourse(Long courseId, String comments);

    /**
     * 拒绝课程
     *
     * @param courseId 课程ID
     * @param reason 拒绝原因
     */
    void rejectCourse(Long courseId, String reason);

    /**
     * 统计待审核课程数量
     *
     * @return 待审核课程数量
     */
    long countPendingApprovalCourses();

    /**
     * 统计所有课程数量
     *
     * @return 课程总数
     */
    long countAllCourses();

    /**
     * 计算课程增长率
     *
     * @return 增长率百分比
     */
    long calculateCourseGrowthRate();

    /**
     * 获取最近添加的课程
     *
     * @param limit 限制条数
     * @return 课程列表
     */
    List<Map<String, Object>> findRecentCourses(int limit);

    /**
     * 获取课程详情的Map表示
     *
     * @param course 课程对象
     * @return 课程详情Map
     */
    Map<String, Object> getCourseDetailMap(Course course);

    /**
     * 根据关键词和状态查找课程（带分页）
     *
     * @param keyword 关键词
     * @param status 状态
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的课程列表
     */
    Map<String, Object> findCourses(String keyword, String status, int page, int size);

    /**
     * 获取待审核课程（带分页）
     *
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的待审核课程列表
     */
    Map<String, Object> findPendingCourses(int page, int size);

    /**
     * 统计已审核通过的课程数量
     *
     * @return 已审核通过的课程数量
     */
    long countApprovedCourses();

    /**
     * 统计被拒绝的课程数量
     *
     * @return 被拒绝的课程数量
     */
    long countRejectedCourses();

    /**
     * 按分类统计课程数量
     *
     * @return 分类统计结果
     */
    Map<String, Long> getCourseCountByCategory();

    /**
     * 获取课程分类列表
     *
     * @return 分类列表
     */
    List<String> getCourseCategories();

    /**
     * 获取课程创建趋势
     *
     * @param days 最近天数
     * @return 日期-数量映射
     */
    Map<String, Long> getCourseCreationTrend(int days);

    /**
     * 获取课程评分统计
     *
     * @return 评分统计数据
     */
    Map<String, Double> getCourseRatingStats();

    /**
     * 更新最后访问的章节
     *
     * @param userEmail 用户邮箱
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 更新后的选修记录
     */
    CourseEnrollment updateLastAccessedChapter(String userEmail, Long courseId, Long chapterId);

    /**
     * 获取章节的小节列表
     *
     * @param chapterId 章节ID
     * @return 小节列表
     */
    List<Map<String, Object>> getChapterSections(Long chapterId);

    /**
     * 创建章节小节
     *
     * @param chapterId 章节ID
     * @param sectionData 小节数据
     * @return 创建的小节
     */
    Map<String, Object> createChapterSection(Long chapterId, Map<String, Object> sectionData);

    /**
     * 更新章节小节
     *
     * @param sectionId 小节ID
     * @param sectionData 小节数据
     * @return 更新后的小节
     */
    Map<String, Object> updateChapterSection(Long sectionId, Map<String, Object> sectionData);

    /**
     * 删除章节小节
     *
     * @param sectionId 小节ID
     */
    void deleteChapterSection(Long sectionId);

    /**
     * 创建课程章节
     *
     * @param courseId 课程ID
     * @param chapterData 章节数据
     * @return 创建的章节
     */
    Map<String, Object> createCourseChapter(Long courseId, Map<String, Object> chapterData);

    /**
     * 更新课程章节
     *
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @param chapterData 章节数据
     * @return 更新后的章节
     */
    Map<String, Object> updateCourseChapter(Long courseId, Long chapterId, Map<String, Object> chapterData);

    /**
     * 删除课程章节
     *
     * @param courseId 课程ID
     * @param chapterId 章节ID
     */
    void deleteCourseChapter(Long courseId, Long chapterId);
}
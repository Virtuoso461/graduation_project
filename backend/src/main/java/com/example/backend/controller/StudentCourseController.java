package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.CourseQueryDTO;
import com.example.backend.dto.CourseProgressDTO;
import com.example.backend.dto.StudyTimeDTO;
import com.example.backend.dto.CourseTranscriptDTO;
import com.example.backend.dto.CourseRecommendationDTO;
import com.example.backend.entity.Course;
import com.example.backend.entity.CourseChapter;
import com.example.backend.entity.CourseEnrollment;
import com.example.backend.service.CourseService;
import com.example.backend.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生课程学习控制器
 * 提供学生课程学习相关的API
 */
@RestController
@RequestMapping("/api/student/courses")
public class StudentCourseController {

    private final CourseService courseService;
    private final StudentService studentService;

    public StudentCourseController(CourseService courseService, StudentService studentService) {
        this.courseService = courseService;
        this.studentService = studentService;
    }

    /**
     * 获取当前登录用户的邮箱
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 获取可选修的课程列表
     *
     * @param queryDTO 查询参数
     * @return 课程列表
     */
    @PostMapping("/available")
    public Result<List<Map<String, Object>>> getAvailableCourses(@RequestBody CourseQueryDTO queryDTO) {
        String userEmail = getCurrentUserEmail();

        // 获取所有已审核通过的课程
        List<Course> allCourses = courseService.getApprovedCourses();

        // 获取学生已选修的课程ID列表
        List<CourseEnrollment> enrolledCourses = courseService.getEnrolledCourses(userEmail);
        Set<Long> enrolledCourseIds = enrolledCourses.stream()
                .map(CourseEnrollment::getCourseId)
                .collect(Collectors.toSet());

        // 过滤出未选修的课程
        List<Course> availableCourses = allCourses.stream()
                .filter(course -> !enrolledCourseIds.contains(course.getId()))
                .collect(Collectors.toList());

        // 应用过滤条件
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            availableCourses = availableCourses.stream()
                    .filter(course -> course.getName().toLowerCase().contains(queryDTO.getKeyword().toLowerCase()) ||
                            (course.getDescription() != null && course.getDescription().toLowerCase().contains(queryDTO.getKeyword().toLowerCase())))
                    .collect(Collectors.toList());
        }

        if (queryDTO.getCategory() != null && !queryDTO.getCategory().isEmpty()) {
            availableCourses = availableCourses.stream()
                    .filter(course -> queryDTO.getCategory().equals(course.getCategory()))
                    .collect(Collectors.toList());
        }

        if (queryDTO.getDifficulty() != null && !queryDTO.getDifficulty().isEmpty()) {
            availableCourses = availableCourses.stream()
                    .filter(course -> queryDTO.getDifficulty().equals(course.getDifficulty()))
                    .collect(Collectors.toList());
        }

        // 转换为前端需要的格式
        List<Map<String, Object>> result = availableCourses.stream()
                .map(courseService::getCourseDetailMap)
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    @GetMapping("/{courseId}")
    public Result<Map<String, Object>> getCourseDetail(@PathVariable Long courseId) {
        String userEmail = getCurrentUserEmail();

        // 获取课程详情
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return Result.failed("课程不存在");
        }

        // 检查课程是否已审核通过
        if (!course.isApproved()) {
            return Result.failed("课程尚未审核通过");
        }

        // 获取课程详情
        Map<String, Object> courseDetail = courseService.getCourseDetailMap(course);

        // 检查学生是否已选修该课程
        boolean isEnrolled = courseService.isEnrolled(userEmail, courseId);
        courseDetail.put("isEnrolled", isEnrolled);

        // 如果已选修，获取学习进度
        if (isEnrolled) {
            Map<String, Object> progress = studentService.getStudyProgress(userEmail, courseId);
            courseDetail.put("progress", progress);
        }

        return Result.success(courseDetail);
    }

    /**
     * 选修课程
     *
     * @param courseId 课程ID
     * @return 选修结果
     */
    @PostMapping("/{courseId}/enroll")
    public Result<Map<String, Object>> enrollCourse(@PathVariable Long courseId) {
        String userEmail = getCurrentUserEmail();

        // 获取课程详情
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return Result.failed("课程不存在");
        }

        // 检查课程是否已审核通过
        if (!course.isApproved()) {
            return Result.failed("课程尚未审核通过");
        }

        // 检查是否已选修
        if (courseService.isEnrolled(userEmail, courseId)) {
            return Result.failed("您已选修该课程");
        }

        // 选修课程
        CourseEnrollment enrollment = courseService.enrollCourse(userEmail, courseId);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("enrollmentId", enrollment.getId());
        result.put("courseId", enrollment.getCourseId());
        result.put("enrollTime", enrollment.getEnrollTime());

        return Result.success(result, "选修成功");
    }

    /**
     * 获取已选修的课程
     *
     * @param queryDTO 查询参数
     * @return 课程列表
     */
    @PostMapping("/enrolled")
    public Result<List<Map<String, Object>>> getEnrolledCourses(@RequestBody CourseQueryDTO queryDTO) {
        String userEmail = getCurrentUserEmail();

        // 获取学生已选修的课程
        List<CourseEnrollment> enrollments;

        if ("completed".equals(queryDTO.getStatus())) {
            enrollments = courseService.getCompletedCourses(userEmail);
        } else if ("in-progress".equals(queryDTO.getStatus())) {
            enrollments = courseService.getInProgressCourses(userEmail);
        } else {
            enrollments = courseService.getEnrolledCourses(userEmail);
        }

        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();

        for (CourseEnrollment enrollment : enrollments) {
            Course course = courseService.getCourseById(enrollment.getCourseId());
            if (course != null) {
                Map<String, Object> courseMap = courseService.getCourseDetailMap(course);

                // 添加选修信息
                courseMap.put("enrollmentId", enrollment.getId());
                courseMap.put("progress", enrollment.getProgress());
                courseMap.put("isCompleted", enrollment.getIsCompleted());
                courseMap.put("enrollTime", enrollment.getEnrollTime());
                courseMap.put("lastAccessTime", enrollment.getLastAccessTime());

                if (enrollment.getLastAccessedChapterId() != null) {
                    courseMap.put("lastAccessedChapterId", enrollment.getLastAccessedChapterId());
                }

                result.add(courseMap);
            }
        }

        return Result.success(result);
    }

    /**
     * 更新课程学习进度
     *
     * @param courseId 课程ID
     * @param progressDTO 进度参数
     * @return 更新结果
     */
    @PostMapping("/{courseId}/progress")
    public Result<Map<String, Object>> updateCourseProgress(
            @PathVariable Long courseId,
            @RequestBody CourseProgressDTO progressDTO) {

        String userEmail = getCurrentUserEmail();

        // 检查是否已选修
        if (!courseService.isEnrolled(userEmail, courseId)) {
            return Result.failed("您尚未选修该课程");
        }

        // 更新进度
        CourseEnrollment enrollment = courseService.updateCourseProgress(
                userEmail,
                courseId,
                progressDTO.getProgress(),
                progressDTO.getChapterId(),
                progressDTO.getCompleted());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("enrollmentId", enrollment.getId());
        result.put("courseId", enrollment.getCourseId());
        result.put("progress", enrollment.getProgress());
        result.put("isCompleted", enrollment.getIsCompleted());
        result.put("lastAccessedChapterId", enrollment.getLastAccessedChapterId());
        result.put("lastAccessTime", enrollment.getLastAccessTime());

        return Result.success(result, "进度更新成功");
    }

    /**
     * 获取课程学习进度
     *
     * @param courseId 课程ID
     * @return 学习进度
     */
    @GetMapping("/{courseId}/progress")
    public Result<Map<String, Object>> getCourseProgress(@PathVariable Long courseId) {
        String userEmail = getCurrentUserEmail();

        // 检查是否已选修
        if (!courseService.isEnrolled(userEmail, courseId)) {
            return Result.failed("您尚未选修该课程");
        }

        // 获取学习进度
        Map<String, Object> progress = studentService.getStudyProgress(userEmail, courseId);

        return Result.success(progress);
    }

    /**
     * 获取课程章节
     *
     * @param courseId 课程ID
     * @return 章节列表
     */
    @GetMapping("/{courseId}/chapters")
    public Result<List<Map<String, Object>>> getCourseChapters(@PathVariable Long courseId) {
        String userEmail = getCurrentUserEmail();

        // 获取课程详情
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return Result.failed("课程不存在");
        }

        // 检查课程是否已审核通过
        if (!course.isApproved()) {
            return Result.failed("课程尚未审核通过");
        }

        // 检查是否已选修
        if (!courseService.isEnrolled(userEmail, courseId)) {
            return Result.failed("您尚未选修该课程");
        }

        // 获取章节列表
        List<Map<String, Object>> chapters = courseService.getCourseChapters(courseId);

        return Result.success(chapters);
    }

    /**
     * 获取章节详情
     *
     * @param chapterId 章节ID
     * @return 章节详情
     */
    @GetMapping("/chapters/{chapterId}")
    public Result<Map<String, Object>> getChapterDetail(@PathVariable Long chapterId) {
        String userEmail = getCurrentUserEmail();

        // 获取章节详情
        CourseChapter chapter = courseService.getChapterById(chapterId);
        if (chapter == null) {
            return Result.failed("章节不存在");
        }

        // 检查是否已选修该课程
        if (!courseService.isEnrolled(userEmail, chapter.getCourseId())) {
            return Result.failed("您尚未选修该课程");
        }

        // 更新最后访问的章节
        courseService.updateLastAccessedChapter(userEmail, chapter.getCourseId(), chapterId);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", chapter.getId());
        result.put("courseId", chapter.getCourseId());
        result.put("title", chapter.getTitle());
        result.put("description", chapter.getDescription());
        result.put("duration", chapter.getDuration());
        result.put("videoUrl", chapter.getVideoUrl());
        result.put("sortOrder", chapter.getSortOrder());

        return Result.success(result);
    }

    /**
     * 记录学习时间
     *
     * @param courseId 课程ID
     * @param timeDTO 学习时间参数
     * @return 记录结果
     */
    @PostMapping("/{courseId}/study-time")
    public Result<Map<String, Object>> recordStudyTime(
            @PathVariable Long courseId,
            @RequestBody StudyTimeDTO timeDTO) {

        String userEmail = getCurrentUserEmail();

        // 检查是否已选修
        if (!courseService.isEnrolled(userEmail, courseId)) {
            return Result.failed("您尚未选修该课程");
        }

        // 记录学习时间
        Map<String, Object> result = courseService.recordStudyTime(
                userEmail,
                courseId,
                timeDTO.getMinutes(),
                timeDTO.getChapterId());

        return Result.success(result, "学习时间记录成功");
    }

    /**
     * 获取课程统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCourseStatistics() {
        String userEmail = getCurrentUserEmail();

        // 获取统计数据
        Map<String, Object> statistics = courseService.getCourseStatistics(userEmail);

        return Result.success(statistics);
    }

    /**
     * 获取课程成绩单
     *
     * @return 成绩单
     */
    @GetMapping("/transcript")
    public Result<List<CourseTranscriptDTO>> getCourseTranscript() {
        String userEmail = getCurrentUserEmail();
        
        // 获取成绩单
        List<Map<String, Object>> transcript = courseService.getCourseTranscript(userEmail);
        
        // 将Map转换为DTO对象
        List<CourseTranscriptDTO> transcriptDTOs = transcript.stream()
                .map(map -> {
                    CourseTranscriptDTO dto = new CourseTranscriptDTO();
                    dto.setCourseId((Long) map.get("courseId"));
                    dto.setCourseName((String) map.get("courseName"));
                    dto.setCategory((String) map.get("category"));
                    dto.setCredits((Integer) map.get("credits"));
                    dto.setGrade((String) map.get("grade"));
                    dto.setIsCompleted((Boolean) map.get("isCompleted"));
                    dto.setProgress((Integer) map.get("progress"));
                    dto.setEnrollTime((String) map.get("enrollTime"));
                    dto.setCompletionDate((String) map.get("completionDate"));
                    dto.setTotalStudyTime((Integer) map.get("totalStudyTime"));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Result.success(transcriptDTOs);
    }

    /**
     * 获取课程推荐
     *
     * @param recommendationDTO 推荐参数
     * @return 推荐课程列表
     */
    @PostMapping("/recommendations")
    public Result<List<Map<String, Object>>> getCourseRecommendations(@RequestBody CourseRecommendationDTO recommendationDTO) {
        String userEmail = getCurrentUserEmail();
        
        // 获取推荐课程
        List<Map<String, Object>> recommendations = courseService.getRecommendedCourses(userEmail, recommendationDTO.getLimit());
        
        return Result.success(recommendations);
    }
}

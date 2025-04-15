package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.ExamService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 教师考试管理控制器
 * 提供考试管理功能
 */
@RestController
@RequestMapping("/api/teacher/exams")
public class TeacherExamsController {

    private final UserService userService;
    private final ExamService examService;
    private final CourseService courseService;

    @Autowired
    public TeacherExamsController(UserService userService, ExamService examService, CourseService courseService) {
        this.userService = userService;
        this.examService = examService;
        this.courseService = courseService;
    }

    /**
     * 获取当前登录的教师ID
     */
    private Long getCurrentTeacherId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findByUsername(userEmail);

        if (user == null || user.getRole() != Role.TEACHER) {
            throw new SecurityException("非教师账号，无权访问");
        }

        return user.getId();
    }

    /**
     * 获取教师的所有考试列表
     *
     * @return 考试列表
     */
    @GetMapping()
    public Result<Map<String, Object>> getAllExams(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> exams = examService.getTeacherExams(teacherId, keyword, page, size);
            return Result.success(exams);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取考试列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取待批改的考试列表
     *
     * @return 待批改考试列表
     */
    @GetMapping("/pending")
    public Result<Map<String, Object>> getPendingExams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> pendingExams = examService.getPendingExams(teacherId, page, size);
            return Result.success(pendingExams);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取待批改考试失败: " + e.getMessage());
        }
    }

    /**
     * 按课程获取考试列表
     *
     * @param courseId 课程ID
     * @return 该课程的考试列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<Map<String, Object>>> getExamsByCourse(@PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            List<Map<String, Object>> exams = examService.getCourseExams(courseId);
            return Result.success(exams);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程考试失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试详情
     *
     * @param examId 考试ID
     * @return 考试详细信息
     */
    @GetMapping("/{examId}")
    public Result<Map<String, Object>> getExamDetail(@PathVariable Long examId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证考试是否属于该教师
            boolean examOwned = examService.isTeacherExam(teacherId, examId);
            if (!examOwned) {
                return Result.forbidden("您不是该考试的创建者，无权查看");
            }

            Map<String, Object> examDetail = examService.getExamDetail(examId);
            return Result.success(examDetail);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取考试详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新考试
     *
     * @param examData 考试数据
     * @return 创建结果
     */
    @PostMapping()
    public Result<Map<String, Object>> createExam(@RequestBody Map<String, Object> examData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            Long courseId = ((Number) examData.get("courseId")).longValue();
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权创建考试");
            }

            Map<String, Object> createdExam = examService.createExam(teacherId, examData);
            return Result.success(createdExam, "考试创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建考试失败: " + e.getMessage());
        }
    }

    /**
     * 上传考试相关资料
     *
     * @param examId 考试ID
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/{examId}/upload")
    public Result<String> uploadExamFile(@PathVariable Long examId, @RequestParam MultipartFile file) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证考试是否属于该教师
            boolean examOwned = examService.isTeacherExam(teacherId, examId);
            if (!examOwned) {
                return Result.forbidden("您不是该考试的创建者，无权上传文件");
            }

            String fileUrl = examService.uploadExamFile(examId, file);
            return Result.success(fileUrl, "文件上传成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 更新考试
     *
     * @param examId 考试ID
     * @param examData 考试数据
     * @return 更新结果
     */
    @PutMapping("/{examId}")
    public Result<Map<String, Object>> updateExam(
            @PathVariable Long examId,
            @RequestBody Map<String, Object> examData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证考试是否属于该教师
            boolean examOwned = examService.isTeacherExam(teacherId, examId);
            if (!examOwned) {
                return Result.forbidden("您不是该考试的创建者，无权更新");
            }

            Map<String, Object> updatedExam = examService.updateExam(examId, examData);
            return Result.success(updatedExam, "考试更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新考试失败: " + e.getMessage());
        }
    }

    /**
     * 删除考试
     *
     * @param examId 考试ID
     * @return 删除结果
     */
    @DeleteMapping("/{examId}")
    public Result<String> deleteExam(@PathVariable Long examId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证考试是否属于该教师
            boolean examOwned = examService.isTeacherExam(teacherId, examId);
            if (!examOwned) {
                return Result.forbidden("您不是该考试的创建者，无权删除");
            }

            examService.deleteExam(examId);
            return Result.success("考试删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除考试失败: " + e.getMessage());
        }
    }

    /**
     * 发布考试
     *
     * @param examId 考试ID
     * @return 发布结果
     */
    @PostMapping("/{examId}/publish")
    public Result<String> publishExam(@PathVariable Long examId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证考试是否属于该教师
            boolean examOwned = examService.isTeacherExam(teacherId, examId);
            if (!examOwned) {
                return Result.forbidden("您不是该考试的创建者，无权发布");
            }

            examService.publishExam(examId);
            return Result.success("考试已发布");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("发布考试失败: " + e.getMessage());
        }
    }

    /**
     * 批改学生考试
     *
     * @param submissionId 学生提交ID
     * @param gradingData 评分数据
     * @return 批改结果
     */
    @PostMapping("/submissions/{submissionId}/grade")
    public Result<String> gradeExamSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> gradingData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证提交是否属于该教师的考试
            boolean hasAuthority = examService.canTeacherGradeSubmission(teacherId, submissionId);
            if (!hasAuthority) {
                return Result.forbidden("您不是该考试的授课教师，无权批改");
            }

            examService.gradeExamSubmission(submissionId, gradingData);
            return Result.success("考试批改成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批改考试失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getExamStatistics() {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> statistics = examService.getTeacherExamStatistics(teacherId);
            return Result.success(statistics);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个考试的统计数据
     *
     * @param examId 考试ID
     * @return 统计数据
     */
    @GetMapping("/{examId}/statistics")
    public Result<Map<String, Object>> getExamDetailStatistics(@PathVariable Long examId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证考试是否属于该教师
            if (!examService.isTeacherExam(teacherId, examId)) {
                return Result.forbidden("您不是该考试的创建者，无权查看");
            }

            Map<String, Object> statistics = examService.getExamDetailStatistics(examId);
            return Result.success(statistics);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取考试统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取测试提交列表
     *
     * @param examId 测试ID
     * @param page 页码
     * @param size 每页数量
     * @return 提交列表
     */
    @GetMapping("/{examId}/submissions")
    public Result<Map<String, Object>> getExamSubmissions(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证测试是否属于该教师
            boolean examOwned = examService.isTeacherExam(teacherId, examId);
            if (!examOwned) {
                return Result.forbidden("您不是该测试的创建者，无权查看");
            }

            Map<String, Object> submissions = examService.getExamSubmissions(examId, page, size);
            return Result.success(submissions);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取测试提交列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取提交详情
     *
     * @param submissionId 提交ID
     * @return 提交详情
     */
    @GetMapping("/submissions/{submissionId}")
    public Result<Map<String, Object>> getSubmissionDetail(@PathVariable Long submissionId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证提交是否属于该教师的测试
            boolean hasAuthority = examService.canTeacherGradeSubmission(teacherId, submissionId);
            if (!hasAuthority) {
                return Result.forbidden("您不是该测试的授课教师，无权查看");
            }

            Map<String, Object> submission = examService.getSubmissionDetail(submissionId);
            return Result.success(submission);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取提交详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取测试排名
     *
     * @param examId 测试ID
     * @return 排名数据
     */
    @GetMapping("/{examId}/ranking")
    public Result<Map<String, Object>> getExamRanking(@PathVariable Long examId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证测试是否属于该教师
            boolean examOwned = examService.isTeacherExam(teacherId, examId);
            if (!examOwned) {
                return Result.forbidden("您不是该测试的创建者，无权查看");
            }

            Map<String, Object> ranking = examService.getTeacherExamRanking(examId);
            return Result.success(ranking);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取测试排名失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除考试
     *
     * @param request 请求数据，包含考试ID列表
     * @return 删除结果
     */
    @PostMapping("/batch-delete")
    public Result<String> batchDeleteExams(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = getCurrentTeacherId();

            List<Long> examIds = (List<Long>) request.get("examIds");
            if (examIds == null || examIds.isEmpty()) {
                return Result.validateFailed("考试ID列表不能为空");
            }

            // 验证所有考试是否属于该教师
            for (Long examId : examIds) {
                if (!examService.isTeacherExam(teacherId, examId)) {
                    return Result.forbidden("包含非您创建的考试，无权删除");
                }
            }

            examService.batchDeleteExams(examIds);
            return Result.success("批量删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量发布考试
     *
     * @param request 请求数据，包含考试ID列表
     * @return 发布结果
     */
    @PostMapping("/batch-publish")
    public Result<String> batchPublishExams(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = getCurrentTeacherId();

            List<Long> examIds = (List<Long>) request.get("examIds");
            if (examIds == null || examIds.isEmpty()) {
                return Result.validateFailed("考试ID列表不能为空");
            }

            // 验证所有考试是否属于该教师
            for (Long examId : examIds) {
                if (!examService.isTeacherExam(teacherId, examId)) {
                    return Result.forbidden("包含非您创建的考试，无权发布");
                }
            }

            examService.batchPublishExams(examIds);
            return Result.success("批量发布成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批量发布失败: " + e.getMessage());
        }
    }

    /**
     * 导出考试成绩
     *
     * @param examId 考试ID
     * @return 导出结果
     */
    @GetMapping("/{examId}/export-results")
    public Result<String> exportExamResults(@PathVariable Long examId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证考试是否属于该教师
            if (!examService.isTeacherExam(teacherId, examId)) {
                return Result.forbidden("您不是该考试的创建者，无权导出");
            }

            String exportUrl = examService.exportExamResults(examId);
            return Result.success(exportUrl, "成绩导出成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("导出成绩失败: " + e.getMessage());
        }
    }
}

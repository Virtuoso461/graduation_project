package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.AssignmentService;
import com.example.backend.service.CourseService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 教师作业管理控制器
 * 提供作业管理功能
 */
@RestController
@RequestMapping("/api/teacher/assignments")
public class TeacherAssignmentsController {

    private final UserService userService;
    private final AssignmentService assignmentService;
    private final CourseService courseService;

    @Autowired
    public TeacherAssignmentsController(UserService userService, AssignmentService assignmentService,
                                        CourseService courseService) {
        this.userService = userService;
        this.assignmentService = assignmentService;
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
     * 获取教师的所有作业列表
     *
     * @return 作业列表
     */
    @GetMapping()
    public Result<Map<String, Object>> getAllAssignments(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> assignments = assignmentService.getTeacherAssignments(teacherId, keyword, page, size);
            return Result.success(assignments);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取作业列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取待批改的作业列表
     *
     * @return 待批改作业列表
     */
    @GetMapping("/pending")
    public Result<Map<String, Object>> getPendingAssignments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> pendingAssignments = assignmentService.getPendingAssignments(teacherId, page, size);
            return Result.success(pendingAssignments);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取待批改作业失败: " + e.getMessage());
        }
    }

    /**
     * 按课程获取作业列表
     *
     * @param courseId 课程ID
     * @return 该课程的作业列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<Map<String, Object>>> getAssignmentsByCourse(@PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            List<Map<String, Object>> assignments = assignmentService.getCourseAssignments(courseId);
            return Result.success(assignments);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程作业失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业详情
     *
     * @param assignmentId 作业ID
     * @return 作业详细信息
     */
    @GetMapping("/{assignmentId}")
    public Result<Map<String, Object>> getAssignmentDetail(@PathVariable Long assignmentId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证作业是否属于该教师
            boolean assignmentOwned = assignmentService.isTeacherAssignment(teacherId, assignmentId);
            if (!assignmentOwned) {
                return Result.forbidden("您不是该作业的创建者，无权查看");
            }

            Map<String, Object> assignmentDetail = assignmentService.getAssignmentDetail(assignmentId);
            return Result.success(assignmentDetail);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取作业详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新作业
     *
     * @param assignmentData 作业数据
     * @return 创建结果
     */
    @PostMapping()
    public Result<Map<String, Object>> createAssignment(@RequestBody Map<String, Object> assignmentData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            Long courseId = ((Number) assignmentData.get("courseId")).longValue();
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权创建作业");
            }

            Map<String, Object> createdAssignment = assignmentService.createAssignment(teacherId, assignmentData);
            return Result.success(createdAssignment, "作业创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建作业失败: " + e.getMessage());
        }
    }

    /**
     * 上传作业相关资料
     *
     * @param assignmentId 作业ID
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/{assignmentId}/upload")
    public Result<String> uploadAssignmentFile(@PathVariable Long assignmentId, @RequestParam MultipartFile file) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证作业是否属于该教师
            boolean assignmentOwned = assignmentService.isTeacherAssignment(teacherId, assignmentId);
            if (!assignmentOwned) {
                return Result.forbidden("您不是该作业的创建者，无权上传文件");
            }

            String fileUrl = assignmentService.uploadAssignmentFile(assignmentId, file);
            return Result.success(fileUrl, "文件上传成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 更新作业
     *
     * @param assignmentId 作业ID
     * @param assignmentData 作业数据
     * @return 更新结果
     */
    @PutMapping("/{assignmentId}")
    public Result<Map<String, Object>> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody Map<String, Object> assignmentData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证作业是否属于该教师
            boolean assignmentOwned = assignmentService.isTeacherAssignment(teacherId, assignmentId);
            if (!assignmentOwned) {
                return Result.forbidden("您不是该作业的创建者，无权更新");
            }

            Map<String, Object> updatedAssignment = assignmentService.updateAssignment(assignmentId, assignmentData);
            return Result.success(updatedAssignment, "作业更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新作业失败: " + e.getMessage());
        }
    }

    /**
     * 删除作业
     *
     * @param assignmentId 作业ID
     * @return 删除结果
     */
    @DeleteMapping("/{assignmentId}")
    public Result<String> deleteAssignment(@PathVariable Long assignmentId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证作业是否属于该教师
            boolean assignmentOwned = assignmentService.isTeacherAssignment(teacherId, assignmentId);
            if (!assignmentOwned) {
                return Result.forbidden("您不是该作业的创建者，无权删除");
            }

            assignmentService.deleteAssignment(assignmentId);
            return Result.success("作业删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除作业失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业提交列表
     *
     * @param assignmentId 作业ID
     * @param page 页码
     * @param size 每页数量
     * @return 提交列表
     */
    @GetMapping("/{assignmentId}/submissions")
    public Result<Map<String, Object>> getAssignmentSubmissions(
            @PathVariable Long assignmentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证作业是否属于该教师
            boolean assignmentOwned = assignmentService.isTeacherAssignment(teacherId, assignmentId);
            if (!assignmentOwned) {
                return Result.forbidden("您不是该作业的创建者，无权查看");
            }

            Map<String, Object> submissions = assignmentService.getAssignmentSubmissions(assignmentId, page, size);
            return Result.success(submissions);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取作业提交列表失败: " + e.getMessage());
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

            // 验证提交是否属于该教师的作业
            boolean hasAuthority = assignmentService.canTeacherGradeSubmission(teacherId, submissionId);
            if (!hasAuthority) {
                return Result.forbidden("您不是该作业的授课教师，无权查看");
            }

            Map<String, Object> submission = assignmentService.getSubmissionDetail(submissionId);
            return Result.success(submission);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取提交详情失败: " + e.getMessage());
        }
    }

    /**
     * 批改学生作业
     *
     * @param submissionId 学生提交ID
     * @param gradingData 评分数据
     * @return 批改结果
     */
    @PostMapping("/submissions/{submissionId}/grade")
    public Result<String> gradeAssignmentSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> gradingData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证提交是否属于该教师的作业
            boolean hasAuthority = assignmentService.canTeacherGradeSubmission(teacherId, submissionId);
            if (!hasAuthority) {
                return Result.forbidden("您不是该作业的授课教师，无权批改");
            }

            assignmentService.gradeAssignmentSubmission(submissionId, gradingData);
            return Result.success("作业批改成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批改作业失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getAssignmentStatistics() {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> statistics = assignmentService.getTeacherAssignmentStatistics(teacherId);
            return Result.success(statistics);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除作业
     *
     * @param request 请求数据，包含作业ID列表
     * @return 删除结果
     */
    @PostMapping("/batch-delete")
    public Result<String> batchDeleteAssignments(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = getCurrentTeacherId();

            List<Long> assignmentIds = (List<Long>) request.get("assignmentIds");
            if (assignmentIds == null || assignmentIds.isEmpty()) {
                return Result.validateFailed("作业ID列表不能为空");
            }

            // 验证所有作业是否属于该教师
            for (Long assignmentId : assignmentIds) {
                if (!assignmentService.isTeacherAssignment(teacherId, assignmentId)) {
                    return Result.forbidden("包含非您创建的作业，无权删除");
                }
            }

            assignmentService.batchDeleteAssignments(assignmentIds);
            return Result.success("批量删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量发布作业
     *
     * @param request 请求数据，包含作业ID列表
     * @return 发布结果
     */
    @PostMapping("/batch-publish")
    public Result<String> batchPublishAssignments(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = getCurrentTeacherId();

            List<Long> assignmentIds = (List<Long>) request.get("assignmentIds");
            if (assignmentIds == null || assignmentIds.isEmpty()) {
                return Result.validateFailed("作业ID列表不能为空");
            }

            // 验证所有作业是否属于该教师
            for (Long assignmentId : assignmentIds) {
                if (!assignmentService.isTeacherAssignment(teacherId, assignmentId)) {
                    return Result.forbidden("包含非您创建的作业，无权发布");
                }
            }

            assignmentService.batchPublishAssignments(assignmentIds);
            return Result.success("批量发布成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批量发布失败: " + e.getMessage());
        }
    }
}

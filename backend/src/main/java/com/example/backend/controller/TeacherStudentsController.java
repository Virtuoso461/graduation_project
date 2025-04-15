package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.StudentService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师学生管理控制器
 * 提供教师管理学生的功能
 */
@RestController
@RequestMapping("/api/teacher/students")
public class TeacherStudentsController {

    private final UserService userService;
    private final StudentService studentService;
    private final CourseService courseService;

    @Autowired
    public TeacherStudentsController(UserService userService, StudentService studentService, CourseService courseService) {
        this.userService = userService;
        this.studentService = studentService;
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
     * 获取教师所有课程的学生列表
     *
     * @return 学生列表
     */
    @GetMapping()
    public Result<Map<String, Object>> getAllStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> students = studentService.getTeacherStudents(teacherId, keyword, page, size);
            return Result.success(students);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 按课程获取学生列表
     *
     * @param courseId 课程ID
     * @return 该课程的学生列表
     */
    @GetMapping("/course/{courseId}")
    public Result<Map<String, Object>> getStudentsByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            Map<String, Object> students = studentService.getCourseStudents(courseId, keyword, page, size);
            return Result.success(students);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程学生失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生详细信息
     *
     * @param studentId 学生ID
     * @return 学生详细信息
     */
    @GetMapping("/{studentId}")
    public Result<Map<String, Object>> getStudentDetail(@PathVariable Long studentId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证学生是否在教师的课程中
            boolean hasStudent = studentService.isTeacherStudent(teacherId, studentId);
            if (!hasStudent) {
                return Result.forbidden("该学生不在您的任何课程中，无权查看");
            }

            Map<String, Object> studentDetail = studentService.getStudentDetail(studentId, teacherId);
            return Result.success(studentDetail);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取学生详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生成绩概览
     *
     * @param studentId 学生ID
     * @return 学生各课程成绩
     */
    @GetMapping("/{studentId}/grades")
    public Result<List<Map<String, Object>>> getStudentGrades(@PathVariable Long studentId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证学生是否在教师的课程中
            boolean hasStudent = studentService.isTeacherStudent(teacherId, studentId);
            if (!hasStudent) {
                return Result.forbidden("该学生不在您的任何课程中，无权查看");
            }

            List<Map<String, Object>> grades = studentService.getStudentGrades(studentId, teacherId);
            return Result.success(grades);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取学生成绩失败: " + e.getMessage());
        }
    }

    /**
     * 添加学生备注
     *
     * @param studentId 学生ID
     * @param data 备注数据
     * @return 添加结果
     */
    @PostMapping("/{studentId}/notes")
    public Result<String> addStudentNote(@PathVariable Long studentId, @RequestBody Map<String, String> data) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证学生是否在教师的课程中
            boolean hasStudent = studentService.isTeacherStudent(teacherId, studentId);
            if (!hasStudent) {
                return Result.forbidden("该学生不在您的任何课程中，无权添加备注");
            }

            studentService.addStudentNote(studentId, teacherId, data.get("note"));
            return Result.success("添加备注成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("添加学生备注失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生学习进度
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学习进度数据
     */
    @GetMapping("/{studentId}/progress")
    public Result<Map<String, Object>> getStudentProgress(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证学生是否在教师的课程中
            if (!studentService.isTeacherStudent(teacherId, studentId)) {
                return Result.forbidden("该学生不在您的任何课程中，无权查看");
            }

            Map<String, Object> progress;
            if (courseId != null) {
                // 验证课程是否属于教师
                if (!courseService.isTeacherCourse(teacherId, courseId)) {
                    return Result.forbidden("您不是该课程的授课教师，无权查看");
                }

                // 获取学生在特定课程的进度
                progress = studentService.getStudentCourseProgress(studentId, courseId);
            } else {
                // 获取学生在所有课程的进度
                progress = studentService.getStudentOverallProgress(studentId, teacherId);
            }

            return Result.success(progress);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取学生进度失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生的作业提交情况
     *
     * @param studentId 学生ID
     * @param courseId 课程ID（可选）
     * @return 作业提交列表
     */
    @GetMapping("/{studentId}/assignments")
    public Result<List<Map<String, Object>>> getStudentAssignments(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证学生是否在教师的课程中
            if (!studentService.isTeacherStudent(teacherId, studentId)) {
                return Result.forbidden("该学生不在您的任何课程中，无权查看");
            }

            List<Map<String, Object>> assignments;
            if (courseId != null) {
                // 验证课程是否属于教师
                if (!courseService.isTeacherCourse(teacherId, courseId)) {
                    return Result.forbidden("您不是该课程的授课教师，无权查看");
                }

                // 获取学生在特定课程的作业提交
                assignments = studentService.getStudentCourseAssignments(studentId, courseId);
            } else {
                // 获取学生在所有课程的作业提交
                assignments = studentService.getStudentAllAssignments(studentId, teacherId);
            }

            return Result.success(assignments);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取学生作业失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生的考试成绩
     *
     * @param studentId 学生ID
     * @param courseId 课程ID（可选）
     * @return 考试成绩列表
     */
    @GetMapping("/{studentId}/exams")
    public Result<List<Map<String, Object>>> getStudentExams(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证学生是否在教师的课程中
            if (!studentService.isTeacherStudent(teacherId, studentId)) {
                return Result.forbidden("该学生不在您的任何课程中，无权查看");
            }

            List<Map<String, Object>> exams;
            if (courseId != null) {
                // 验证课程是否属于教师
                if (!courseService.isTeacherCourse(teacherId, courseId)) {
                    return Result.forbidden("您不是该课程的授课教师，无权查看");
                }

                // 获取学生在特定课程的考试成绩
                exams = studentService.getStudentCourseExams(studentId, courseId);
            } else {
                // 获取学生在所有课程的考试成绩
                exams = studentService.getStudentAllExams(studentId, teacherId);
            }

            return Result.success(exams);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取学生考试成绩失败: " + e.getMessage());
        }
    }
}

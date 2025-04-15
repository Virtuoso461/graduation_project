package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Course;
import com.example.backend.entity.CourseCategory;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员课程管理控制器
 * 提供课程管理功能
 */
@RestController
@RequestMapping("/api/admin/courses")
public class AdminCoursesController {

    private final UserService userService;
    private final CourseService courseService;

    public AdminCoursesController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    /**
     * 验证当前用户是否为管理员
     */
    private void validateAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findByUsername(userEmail);

        if (user == null || user.getRole() != Role.ADMIN) {
            throw new SecurityException("非管理员账号，无权访问");
        }
    }

    /**
     * 获取课程列表
     *
     * @param keyword 搜索关键词
     * @param status 状态筛选
     * @param page 页码
     * @param size 每页大小
     * @return 课程列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            validateAdminRole();

            Map<String, Object> coursesData = courseService.findCourses(keyword, status, page, size);
            return Result.success(coursesData);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    @GetMapping("/{courseId}")
    public Result<Map<String, Object>> getCourseDetail(@PathVariable Long courseId) {
        try {
            validateAdminRole();

            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                return Result.notFound("课程不存在");
            }

            Map<String, Object> courseDetail = courseService.getCourseDetailMap(course);
            return Result.success(courseDetail);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程详情失败: " + e.getMessage());
        }
    }

    /**
     * 审核课程
     *
     * @param courseId 课程ID
     * @param approvalData 审核数据
     * @return 审核结果
     */
    @PutMapping("/{courseId}/approval")
    public Result<Map<String, Object>> approveCourse(@PathVariable Long courseId, @RequestBody Map<String, Object> approvalData) {
        try {
            validateAdminRole();

            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                return Result.notFound("课程不存在");
            }

            boolean approved = (Boolean) approvalData.get("approved");
            String feedback = (String) approvalData.get("feedback");

            course.setApproved(approved);
            course.setApprovalFeedback(feedback);
            Course updatedCourse = courseService.updateCourse(course);

            Map<String, Object> result = new HashMap<>();
            result.put("id", updatedCourse.getId());
            result.put("name", updatedCourse.getName());
            result.put("approved", updatedCourse.isApproved());
            result.put("approvalFeedback", updatedCourse.getApprovalFeedback());

            return Result.success(result, approved ? "课程已审核通过" : "课程已拒绝");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("审核课程失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程
     *
     * @param courseId 课程ID
     * @return 删除结果
     */
    @DeleteMapping("/{courseId}")
    public Result<String> deleteCourse(@PathVariable Long courseId) {
        try {
            validateAdminRole();

            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                return Result.notFound("课程不存在");
            }

            courseService.deleteCourse(courseId);
            return Result.success("课程删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除课程失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程分类
     *
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<CourseCategory>> getCourseCategories() {
        try {
            validateAdminRole();

            List<CourseCategory> categories = courseService.getAllCategories();
            return Result.success(categories);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程分类失败: " + e.getMessage());
        }
    }

    /**
     * 创建课程分类
     *
     * @param categoryData 分类数据
     * @return 创建结果
     */
    @PostMapping("/categories")
    public Result<CourseCategory> createCourseCategory(@RequestBody Map<String, String> categoryData) {
        try {
            validateAdminRole();

            String name = categoryData.get("name");
            String description = categoryData.get("description");

            if (name == null || name.isEmpty()) {
                return Result.validateFailed("分类名称不能为空");
            }

            CourseCategory category = new CourseCategory();
            category.setName(name);
            category.setDescription(description);

            CourseCategory createdCategory = courseService.createCategory(category);
            return Result.success(createdCategory, "课程分类创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建课程分类失败: " + e.getMessage());
        }
    }

    /**
     * 更新课程分类
     *
     * @param categoryId 分类ID
     * @param categoryData 分类数据
     * @return 更新结果
     */
    @PutMapping("/categories/{categoryId}")
    public Result<CourseCategory> updateCourseCategory(
            @PathVariable Long categoryId,
            @RequestBody Map<String, String> categoryData) {
        try {
            validateAdminRole();

            CourseCategory category = courseService.getCategoryById(categoryId);
            if (category == null) {
                return Result.notFound("课程分类不存在");
            }

            String name = categoryData.get("name");
            String description = categoryData.get("description");

            if (name != null && !name.isEmpty()) {
                category.setName(name);
            }
            if (description != null) {
                category.setDescription(description);
            }

            CourseCategory updatedCategory = courseService.updateCategory(category);
            return Result.success(updatedCategory, "课程分类更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新课程分类失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程分类
     *
     * @param categoryId 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/categories/{categoryId}")
    public Result<String> deleteCourseCategory(@PathVariable Long categoryId) {
        try {
            validateAdminRole();

            CourseCategory category = courseService.getCategoryById(categoryId);
            if (category == null) {
                return Result.notFound("课程分类不存在");
            }

            // 检查分类是否被使用
            if (courseService.isCategoryInUse(categoryId)) {
                return Result.validateFailed("该分类下存在课程，无法删除");
            }

            courseService.deleteCategory(categoryId);
            return Result.success("课程分类删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除课程分类失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCourseStatistics() {
        try {
            validateAdminRole();

            Map<String, Object> stats = new HashMap<>();

            // 总课程数
            long totalCourses = courseService.countAllCourses();
            stats.put("totalCourses", totalCourses);

            // 待审核课程数
            long pendingCourses = courseService.countPendingApprovalCourses();
            stats.put("pendingCourses", pendingCourses);

            // 已审核课程数
            long approvedCourses = courseService.countApprovedCourses();
            stats.put("approvedCourses", approvedCourses);

            // 拒绝的课程数
            long rejectedCourses = courseService.countRejectedCourses();
            stats.put("rejectedCourses", rejectedCourses);

            // 按分类统计
            Map<String, Long> categoryStats = courseService.getCourseCountByCategory();
            stats.put("categoryStats", categoryStats);

            // 最近30天课程创建趋势
            Map<String, Long> creationTrend = courseService.getCourseCreationTrend(30);
            stats.put("creationTrend", creationTrend);

            // 课程评分统计
            Map<String, Double> ratingStats = courseService.getCourseRatingStats();
            stats.put("ratingStats", ratingStats);

            return Result.success(stats);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程统计数据失败: " + e.getMessage());
        }
    }
}

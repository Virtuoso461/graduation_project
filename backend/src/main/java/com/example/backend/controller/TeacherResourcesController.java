package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.ResourceService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 教师资源管理控制器
 * 提供教学资源管理功能
 */
@RestController
@RequestMapping("/api/teacher/resources")
public class TeacherResourcesController {

    private final UserService userService;
    private final ResourceService resourceService;
    private final CourseService courseService;

    @Autowired
    public TeacherResourcesController(UserService userService, ResourceService resourceService, CourseService courseService) {
        this.userService = userService;
        this.resourceService = resourceService;
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
     * 获取教师的所有教学资源
     *
     * @return 资源列表
     */
    @GetMapping()
    public Result<Map<String, Object>> getTeacherResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();
            Map<String, Object> resources = resourceService.getTeacherResources(teacherId, keyword, type, page, size);
            return Result.success(resources);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取教学资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程的教学资源
     *
     * @param courseId 课程ID
     * @return 资源列表
     */
    @GetMapping("/course/{courseId}")
    public Result<Map<String, Object>> getCourseResources(
            @PathVariable Long courseId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            Map<String, Object> resources = resourceService.getCourseResources(courseId, keyword, type, page, size);
            return Result.success(resources);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程资源失败: " + e.getMessage());
        }
    }

    /**
     * 上传教学资源
     *
     * @param file 资源文件
     * @param data 资源元数据
     * @return 上传结果
     */
    @PostMapping()
    public Result<Map<String, Object>> uploadResource(
            @RequestParam MultipartFile file,
            @RequestParam Map<String, String> data) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 如果指定了课程，验证课程是否属于该教师
            if (data.containsKey("courseId")) {
                Long courseId = Long.parseLong(data.get("courseId"));
                boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
                if (!courseOwned) {
                    return Result.forbidden("您不是该课程的授课教师，无权上传");
                }
            }

            Map<String, Object> resource = resourceService.uploadResource(teacherId, file, data);
            return Result.success(resource, "资源上传成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("上传资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源详情
     *
     * @param resourceId 资源ID
     * @return 资源详情
     */
    @GetMapping("/{resourceId}")
    public Result<Map<String, Object>> getResourceDetail(@PathVariable Long resourceId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证资源是否属于该教师
            boolean resourceOwned = resourceService.isTeacherResource(teacherId, resourceId);
            if (!resourceOwned) {
                return Result.forbidden("您不是该资源的上传者，无权查看");
            }

            Map<String, Object> resource = resourceService.getResourceDetail(resourceId);
            return Result.success(resource);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取资源详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新资源信息
     *
     * @param resourceId 资源ID
     * @param data 资源数据
     * @return 更新结果
     */
    @PutMapping("/{resourceId}")
    public Result<Map<String, Object>> updateResource(@PathVariable Long resourceId, @RequestBody Map<String, Object> data) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证资源是否属于该教师
            boolean resourceOwned = resourceService.isTeacherResource(teacherId, resourceId);
            if (!resourceOwned) {
                return Result.forbidden("您不是该资源的上传者，无权修改");
            }

            Map<String, Object> updatedResource = resourceService.updateResource(resourceId, data);
            return Result.success(updatedResource, "资源信息更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新资源信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除资源
     *
     * @param resourceId 资源ID
     * @return 删除结果
     */
    @DeleteMapping("/{resourceId}")
    public Result<String> deleteResource(@PathVariable Long resourceId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证资源是否属于该教师
            boolean resourceOwned = resourceService.isTeacherResource(teacherId, resourceId);
            if (!resourceOwned) {
                return Result.forbidden("您不是该资源的上传者，无权删除");
            }

            resourceService.deleteResource(resourceId);
            return Result.success("资源删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源类型列表
     *
     * @return 资源类型列表
     */
    @GetMapping("/types")
    public Result<List<String>> getResourceTypes() {
        try {
            getCurrentTeacherId(); // 验证教师身份

            List<String> types = resourceService.getResourceTypes();
            return Result.success(types);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取资源类型失败: " + e.getMessage());
        }
    }

    /**
     * 将资源添加到课程
     *
     * @param resourceId 资源ID
     * @param courseId 课程ID
     * @return 添加结果
     */
    @PostMapping("/{resourceId}/add-to-course/{courseId}")
    public Result<String> addResourceToCourse(@PathVariable Long resourceId, @PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证资源是否属于该教师
            boolean resourceOwned = resourceService.isTeacherResource(teacherId, resourceId);
            if (!resourceOwned) {
                return Result.forbidden("您不是该资源的上传者，无权操作");
            }

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权操作");
            }

            resourceService.addResourceToCourse(resourceId, courseId);
            return Result.success("资源已添加到课程");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("添加资源到课程失败: " + e.getMessage());
        }
    }

    /**
     * 从课程中移除资源
     *
     * @param resourceId 资源ID
     * @param courseId 课程ID
     * @return 移除结果
     */
    @DeleteMapping("/{resourceId}/remove-from-course/{courseId}")
    public Result<String> removeResourceFromCourse(@PathVariable Long resourceId, @PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权操作");
            }

            resourceService.removeResourceFromCourse(resourceId, courseId);
            return Result.success("资源已从课程中移除");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("从课程中移除资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getResourceStatistics() {
        try {
            Long teacherId = getCurrentTeacherId();
            Map<String, Object> statistics = resourceService.getTeacherResourceStatistics(teacherId);
            return Result.success(statistics);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取资源统计失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除资源
     *
     * @param request 请求数据，包含资源ID列表
     * @return 删除结果
     */
    @PostMapping("/batch-delete")
    public Result<String> batchDeleteResources(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = getCurrentTeacherId();

            List<Long> resourceIds = (List<Long>) request.get("resourceIds");
            if (resourceIds == null || resourceIds.isEmpty()) {
                return Result.validateFailed("资源ID列表不能为空");
            }

            // 验证所有资源是否属于该教师
            for (Long resourceId : resourceIds) {
                if (!resourceService.isTeacherResource(teacherId, resourceId)) {
                    return Result.forbidden("包含非您上传的资源，无权删除");
                }
            }

            resourceService.batchDeleteResources(resourceIds);
            return Result.success("批量删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量移动资源到课程
     *
     * @param request 请求数据，包含资源ID列表和课程ID
     * @return 移动结果
     */
    @PostMapping("/batch-move")
    public Result<String> batchMoveResources(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = getCurrentTeacherId();

            List<Long> resourceIds = (List<Long>) request.get("resourceIds");
            Long courseId = ((Number) request.get("courseId")).longValue();

            if (resourceIds == null || resourceIds.isEmpty() || courseId == null) {
                return Result.validateFailed("资源ID列表和课程ID不能为空");
            }

            // 验证课程是否属于该教师
            if (!courseService.isTeacherCourse(teacherId, courseId)) {
                return Result.forbidden("您不是该课程的授课教师，无权操作");
            }

            // 验证所有资源是否属于该教师
            for (Long resourceId : resourceIds) {
                if (!resourceService.isTeacherResource(teacherId, resourceId)) {
                    return Result.forbidden("包含非您上传的资源，无权移动");
                }
            }

            resourceService.batchAddResourcesToCourse(resourceIds, courseId);
            return Result.success("批量移动成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("批量移动失败: " + e.getMessage());
        }
    }

    /**
     * 按类型获取资源列表
     *
     * @param type 资源类型
     * @param page 页码
     * @param size 每页大小
     * @return 资源列表
     */
    @GetMapping("/by-type/{type}")
    public Result<Map<String, Object>> getResourcesByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> resources = resourceService.getTeacherResourcesByType(teacherId, type, page, size);
            return Result.success(resources);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取资源列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源分类列表
     *
     * @return 资源分类列表
     */
    @GetMapping("/categories")
    public Result<List<String>> getResourceCategories() {
        try {
            getCurrentTeacherId(); // 验证教师身份
            List<String> categories = resourceService.getResourceCategories();
            return Result.success(categories);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取资源分类失败: " + e.getMessage());
        }
    }
}

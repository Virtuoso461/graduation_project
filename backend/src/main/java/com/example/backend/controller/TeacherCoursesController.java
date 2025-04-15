package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Course;
import com.example.backend.entity.CourseChapter;
import com.example.backend.entity.CourseSection;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.CourseSectionRepository;
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
 * 教师课程管理控制器
 * 提供教师课程管理功能
 */
@RestController
@RequestMapping("/api/teacher/courses")
public class TeacherCoursesController {

    private final UserService userService;
    private final CourseService courseService;
    private final CourseSectionRepository courseSectionRepository;

    @Autowired
    public TeacherCoursesController(UserService userService, CourseService courseService, CourseSectionRepository courseSectionRepository) {
        this.userService = userService;
        this.courseService = courseService;
        this.courseSectionRepository = courseSectionRepository;
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
     * 获取教师的所有课程
     *
     * @return 课程列表
     */
    @GetMapping()
    public Result<Map<String, Object>> getTeacherCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long teacherId = getCurrentTeacherId();

            Map<String, Object> courses = courseService.getTeacherCourses(teacherId, keyword, page, size);
            return Result.success(courses);
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
     * @return 课程详细信息
     */
    @GetMapping("/{courseId}")
    public Result<Course> getCourseDetail(@PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            Course course = courseService.getCourseById(courseId);
            return Result.success(course);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新课程
     *
     * @param courseData 课程数据
     * @return 创建结果
     */
    @PostMapping()
    public Result<Course> createCourse(@RequestBody Course courseData) {
        try {
            Long teacherId = getCurrentTeacherId();

            Course createdCourse = courseService.createCourse(teacherId, courseData);
            return Result.success(createdCourse, "课程创建成功，等待管理员审核");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建课程失败: " + e.getMessage());
        }
    }

    /**
     * 更新课程信息
     *
     * @param courseId 课程ID
     * @param courseData 课程数据
     * @return 更新结果
     */
    @PutMapping("/{courseId}")
    public Result<Course> updateCourse(@PathVariable Long courseId, @RequestBody Course courseData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权更新");
            }

            Course updatedCourse = courseService.updateCourse(courseId, courseData);
            return Result.success(updatedCourse, "课程更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新课程失败: " + e.getMessage());
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
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权删除");
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
     * 上传课程封面图片
     *
     * @param courseId 课程ID
     * @param file 图片文件
     * @return 上传结果
     */
    @PostMapping("/{courseId}/cover")
    public Result<String> uploadCourseCover(@PathVariable Long courseId, @RequestParam MultipartFile file) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权上传");
            }

            String coverUrl = courseService.uploadCourseCover(courseId, file);
            return Result.success(coverUrl, "封面上传成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("上传封面失败: " + e.getMessage());
        }
    }

    /**
     * 上传课程资料
     *
     * @param courseId 课程ID
     * @param file 资料文件
     * @return 上传结果
     */
    @PostMapping("/{courseId}/materials")
    public Result<Map<String, Object>> uploadCourseMaterial(@PathVariable Long courseId, @RequestParam MultipartFile file) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权上传");
            }

            Map<String, Object> material = courseService.uploadCourseMaterial(courseId, file);
            return Result.success(material, "资料上传成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("上传资料失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程资料列表
     *
     * @param courseId 课程ID
     * @return 资料列表
     */
    @GetMapping("/{courseId}/materials")
    public Result<List<Map<String, Object>>> getCourseMaterials(@PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            List<Map<String, Object>> materials = courseService.getCourseMaterials(courseId);
            return Result.success(materials);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程资料失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程资料
     *
     * @param courseId 课程ID
     * @param materialId 资料ID
     * @return 删除结果
     */
    @DeleteMapping("/{courseId}/materials/{materialId}")
    public Result<String> deleteCourseMaterial(@PathVariable Long courseId, @PathVariable Long materialId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权删除");
            }

            courseService.deleteCourseMaterial(courseId, materialId);
            return Result.success("资料删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除资料失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程学生列表
     *
     * @param courseId 课程ID
     * @return 学生列表
     */
    @GetMapping("/{courseId}/students")
    public Result<List<Map<String, Object>>> getCourseStudents(@PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            List<Map<String, Object>> students = courseService.getCourseStudents(courseId);
            return Result.success(students);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程统计数据
     *
     * @param courseId 课程ID
     * @return 统计数据
     */
    @GetMapping("/{courseId}/statistics")
    public Result<Map<String, Object>> getCourseStatistics(@PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            Map<String, Object> statistics = courseService.getCourseStatistics(courseId);
            return Result.success(statistics);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程章节列表
     *
     * @param courseId 课程ID
     * @return 章节列表
     */
    @GetMapping("/{courseId}/chapters")
    public Result<List<Map<String, Object>>> getCourseChapters(@PathVariable Long courseId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            List<Map<String, Object>> chapters = courseService.getCourseChapters(courseId);
            return Result.success(chapters);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取课程章节列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建课程章节
     *
     * @param courseId 课程ID
     * @param chapterData 章节数据
     * @return 创建结果
     */
    @PostMapping("/{courseId}/chapters")
    public Result<Map<String, Object>> createCourseChapter(
            @PathVariable Long courseId,
            @RequestBody Map<String, Object> chapterData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权创建");
            }

            Map<String, Object> chapter = courseService.createCourseChapter(courseId, chapterData);
            return Result.success(chapter, "章节创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建课程章节失败: " + e.getMessage());
        }
    }

    /**
     * 更新课程章节
     *
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @param chapterData 章节数据
     * @return 更新结果
     */
    @PutMapping("/{courseId}/chapters/{chapterId}")
    public Result<Map<String, Object>> updateCourseChapter(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @RequestBody Map<String, Object> chapterData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权更新");
            }

            Map<String, Object> chapter = courseService.updateCourseChapter(courseId, chapterId, chapterData);
            return Result.success(chapter, "章节更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新课程章节失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程章节
     *
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 删除结果
     */
    @DeleteMapping("/{courseId}/chapters/{chapterId}")
    public Result<String> deleteCourseChapter(
            @PathVariable Long courseId,
            @PathVariable Long chapterId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, courseId);
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权删除");
            }

            courseService.deleteCourseChapter(courseId, chapterId);
            return Result.success("章节删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除课程章节失败: " + e.getMessage());
        }
    }

    /**
     * 获取章节小节列表
     *
     * @param chapterId 章节ID
     * @return 小节列表
     */
    @GetMapping("/chapters/{chapterId}/sections")
    public Result<List<Map<String, Object>>> getChapterSections(@PathVariable Long chapterId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 获取章节信息
            CourseChapter chapter = courseService.getChapterById(chapterId);
            if (chapter == null) {
                return Result.failed("章节不存在");
            }

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, chapter.getCourseId());
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权查看");
            }

            List<Map<String, Object>> sections = courseService.getChapterSections(chapterId);
            return Result.success(sections);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取章节小节列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加章节小节
     *
     * @param chapterId 章节ID
     * @param sectionData 小节数据
     * @return 创建结果
     */
    @PostMapping("/chapters/{chapterId}/sections")
    public Result<Map<String, Object>> addChapterSection(
            @PathVariable Long chapterId,
            @RequestBody Map<String, Object> sectionData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 获取章节信息
            CourseChapter chapter = courseService.getChapterById(chapterId);
            if (chapter == null) {
                return Result.failed("章节不存在");
            }

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, chapter.getCourseId());
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权创建");
            }

            Map<String, Object> section = courseService.createChapterSection(chapterId, sectionData);
            return Result.success(section, "小节创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建章节小节失败: " + e.getMessage());
        }
    }

    /**
     * 更新小节信息
     *
     * @param sectionId 小节ID
     * @param sectionData 小节数据
     * @return 更新结果
     */
    @PutMapping("/sections/{sectionId}")
    public Result<Map<String, Object>> updateSection(
            @PathVariable Long sectionId,
            @RequestBody Map<String, Object> sectionData) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 获取小节信息
            CourseSection section = courseSectionRepository.findById(sectionId).orElse(null);
            if (section == null) {
                return Result.failed("小节不存在");
            }

            // 获取章节信息
            CourseChapter chapter = courseService.getChapterById(section.getChapterId());
            if (chapter == null) {
                return Result.failed("章节不存在");
            }

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, chapter.getCourseId());
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权更新");
            }

            Map<String, Object> updatedSection = courseService.updateChapterSection(sectionId, sectionData);
            return Result.success(updatedSection, "小节更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新小节失败: " + e.getMessage());
        }
    }

    /**
     * 删除小节
     *
     * @param sectionId 小节ID
     * @return 删除结果
     */
    @DeleteMapping("/sections/{sectionId}")
    public Result<String> deleteSection(@PathVariable Long sectionId) {
        try {
            Long teacherId = getCurrentTeacherId();

            // 获取小节信息
            CourseSection section = courseSectionRepository.findById(sectionId).orElse(null);
            if (section == null) {
                return Result.failed("小节不存在");
            }

            // 获取章节信息
            CourseChapter chapter = courseService.getChapterById(section.getChapterId());
            if (chapter == null) {
                return Result.failed("章节不存在");
            }

            // 验证课程是否属于该教师
            boolean courseOwned = courseService.isTeacherCourse(teacherId, chapter.getCourseId());
            if (!courseOwned) {
                return Result.forbidden("您不是该课程的授课教师，无权删除");
            }

            courseService.deleteChapterSection(sectionId);
            return Result.success("小节删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除小节失败: " + e.getMessage());
        }
    }
}

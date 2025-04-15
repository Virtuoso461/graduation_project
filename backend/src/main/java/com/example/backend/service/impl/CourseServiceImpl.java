package com.example.backend.service.impl;

import com.example.backend.entity.Course;
import com.example.backend.entity.CourseCategory;
import com.example.backend.entity.CourseChapter;
import com.example.backend.entity.CourseEnrollment;
import com.example.backend.entity.CourseSection;
import com.example.backend.repository.CourseCategoryRepository;
import com.example.backend.repository.CourseChapterRepository;
import com.example.backend.repository.CourseEnrollmentRepository;
import com.example.backend.repository.CourseSectionRepository;
import com.example.backend.repository.CourseRepository;
import com.example.backend.service.CourseService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseChapterRepository courseChapterRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CourseCategoryRepository courseCategoryRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository,
                            CourseChapterRepository courseChapterRepository,
                            CourseEnrollmentRepository courseEnrollmentRepository,
                            CourseSectionRepository courseSectionRepository,
                            CourseCategoryRepository courseCategoryRepository) {
        this.courseRepository = courseRepository;
        this.courseChapterRepository = courseChapterRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.courseCategoryRepository = courseCategoryRepository;
    }

    @Override
    public List<Course> getCourses(Long userId) {
        if (userId != null) {
            // 获取用户已选课程
            // 这里需要实现根据用户ID获取已选课程的逻辑
            return new ArrayList<>();
        } else {
            // 获取所有课程
            return courseRepository.findAll();
        }
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<CourseCategory> getAllCategories() {
        return courseCategoryRepository.findAll();
    }

    @Override
    public CourseCategory getCategoryById(Long id) {
        return courseCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public CourseCategory createCategory(CourseCategory category) {
        // 检查同名分类是否存在
        if (courseCategoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        return courseCategoryRepository.save(category);
    }

    @Override
    public CourseCategory updateCategory(CourseCategory category) {
        // 检查同名分类是否存在（需要排除当前分类）
        CourseCategory existingCategory = courseCategoryRepository.findByName(category.getName());
        if (existingCategory != null && !Objects.equals(existingCategory.getId(), category.getId())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        return courseCategoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        courseCategoryRepository.deleteById(id);
    }

    @Override
    public boolean isCategoryInUse(Long categoryId) {
        // 获取分类名称
        CourseCategory category = getCategoryById(categoryId);
        if (category == null) {
            return false;
        }
        
        // 检查该分类是否被课程使用
        List<Course> courses = courseRepository.findByCategory(category.getName());
        return !courses.isEmpty();
    }

    @Override
    public Course createCourse(Long teacherId, Course courseData) {
        courseData.setTeacherId(teacherId);
        courseData.setApproved(false); // 新创建的课程默认未审核
        return courseRepository.save(courseData);
    }

    @Override
    public boolean isTeacherCourse(Long teacherId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        return course != null && course.getTeacherId().equals(teacherId);
    }

    @Override
    public void approveCourse(Long courseId, String comments) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("课程不存在"));
        course.setApproved(true);
        course.setApprovalFeedback(comments);
        courseRepository.save(course);
    }

    @Override
    public void rejectCourse(Long courseId, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("课程不存在"));
        course.setApproved(false);
        course.setApprovalFeedback(reason);
        courseRepository.save(course);
    }

    @Override
    public long countPendingApprovalCourses() {
        return courseRepository.findByApprovedFalse().size();
    }

    @Override
    public long countAllCourses() {
        return courseRepository.count();
    }

    @Override
    public long calculateCourseGrowthRate() {
        // 实现计算课程增长率的逻辑
        return 0;
    }

    @Override
    public List<Map<String, Object>> findRecentCourses(int limit) {
        // 实现获取最近添加的课程的逻辑
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getCourseDetailMap(Course course) {
        Map<String, Object> courseDetail = new HashMap<>();
        courseDetail.put("id", course.getId());
        courseDetail.put("name", course.getName());
        courseDetail.put("description", course.getDescription());
        courseDetail.put("teacherId", course.getTeacherId());
        courseDetail.put("approved", course.isApproved());
        courseDetail.put("createdAt", course.getCreatedAt());
        courseDetail.put("updatedAt", course.getUpdatedAt());
        courseDetail.put("category", course.getCategory());
        courseDetail.put("coverImage", course.getCoverImage());
        courseDetail.put("duration", course.getDuration());
        courseDetail.put("difficulty", course.getDifficulty());
        courseDetail.put("credits", course.getCredits());
        courseDetail.put("approvalFeedback", course.getApprovalFeedback());
        
        return courseDetail;
    }

    @Override
    public void deleteCourseMaterial(Long courseId, Long materialId) {

    }

    @Override
    public List<Map<String, Object>> getCourseStudents(Long courseId) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getRecentCourseActivities(Long teacherId, int limit) {
        return List.of();
    }

    @Override
    public long countTeacherCourses(Long teacherId) {
        return 0;
    }

    @Override
    public long countTeacherStudents(Long teacherId) {
        return 0;
    }

    @Override
    public int calculateCourseCompletionRate(Long teacherId) {
        return 0;
    }

    @Override
    public Map<String, Object> findCourses(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Course> coursesPage;
        
        // 根据搜索条件查询
        // 这里简化处理，实际应该根据keyword和status组合查询
        if (keyword != null && !keyword.isEmpty()) {
            coursesPage = courseRepository.findAll(pageable); // 应该替换为实际的搜索逻辑
        } else {
            coursesPage = courseRepository.findAll(pageable);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", coursesPage.getTotalElements());
        result.put("totalPages", coursesPage.getTotalPages());
        result.put("currentPage", page);
        result.put("courses", coursesPage.getContent());
        
        return result;
    }

    @Override
    public Map<String, Object> findPendingCourses(int page, int size) {
        // 实现获取待审核课程的逻辑
        return new HashMap<>();
    }

    @Override
    public long countApprovedCourses() {
        return courseRepository.findByApprovedTrue().size();
    }

    @Override
    public long countRejectedCourses() {
        // 假设拒绝的课程是approved=false且有反馈的
        // 实际逻辑可能需要根据数据库设计调整
        return 0; // 简化实现
    }

    @Override
    public Map<String, Long> getCourseCountByCategory() {
        List<Course> allCourses = courseRepository.findAll();
        Map<String, Long> categoryCounts = new HashMap<>();
        
        // 对课程按分类进行分组计数
        for (Course course : allCourses) {
            String category = course.getCategory();
            if (category != null) {
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0L) + 1);
            }
        }
        
        return categoryCounts;
    }

    @Override
    public List<String> getCourseCategories() {
        // 转换为纯字符串列表
        return courseCategoryRepository.findAll()
                .stream()
                .map(CourseCategory::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getCourseStatistics(Long courseId) {
        Map<String, Object> statistics = new HashMap<>();

        // 获取课程信息
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return statistics;
        }

        // 获取章节数量
        long chapterCount = courseChapterRepository.countByCourseId(courseId);
        statistics.put("chapterCount", chapterCount);

        // 获取小节数量
        long sectionCount = 0;
        List<CourseChapter> chapters = courseChapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
        for (CourseChapter chapter : chapters) {
            sectionCount += courseSectionRepository.countByChapterId(chapter.getId());
        }
        statistics.put("sectionCount", sectionCount);

        // 获取选课人数
        long enrollmentCount = courseEnrollmentRepository.countByCourseId(courseId);
        statistics.put("enrollmentCount", enrollmentCount);

        // 获取完成人数
        long completedCount = 0; // 需要实现
        statistics.put("completedCount", completedCount);

        // 获取平均进度
        double averageProgress = 0.0; // 需要实现
        statistics.put("averageProgress", averageProgress);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getCourseChapters(Long courseId) {
        List<CourseChapter> chapters = courseChapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
        
        return chapters.stream().map(chapter -> {
            Map<String, Object> chapterMap = new HashMap<>();
            chapterMap.put("id", chapter.getId());
            chapterMap.put("title", chapter.getTitle());
            chapterMap.put("description", chapter.getDescription());
            chapterMap.put("duration", chapter.getDuration());
            chapterMap.put("videoUrl", chapter.getVideoUrl());
            chapterMap.put("sortOrder", chapter.getSortOrder());
            return chapterMap;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> createCourseChapter(Long courseId, Map<String, Object> chapterData) {
        // 实现创建课程章节的逻辑
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> updateCourseChapter(Long courseId, Long chapterId, Map<String, Object> chapterData) {
        // 实现更新课程章节的逻辑
        return new HashMap<>();
    }

    @Override
    public void deleteCourseChapter(Long courseId, Long chapterId) {
        // 实现删除课程章节的逻辑
    }

    @Override
    public List<Course> getApprovedCourses() {
        return courseRepository.findByApprovedTrue();
    }

    @Override
    public List<CourseEnrollment> getEnrolledCourses(String userEmail) {
        return courseEnrollmentRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<CourseEnrollment> getCompletedCourses(String userEmail) {
        return courseEnrollmentRepository.findByUserEmailAndIsCompletedTrue(userEmail);
    }

    @Override
    public List<CourseEnrollment> getInProgressCourses(String userEmail) {
        return courseEnrollmentRepository.findByUserEmailAndIsCompletedFalse(userEmail);
    }

    @Override
    public boolean isEnrolled(String userEmail, Long courseId) {
        return courseEnrollmentRepository.findByUserEmailAndCourseId(userEmail, courseId).isPresent();
    }

    @Override
    @Transactional
    public CourseEnrollment enrollCourse(String userEmail, Long courseId) {
        // 检查是否已选修
        if (isEnrolled(userEmail, courseId)) {
            throw new IllegalStateException("您已选修该课程");
        }
        
        // 检查课程是否存在且已审核通过
        Course course = getCourseById(courseId);
        if (course == null) {
            throw new NoSuchElementException("课程不存在");
        }
        if (!course.isApproved()) {
            throw new IllegalStateException("课程尚未审核通过");
        }
        
        // 创建选修记录
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setUserEmail(userEmail);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollTime(new Date());
        enrollment.setLastAccessTime(new Date());
        enrollment.setCreatedAt(new Date()); // 设置创建时间
        enrollment.setProgress(0);
        enrollment.setIsCompleted(false);
        
        return courseEnrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public CourseEnrollment updateCourseProgress(String userEmail, Long courseId, Integer progress, 
                                                Long chapterId, Boolean completed) {
        // 获取选修记录
        CourseEnrollment enrollment = courseEnrollmentRepository.findByUserEmailAndCourseId(userEmail, courseId)
                .orElseThrow(() -> new NoSuchElementException("未找到选修记录"));
        
        // 更新进度
        if (progress != null) {
            enrollment.setProgress(progress);
        }
        
        // 更新最后访问的章节ID
        if (chapterId != null) {
            enrollment.setLastAccessedChapterId(chapterId);
        }
        
        // 更新完成状态
        if (completed != null) {
            enrollment.setIsCompleted(completed);
        }
        
        // 更新最后访问时间
        enrollment.setLastAccessTime(new Date());
        
        return courseEnrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public CourseEnrollment updateLastAccessedChapter(String userEmail, Long courseId, Long chapterId) {
        // 获取选修记录
        CourseEnrollment enrollment = courseEnrollmentRepository.findByUserEmailAndCourseId(userEmail, courseId)
                .orElseThrow(() -> new NoSuchElementException("未找到选修记录"));

        // 更新最后访问的章节
        enrollment.setLastAccessedChapterId(chapterId);

        // 更新最后访问时间
        enrollment.setLastAccessTime(new Date());

        return courseEnrollmentRepository.save(enrollment);
    }

    @Override
    public CourseChapter getChapterById(Long chapterId) {
        return courseChapterRepository.findById(chapterId).orElse(null);
    }

    @Override
    @Transactional
    public Map<String, Object> recordStudyTime(String userEmail, Long courseId, Integer minutes, Long chapterId) {
        // 获取选修记录
        CourseEnrollment enrollment = courseEnrollmentRepository.findByUserEmailAndCourseId(userEmail, courseId)
                .orElseThrow(() -> new NoSuchElementException("未找到选修记录"));
        
        // 更新学习时间
        Integer totalStudyTime = enrollment.getTotalStudyTime();
        if (totalStudyTime == null) {
            totalStudyTime = 0;
        }
        enrollment.setTotalStudyTime(totalStudyTime + minutes);
        
        // 更新最后访问的章节ID和访问时间
        if (chapterId != null) {
            enrollment.setLastAccessedChapterId(chapterId);
        }
        enrollment.setLastAccessTime(new Date());
        
        // 保存选修记录
        courseEnrollmentRepository.save(enrollment);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("courseId", courseId);
        result.put("chapterId", chapterId);
        result.put("minutes", minutes);
        result.put("totalStudyTime", enrollment.getTotalStudyTime());
        result.put("lastAccessTime", enrollment.getLastAccessTime());
        result.put("lastAccessedChapterId", enrollment.getLastAccessedChapterId());

        return result;
    }

    @Override
    public Map<String, Object> getStudentCourseStatistics(String userEmail) {
        Map<String, Object> statistics = new HashMap<>();

        // 获取选修课程总数
        long totalCourses = courseEnrollmentRepository.countByUserEmail(userEmail);
        statistics.put("totalCourses", totalCourses);

        // 获取已完成课程数
        long completedCourses = courseEnrollmentRepository.countByUserEmailAndIsCompleted(userEmail, true);
        statistics.put("completedCourses", completedCourses);

        // 获取进行中课程数
        long inProgressCourses = courseEnrollmentRepository.countByUserEmailAndIsCompleted(userEmail, false);
        statistics.put("inProgressCourses", inProgressCourses);

        // 获取平均进度
        Double averageProgress = courseEnrollmentRepository.getAverageProgressByUserEmail(userEmail);
        statistics.put("averageProgress", averageProgress != null ? averageProgress : 0);

        // 获取总学分
        int totalCredits = 0;
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserEmailAndIsCompleted(userEmail, true);
        for (CourseEnrollment enrollment : enrollments) {
            Course course = courseRepository.findById(enrollment.getCourseId()).orElse(null);
            if (course != null && course.getCredits() != null) {
                totalCredits += course.getCredits();
            }
        }
        statistics.put("totalCredits", totalCredits);

        return statistics;
    }

    public List<Map<String, Object>> getStudentTranscript(String userEmail) {
        List<Map<String, Object>> transcript = new ArrayList<>();

        // 获取所有选修记录
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserEmail(userEmail);

        for (CourseEnrollment enrollment : enrollments) {
            Course course = courseRepository.findById(enrollment.getCourseId()).orElse(null);
            if (course != null) {
                Map<String, Object> courseTranscript = new HashMap<>();
                courseTranscript.put("courseId", course.getId());
                courseTranscript.put("courseName", course.getName());
                courseTranscript.put("credits", course.getCredits());
                courseTranscript.put("grade", enrollment.getGrade());
                courseTranscript.put("isCompleted", enrollment.getIsCompleted());
                courseTranscript.put("progress", enrollment.getProgress());
                courseTranscript.put("enrollTime", enrollment.getEnrollTime());
                courseTranscript.put("completionDate", enrollment.getCompletionDate());

                transcript.add(courseTranscript);
            }
        }

        return transcript;
    }

    @Override
    public List<Map<String, Object>> getRecommendedCourses(String userEmail, Integer limit) {
        // 获取用户已选修的课程
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserEmail(userEmail);
        Set<Long> enrolledCourseIds = enrollments.stream()
                .map(CourseEnrollment::getCourseId)
                .collect(Collectors.toSet());
        
        // 获取用户已选修课程的分类
        Set<String> userCategories = new HashSet<>();
        for (Long courseId : enrolledCourseIds) {
            Course course = getCourseById(courseId);
            if (course != null && course.getCategory() != null) {
                userCategories.add(course.getCategory());
            }
        }
        
        // 获取同类别的其他已审核课程
        List<Course> recommendations = new ArrayList<>();
        for (String category : userCategories) {
            List<Course> categoryCourses = courseRepository.findByCategoryAndApprovedTrue(category);
            categoryCourses = categoryCourses.stream()
                    .filter(c -> !enrolledCourseIds.contains(c.getId()))
                    .collect(Collectors.toList());
            recommendations.addAll(categoryCourses);
        }
        
        // 如果推荐不足，添加其他热门课程
        if (recommendations.size() < limit) {
            List<Course> popularCourses = courseRepository.findByApprovedTrue();
            
            // 创建一个包含推荐课程ID的集合，避免在lambda中直接引用recommendations
            final Set<Long> recommendedCourseIds = recommendations.stream()
                    .map(Course::getId)
                    .collect(Collectors.toSet());
            
            popularCourses = popularCourses.stream()
                    .filter(c -> !enrolledCourseIds.contains(c.getId()) && 
                           !recommendedCourseIds.contains(c.getId()))
                    .limit(limit - recommendations.size())
                    .collect(Collectors.toList());
            recommendations.addAll(popularCourses);
        }
        
        // 限制返回数量
        if (recommendations.size() > limit) {
            recommendations = recommendations.subList(0, limit);
        }
        
        // 将Course对象转换为Map
        return recommendations.stream()
                .map(this::getCourseDetailMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getTeacherCourses(Long teacherId, String keyword, int page, int size) {
        Map<String, Object> result = new HashMap<>();

        // 创建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 查询课程
        List<Course> courses;
        if (keyword != null && !keyword.isEmpty()) {
            // 根据关键词查询
            courses = courseRepository.findByTeacherId(teacherId).stream()
                    .filter(course -> course.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            // 查询所有
            courses = courseRepository.findByTeacherId(teacherId);
        }

        // 手动分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, courses.size());
        List<Course> pagedCourses = start < courses.size() ? courses.subList(start, end) : new ArrayList<>();

        // 转换为前端需要的格式
        List<Map<String, Object>> coursesList = pagedCourses.stream()
                .map(this::getCourseDetailMap)
                .collect(Collectors.toList());

        result.put("courses", coursesList);
        result.put("currentPage", page);
        result.put("totalItems", (long) courses.size());
        result.put("totalPages", (int) Math.ceil((double) courses.size() / size));

        return result;
    }

    @Override
    public Course updateCourse(Long courseId, Course courseData) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("课程不存在"));

        // 更新课程信息
        if (courseData.getName() != null) {
            existingCourse.setName(courseData.getName());
        }
        if (courseData.getDescription() != null) {
            existingCourse.setDescription(courseData.getDescription());
        }
        if (courseData.getCategory() != null) {
            existingCourse.setCategory(courseData.getCategory());
        }
        if (courseData.getDuration() != null) {
            existingCourse.setDuration(courseData.getDuration());
        }
        if (courseData.getDifficulty() != null) {
            existingCourse.setDifficulty(courseData.getDifficulty());
        }
        if (courseData.getCredits() != null) {
            existingCourse.setCredits(courseData.getCredits());
        }

        // 保存更新后的课程
        return courseRepository.save(existingCourse);
    }

    @Override
    public String uploadCourseCover(Long courseId, MultipartFile file) {
        // 实现上传课程封面图片的逻辑
        return ""; // 暂时返回空字符串，需要实现实际逻辑
    }

    @Override
    public Map<String, Object> uploadCourseMaterial(Long courseId, MultipartFile file) {
        // 实现上传课程资料的逻辑
        return new HashMap<>(); // 暂时返回空Map，需要实现实际逻辑
    }

    @Override
    public List<Map<String, Object>> getCourseMaterials(Long courseId) {
        // 实现获取课程资料列表的逻辑
        return new ArrayList<>(); // 暂时返回空列表，需要实现实际逻辑
    }

    @Override
    public List<Map<String, Object>> getChapterSections(Long chapterId) {
        List<CourseSection> sections = courseSectionRepository.findByChapterIdOrderBySortOrderAsc(chapterId);

        return sections.stream().map(section -> {
            Map<String, Object> sectionMap = new HashMap<>();
            sectionMap.put("id", section.getId());
            sectionMap.put("chapterId", section.getChapterId());
            sectionMap.put("title", section.getTitle());
            sectionMap.put("content", section.getContent());
            sectionMap.put("duration", section.getDuration());
            sectionMap.put("videoUrl", section.getVideoUrl());
            sectionMap.put("resourceUrl", section.getResourceUrl());
            sectionMap.put("sortOrder", section.getSortOrder());
            sectionMap.put("createTime", section.getCreateTime());
            sectionMap.put("updateTime", section.getUpdateTime());
            return sectionMap;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> createChapterSection(Long chapterId, Map<String, Object> sectionData) {
        // 检查章节是否存在
        CourseChapter chapter = courseChapterRepository.findById(chapterId)
                .orElseThrow(() -> new NoSuchElementException("章节不存在"));

        // 创建小节
        CourseSection section = new CourseSection();
        section.setChapterId(chapterId);
        section.setTitle((String) sectionData.get("title"));
        section.setContent((String) sectionData.get("content"));
        section.setDuration((String) sectionData.get("duration"));
        section.setVideoUrl((String) sectionData.get("videoUrl"));
        section.setResourceUrl((String) sectionData.get("resourceUrl"));

        // 设置排序顺序
        Integer sortOrder = (Integer) sectionData.get("sortOrder");
        if (sortOrder == null) {
            // 如果没有指定排序顺序，则放在最后
            long count = courseSectionRepository.countByChapterId(chapterId);
            sortOrder = (int) count + 1;
        }
        section.setSortOrder(sortOrder);

        // 保存小节
        section = courseSectionRepository.save(section);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", section.getId());
        result.put("chapterId", section.getChapterId());
        result.put("title", section.getTitle());
        result.put("content", section.getContent());
        result.put("duration", section.getDuration());
        result.put("videoUrl", section.getVideoUrl());
        result.put("resourceUrl", section.getResourceUrl());
        result.put("sortOrder", section.getSortOrder());
        result.put("createTime", section.getCreateTime());
        result.put("updateTime", section.getUpdateTime());

        return result;
    }

    @Override
    public Map<String, Object> updateChapterSection(Long sectionId, Map<String, Object> sectionData) {
        // 检查小节是否存在
        CourseSection section = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new NoSuchElementException("小节不存在"));

        // 更新小节信息
        if (sectionData.containsKey("title")) {
            section.setTitle((String) sectionData.get("title"));
        }
        if (sectionData.containsKey("content")) {
            section.setContent((String) sectionData.get("content"));
        }
        if (sectionData.containsKey("duration")) {
            section.setDuration((String) sectionData.get("duration"));
        }
        if (sectionData.containsKey("videoUrl")) {
            section.setVideoUrl((String) sectionData.get("videoUrl"));
        }
        if (sectionData.containsKey("resourceUrl")) {
            section.setResourceUrl((String) sectionData.get("resourceUrl"));
        }
        if (sectionData.containsKey("sortOrder")) {
            section.setSortOrder((Integer) sectionData.get("sortOrder"));
        }

        // 保存更新后的小节
        section = courseSectionRepository.save(section);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", section.getId());
        result.put("chapterId", section.getChapterId());
        result.put("title", section.getTitle());
        result.put("content", section.getContent());
        result.put("duration", section.getDuration());
        result.put("videoUrl", section.getVideoUrl());
        result.put("resourceUrl", section.getResourceUrl());
        result.put("sortOrder", section.getSortOrder());
        result.put("createTime", section.getCreateTime());
        result.put("updateTime", section.getUpdateTime());

        return result;
    }

    @Override
    public void deleteChapterSection(Long sectionId) {
        courseSectionRepository.deleteById(sectionId);
    }

    @Override
    public Map<String, Long> getCourseCreationTrend(int days) {
        Map<String, Long> trend = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // 获取最近days天的日期
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < days; i++) {
            Date date = calendar.getTime();
            String dateStr = dateFormat.format(date);
            trend.put(dateStr, 0L);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        
        // 获取所有课程
        List<Course> allCourses = courseRepository.findAll();
        
        // 统计每天创建的课程数
        for (Course course : allCourses) {
            Date createdAt = course.getCreatedAt();
            if (createdAt != null) {
                String dateStr = dateFormat.format(createdAt);
                if (trend.containsKey(dateStr)) {
                    trend.put(dateStr, trend.get(dateStr) + 1);
                }
            }
        }
        
        return trend;
    }

    @Override
    public Map<String, Double> getCourseRatingStats() {
        // 实际中应该根据课程评分数据计算
        // 简化实现
        Map<String, Double> ratingStats = new HashMap<>();
        ratingStats.put("averageRating", 4.5);
        ratingStats.put("5星", 60.0);
        ratingStats.put("4星", 25.0);
        ratingStats.put("3星", 10.0);
        ratingStats.put("2星", 3.0);
        ratingStats.put("1星", 2.0);
        return ratingStats;
    }

    @Override
    public Map<String, Object> getCourseStatistics(String userEmail) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取所有选修记录
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserEmail(userEmail);
        
        // 计算总课程数
        int totalCourses = enrollments.size();
        statistics.put("totalCourses", totalCourses);
        
        // 计算已完成课程数
        long completedCourses = enrollments.stream().filter(CourseEnrollment::getIsCompleted).count();
        statistics.put("completedCourses", completedCourses);
        
        // 计算进行中课程数
        statistics.put("inProgressCourses", totalCourses - completedCourses);
        
        // 计算总学习时间（分钟）
        int totalStudyTime = enrollments.stream()
                .filter(e -> e.getTotalStudyTime() != null)
                .mapToInt(CourseEnrollment::getTotalStudyTime)
                .sum();
        statistics.put("totalStudyTime", totalStudyTime);
        
        // 计算平均完成率
        double avgCompletion = enrollments.stream()
                .mapToInt(CourseEnrollment::getProgress)
                .average()
                .orElse(0);
        statistics.put("averageCompletion", avgCompletion);
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> getCourseTranscript(String userEmail) {
        // 获取所有已完成的选修记录
        List<CourseEnrollment> completedCourses = courseEnrollmentRepository.findByUserEmailAndIsCompletedTrue(userEmail);
        
        // 构建成绩单
        List<Map<String, Object>> transcript = new ArrayList<>();
        for (CourseEnrollment enrollment : completedCourses) {
            Course course = getCourseById(enrollment.getCourseId());
            if (course != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("courseId", course.getId());
                record.put("courseName", course.getName());
                record.put("category", course.getCategory());
                record.put("credits", course.getCredits());
                record.put("completionDate", enrollment.getLastAccessTime());
                record.put("studyTime", enrollment.getTotalStudyTime());
                
                transcript.add(record);
            }
        }
        
        return transcript;
    }

    @Override
    public Map<String, Object> getCourseApprovalDetail(Long courseId) {
        // 获取课程详情
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("课程不存在"));
        
        // 构建审核详情
        Map<String, Object> approvalDetail = getCourseDetailMap(course);
        
        // 获取章节信息
        List<Map<String, Object>> chapters = getCourseChapters(courseId);
        approvalDetail.put("chapters", chapters);
        
        return approvalDetail;
    }

    @Override
    public List<Course> getPendingApprovalCourses() {
        return courseRepository.findByApprovedFalse();
    }

    @Override
    public int calculateStudentGrowthRate(Long teacherId) {
        // 计算过去30天内该教师课程的新增学生数量
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        
        // 获取该教师的所有课程
        List<Course> teacherCourses = courseRepository.findByTeacherIdAndApprovedTrue(teacherId);
        
        // 如果教师没有课程，返回0
        if (teacherCourses.isEmpty()) {
            return 0;
        }
        
        // 收集课程ID
        List<Long> courseIds = teacherCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 获取过去30天内的新选课数量
        long recentEnrollments = courseEnrollmentRepository.countByCreatedAtAfterAndCourseIdIn(thirtyDaysAgo, courseIds);
        
        // 获取该教师所有课程的总选课数量
        long totalEnrollments = 0;
        for (Long courseId : courseIds) {
            totalEnrollments += courseEnrollmentRepository.countByCourseId(courseId);
        }
        
        // 计算30天前的选课数量
        long oldEnrollments = totalEnrollments - recentEnrollments;
        
        // 计算增长率
        if (oldEnrollments > 0) {
            return (int) ((recentEnrollments * 100) / oldEnrollments);
        } else {
            return recentEnrollments > 0 ? 100 : 0; // 如果之前没有选课，但现在有，则增长率为100%
        }
    }



    // 辅助方法：根据用户ID获取邮箱
    private String getUserEmailById(Long userId) {
        // 实际实现应该查询用户表
        // 简化处理，假设User表的主键是userEmail
        // 在真实项目中，应该注入UserRepository并查询用户信息
        return "user" + userId + "@example.com";  // 临时返回模拟结果
    }

    // 辅助方法：计算指定日期之前的平均进度
    private double calculateAverageProgressBefore(String userEmail, Date date) {
        // 实际实现应该查询历史记录表获取指定日期的进度快照
        // 简化处理，使用当前进度的80%作为过去的进度
        // 在真实项目中，应该有进度历史记录表
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserEmail(userEmail);
        return enrollments.stream()
                .mapToDouble(enrollment -> enrollment.getProgress() * 0.8) // 假设过去的进度是当前的80%
                .average()
                .orElse(0.0);
    }
}

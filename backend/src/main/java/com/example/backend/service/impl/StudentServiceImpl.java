package com.example.backend.service.impl;

import com.example.backend.entity.Course;
import com.example.backend.entity.CourseEnrollment;
import com.example.backend.entity.User;
import com.example.backend.repository.CourseEnrollmentRepository;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生管理服务实现类
 */
@Service
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    public StudentServiceImpl(UserRepository userRepository,
                             CourseRepository courseRepository,
                             CourseEnrollmentRepository courseEnrollmentRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
    }

    @Override
    public Map<String, Object> getTeacherStudents(Long teacherId, String keyword, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取该教师所有的课程
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = teacherCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 如果没有课程，返回空结果
        if (courseIds.isEmpty()) {
            result.put("students", new ArrayList<>());
            result.put("totalItems", 0L);
            result.put("totalPages", 0);
            result.put("currentPage", page);
            return result;
        }
        
        // 获取所有选修过该教师课程的学生
        List<String> studentEmails = new ArrayList<>();
        for (Long courseId : courseIds) {
            List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseId(courseId);
            for (CourseEnrollment enrollment : enrollments) {
                if (!studentEmails.contains(enrollment.getUserEmail())) {
                    studentEmails.add(enrollment.getUserEmail());
                }
            }
        }
        
        // 分页处理学生列表
        int start = (page - 1) * size;
        int end = Math.min(start + size, studentEmails.size());
        List<String> pagedStudentEmails = start < studentEmails.size() ? 
                studentEmails.subList(start, end) : new ArrayList<>();
        
        // 获取学生详细信息
        List<Map<String, Object>> students = new ArrayList<>();
        for (String email : pagedStudentEmails) {
            User student = userRepository.findByUsername(email).orElse(null);
            if (student != null) {
                Map<String, Object> studentMap = new HashMap<>();
                studentMap.put("id", student.getId());
                studentMap.put("name", student.getUsername());
                studentMap.put("email", student.getUsername());
                
                // 获取该学生选修的该教师的课程数量
                int enrolledCourseCount = 0;
                for (Long courseId : courseIds) {
                    if (courseEnrollmentRepository.existsByCourseIdAndUserEmail(courseId, email)) {
                        enrolledCourseCount++;
                    }
                }
                studentMap.put("enrolledCourses", enrolledCourseCount);
                
                students.add(studentMap);
            }
        }
        
        result.put("students", students);
        result.put("totalItems", (long) studentEmails.size());
        result.put("totalPages", (int) Math.ceil((double) studentEmails.size() / size));
        result.put("currentPage", page);
        
        return result;
    }

    @Override
    public Map<String, Object> getCourseStudents(Long courseId, String keyword, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "userEmail"));
        
        // 获取课程的选修记录
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseId(courseId);
        
        // 手动分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, enrollments.size());
        List<CourseEnrollment> pagedEnrollments = start < enrollments.size() ? 
                enrollments.subList(start, end) : new ArrayList<>();
        
        // 获取学生详细信息
        List<Map<String, Object>> students = new ArrayList<>();
        for (CourseEnrollment enrollment : pagedEnrollments) {
            User student = userRepository.findByUsername(enrollment.getUserEmail()).orElse(null);
            if (student != null) {
                Map<String, Object> studentMap = new HashMap<>();
                studentMap.put("id", student.getId());
                studentMap.put("name", student.getUsername());
                studentMap.put("email", student.getUsername());
                studentMap.put("progress", enrollment.getProgress());
                studentMap.put("isCompleted", enrollment.getIsCompleted());
                studentMap.put("enrollTime", enrollment.getEnrollTime());
                studentMap.put("lastAccessTime", enrollment.getLastAccessTime());
                
                students.add(studentMap);
            }
        }
        
        result.put("students", students);
        result.put("totalItems", (long) enrollments.size());
        result.put("totalPages", (int) Math.ceil((double) enrollments.size() / size));
        result.put("currentPage", page);
        
        return result;
    }

    @Override
    public boolean isTeacherStudent(Long teacherId, Long studentId) {
        // 获取学生信息
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) {
            return false;
        }
        
        // 获取教师的所有课程
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = teacherCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 如果没有课程，返回false
        if (courseIds.isEmpty()) {
            return false;
        }
        
        // 检查学生是否选修了该教师的任何课程
        for (Long courseId : courseIds) {
            if (courseEnrollmentRepository.existsByCourseIdAndUserEmail(courseId, student.getUsername())) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Map<String, Object> getStudentDetail(Long studentId, Long teacherId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取学生信息
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) {
            return result;
        }
        
        result.put("id", student.getId());
        result.put("name", student.getUsername());
        result.put("email", student.getUsername());
        
        // 获取教师的所有课程
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = teacherCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 获取学生选修的该教师的课程
        List<Map<String, Object>> enrolledCourses = new ArrayList<>();
        for (Long courseId : courseIds) {
            CourseEnrollment enrollment = courseEnrollmentRepository
                    .findByUserEmailAndCourseId(student.getUsername(), courseId)
                    .orElse(null);
            
            if (enrollment != null) {
                Course course = courseRepository.findById(courseId).orElse(null);
                if (course != null) {
                    Map<String, Object> courseInfo = new HashMap<>();
                    courseInfo.put("id", course.getId());
                    courseInfo.put("name", course.getName());
                    courseInfo.put("progress", enrollment.getProgress());
                    courseInfo.put("isCompleted", enrollment.getIsCompleted());
                    courseInfo.put("enrollTime", enrollment.getEnrollTime());
                    
                    enrolledCourses.add(courseInfo);
                }
            }
        }
        
        result.put("enrolledCourses", enrolledCourses);
        result.put("enrolledCoursesCount", enrolledCourses.size());
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getStudentGrades(Long studentId, Long teacherId) {
        // 获取学生信息
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) {
            return new ArrayList<>();
        }
        
        // 获取教师的所有课程
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
        
        // 构建成绩列表
        List<Map<String, Object>> grades = new ArrayList<>();
        for (Course course : teacherCourses) {
            CourseEnrollment enrollment = courseEnrollmentRepository
                    .findByUserEmailAndCourseId(student.getUsername(), course.getId())
                    .orElse(null);
            
            if (enrollment != null) {
                Map<String, Object> gradeInfo = new HashMap<>();
                gradeInfo.put("courseId", course.getId());
                gradeInfo.put("courseName", course.getName());
                gradeInfo.put("grade", enrollment.getGrade());
                gradeInfo.put("progress", enrollment.getProgress());
                gradeInfo.put("isCompleted", enrollment.getIsCompleted());
                
                grades.add(gradeInfo);
            }
        }
        
        return grades;
    }

    @Override
    public void addStudentNote(Long studentId, Long teacherId, String note) {
        // 实现添加学生备注的逻辑
        // 这可能需要额外的实体和Repository
    }

    @Override
    public int countEnrolledCourses(String email) {
        return (int) courseEnrollmentRepository.countByUserEmail(email);
    }

    @Override
    public Map<String, Object> getStudyTimeStats(String email) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取所有选修记录
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserEmail(email);
        
        // 计算总学习时间（分钟）
        int totalStudyTime = enrollments.stream()
                .filter(e -> e.getTotalStudyTime() != null)
                .mapToInt(CourseEnrollment::getTotalStudyTime)
                .sum();
        
        stats.put("totalStudyTime", totalStudyTime);
        stats.put("totalStudyHours", totalStudyTime / 60);
        stats.put("averageStudyTimePerCourse", enrollments.isEmpty() ? 0 : totalStudyTime / enrollments.size());
        
        return stats;
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        // 实现修改密码的逻辑
        // 需要UserService的支持
        return false;
    }

    @Override
    public Map<String, Object> getStudyProgress(String email, Long courseId) {
        Map<String, Object> progress = new HashMap<>();
        
        // 获取选修记录
        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByUserEmailAndCourseId(email, courseId)
                .orElse(null);
        
        if (enrollment != null) {
            progress.put("progress", enrollment.getProgress());
            progress.put("isCompleted", enrollment.getIsCompleted());
            progress.put("lastAccessedChapterId", enrollment.getLastAccessedChapterId());
            progress.put("totalStudyTime", enrollment.getTotalStudyTime());
            progress.put("enrollTime", enrollment.getEnrollTime());
            progress.put("lastAccessTime", enrollment.getLastAccessTime());
        }
        
        return progress;
    }

    @Override
    public Map<String, Object> getStudentCourseProgress(Long studentId, Long courseId) {
        // 获取学生信息
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) {
            return new HashMap<>();
        }
        
        return getStudyProgress(student.getUsername(), courseId);
    }

    @Override
    public Map<String, Object> getStudentOverallProgress(Long studentId, Long teacherId) {
        // 获取学生信息
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) {
            return new HashMap<>();
        }
        
        // 获取教师的所有课程
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = teacherCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 获取学生选修的该教师的课程的进度
        Map<String, Object> overallProgress = new HashMap<>();
        List<CourseEnrollment> enrollments = new ArrayList<>();
        
        for (Long courseId : courseIds) {
            CourseEnrollment enrollment = courseEnrollmentRepository
                    .findByUserEmailAndCourseId(student.getUsername(), courseId)
                    .orElse(null);
            
            if (enrollment != null) {
                enrollments.add(enrollment);
            }
        }
        
        // 计算平均进度
        double averageProgress = enrollments.isEmpty() ? 0 :
                enrollments.stream()
                        .mapToInt(CourseEnrollment::getProgress)
                        .average()
                        .orElse(0);
        
        // 计算完成率
        double completionRate = enrollments.isEmpty() ? 0 :
                (double) enrollments.stream()
                        .filter(CourseEnrollment::getIsCompleted)
                        .count() / enrollments.size();
        
        overallProgress.put("averageProgress", averageProgress);
        overallProgress.put("completionRate", completionRate);
        overallProgress.put("enrolledCourses", enrollments.size());
        overallProgress.put("completedCourses", enrollments.stream()
                .filter(CourseEnrollment::getIsCompleted)
                .count());
        
        return overallProgress;
    }

    @Override
    public List<Map<String, Object>> getStudentCourseAssignments(Long studentId, Long courseId) {
        // 实现获取学生在特定课程的作业提交情况的逻辑
        // 需要AssignmentSubmissionRepository的支持
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getStudentAllAssignments(Long studentId, Long teacherId) {
        // 实现获取学生在所有课程的作业提交情况的逻辑
        // 需要AssignmentSubmissionRepository的支持
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getStudentCourseExams(Long studentId, Long courseId) {
        // 实现获取学生在特定课程的考试成绩的逻辑
        // 需要ExamResultRepository的支持
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getStudentAllExams(Long studentId, Long teacherId) {
        // 实现获取学生在所有课程的考试成绩的逻辑
        // 需要ExamResultRepository的支持
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> createStudentGroup(Long teacherId, Map<String, Object> groupData) {
        // 实现创建学生分组的逻辑
        // 需要StudentGroupRepository的支持
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getStudentGroups(Long teacherId, String keyword, int page, int size) {
        // 实现获取学生分组列表的逻辑
        // 需要StudentGroupRepository的支持
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> updateStudentGroup(Long groupId, Map<String, Object> groupData) {
        // 实现更新分组信息的逻辑
        // 需要StudentGroupRepository的支持
        return new HashMap<>();
    }

    @Override
    public void deleteStudentGroup(Long groupId) {
        // 实现删除分组的逻辑
        // 需要StudentGroupRepository的支持
    }

    @Override
    public boolean isTeacherGroup(Long teacherId, Long groupId) {
        // 实现验证分组是否属于教师的逻辑
        // 需要StudentGroupRepository的支持
        return false;
    }
} 
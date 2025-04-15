package com.example.backend.service.impl;

import com.example.backend.dto.ProfileDTO;
import com.example.backend.entity.AssignmentSubmission;
import com.example.backend.entity.ExamResult;
import com.example.backend.entity.Profile;
import com.example.backend.repository.ProfileRepository;
import com.example.backend.service.AssignmentService;
import com.example.backend.service.ExamService;
import com.example.backend.service.StudentProfileService;
import com.example.backend.service.StudentService;
import com.example.backend.vo.ProfileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 学生个人资料服务实现类
 */
@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    private final ProfileRepository profileRepository;
    private final AssignmentService assignmentService;
    private final ExamService examService;
    private final StudentService studentService;

    @Value("${file.upload.avatar-dir:images/avatars}")
    private String avatarDir;

    @Value("${file.upload.frontend-path:frontend/public}")
    private String frontendPath;

    @Value("${file.upload.avatar-base-url:/images/avatars}")
    private String avatarBaseUrl;

    public StudentProfileServiceImpl(ProfileRepository profileRepository,
                                    AssignmentService assignmentService,
                                    ExamService examService,
                                    StudentService studentService) {
        this.profileRepository = profileRepository;
        this.assignmentService = assignmentService;
        this.examService = examService;
        this.studentService = studentService;
    }

    @Override
    public Profile initProfile(String email) {
        // 检查是否已存在
        Optional<Profile> existingProfile = profileRepository.findById(email);
        if (existingProfile.isPresent()) {
            return existingProfile.get();
        }

        // 创建新的个人资料
        Profile profile = new Profile();
        profile.setEmail(email);
        profile.setUsername(email);
        profile.setName("学生");
        profile.setGender("未设置");
        profile.setBirthday(LocalDate.now().minusYears(20)); // 默认20岁
        profile.setBio("这个人很懒，什么都没有留下...");
        profile.setAvatar("/images/avatars/default.png");

        return profileRepository.save(profile);
    }

    @Override
    public Profile getProfile(String email) {
        return profileRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("个人资料不存在"));
    }

    @Override
    public Profile updateProfile(Profile profile) {
        // 检查是否存在
        if (!profileRepository.existsById(profile.getEmail())) {
            throw new RuntimeException("个人资料不存在");
        }

        return profileRepository.save(profile);
    }
    
    @Override
    public ProfileVO getProfileVO(String email) {
        Profile profile = getProfile(email);
        
        ProfileVO profileVO = new ProfileVO();
        profileVO.setEmail(profile.getEmail());
        profileVO.setUsername(profile.getUsername());
        profileVO.setName(profile.getName());
        profileVO.setPhoneNumber(profile.getPhoneNumber());
        profileVO.setGender(profile.getGender());
        profileVO.setBirthday(profile.getBirthday() != null ? profile.getBirthday().toString() : null);
        profileVO.setBio(profile.getBio());
        profileVO.setAvatar(profile.getAvatar());
        
        return profileVO;
    }
    
    @Override
    public ProfileVO updateProfileFromDTO(ProfileDTO profileDTO) {
        Profile profile = getProfile(profileDTO.getEmail());
        
        // 更新个人资料字段
        if (profileDTO.getUsername() != null) {
            profile.setUsername(profileDTO.getUsername());
        }
        if (profileDTO.getName() != null) {
            profile.setName(profileDTO.getName());
        }
        if (profileDTO.getPhoneNumber() != null) {
            profile.setPhoneNumber(profileDTO.getPhoneNumber());
        }
        if (profileDTO.getGender() != null) {
            profile.setGender(profileDTO.getGender());
        }
        if (profileDTO.getBirthday() != null) {
            profile.setBirthday(LocalDate.parse(profileDTO.getBirthday(), DateTimeFormatter.ISO_DATE));
        }
        if (profileDTO.getBio() != null) {
            profile.setBio(profileDTO.getBio());
        }
        
        // 保存更新后的个人资料
        profile = updateProfile(profile);
        
        // 转换为VO并返回
        return getProfileVO(profile.getEmail());
    }
    
    @Override
    public Map<String, String> uploadAvatar(String email, MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的文件");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;
        
        // 确保目录存在
        String uploadDir = frontendPath + "/" + avatarDir;
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 保存文件
        Path filePath = Paths.get(uploadDir, filename);
        file.transferTo(filePath.toFile());
        
        // 更新用户头像
        Profile profile = getProfile(email);
        String avatarUrl = avatarBaseUrl + "/" + filename;
        profile.setAvatar(avatarUrl);
        updateProfile(profile);
        
        // 返回头像URL
        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", avatarUrl);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getStudyOverview(String email) {
        Map<String, Object> overview = new HashMap<>();
        
        // 获取已完成的作业数量
        int completedAssignments = assignmentService.countCompletedAssignments(email);
        // 获取待完成的作业数量
        int pendingAssignments = assignmentService.countPendingAssignments(email);
        // 获取已参加的考试数量
        int completedExams = examService.countCompletedExams(email);
        // 获取平均成绩
        double averageScore = examService.calculateAverageScore(email);
        // 获取已选课程数量
        int enrolledCourses = studentService.countEnrolledCourses(email);
        
        overview.put("completedAssignments", completedAssignments);
        overview.put("pendingAssignments", pendingAssignments);
        overview.put("completedExams", completedExams);
        overview.put("averageScore", averageScore);
        overview.put("enrolledCourses", enrolledCourses);
        
        return overview;
    }
    
    @Override
    public List<AssignmentSubmission> getAssignmentSubmissions(String email, String status) {
        if (status != null && !status.isEmpty()) {
            return assignmentService.getAssignmentSubmissionsByStatus(email, status);
        } else {
            return assignmentService.getAllAssignmentSubmissions(email);
        }
    }
    
    @Override
    public List<ExamResult> getExamResults(String email, Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            return examService.getExamResultsByDateRange(email, startDate, endDate);
        } else {
            return examService.getCompletedExams(email);
        }
    }
    
    @Override
    public Map<String, Object> getStudyStatistics(String email) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取作业统计数据
        Map<String, Object> assignmentStats = assignmentService.getAssignmentStats(email);
        
        // 获取考试统计数据
        Map<String, Object> examStats = examService.getExamStats(email);
        
        // 获取学习时长统计
        Map<String, Object> studyTimeStats = studentService.getStudyTimeStats(email);
        
        statistics.put("assignments", assignmentStats);
        statistics.put("exams", examStats);
        statistics.put("studyTime", studyTimeStats);
        
        return statistics;
    }
    
    @Override
    public List<Map<String, Object>> getLearningRecords(String email) {
        // TODO: 应从数据库中获取学习记录
        // 由于目前没有学习记录表，这里返回模拟数据
        List<Map<String, Object>> records = new ArrayList<>();
        
        // 模拟数据
        Map<String, Object> record1 = new HashMap<>();
        record1.put("id", 1);
        record1.put("type", "course");
        record1.put("title", "Java编程基础");
        record1.put("date", "2023-05-15");
        record1.put("duration", 45); // 分钟
        record1.put("progress", 30); // 百分比
        records.add(record1);
        
        Map<String, Object> record2 = new HashMap<>();
        record2.put("id", 2);
        record2.put("type", "assignment");
        record2.put("title", "Java编程作业1");
        record2.put("date", "2023-05-16");
        record2.put("status", "已提交");
        record2.put("score", 85);
        records.add(record2);
        
        Map<String, Object> record3 = new HashMap<>();
        record3.put("id", 3);
        record3.put("type", "exam");
        record3.put("title", "Java期中考试");
        record3.put("date", "2023-05-20");
        record3.put("status", "已完成");
        record3.put("score", 92);
        records.add(record3);
        
        return records;
    }
    
    @Override
    public Map<String, Object> getStudyTrend(String email) {
        Map<String, Object> trend = new HashMap<>();
        
        // 学习时长趋势
        List<Map<String, Object>> studyTimeTrend = new ArrayList<>();
        String[] weeks = {"第1周", "第2周", "第3周", "第4周", "第5周", "第6周"};
        double[] hours = {10.5, 12.3, 8.7, 15.2, 14.8, 16.5};
        
        for (int i = 0; i < weeks.length; i++) {
            Map<String, Object> weekData = new HashMap<>();
            weekData.put("week", weeks[i]);
            weekData.put("hours", hours[i]);
            studyTimeTrend.add(weekData);
        }
        
        // 成绩趋势
        List<Map<String, Object>> scoreTrend = new ArrayList<>();
        String[] exams = {"Java期中考试", "Web开发测试", "数据库期末考试", "算法测试", "设计模式测试"};
        int[] scores = {92, 88, 95, 85, 90};
        
        for (int i = 0; i < exams.length; i++) {
            Map<String, Object> examData = new HashMap<>();
            examData.put("exam", exams[i]);
            examData.put("score", scores[i]);
            scoreTrend.add(examData);
        }
        
        // 作业完成率趋势
        List<Map<String, Object>> assignmentTrend = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        double[] rates = {0.85, 0.90, 0.95, 0.88, 0.92, 0.96};
        
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[i]);
            monthData.put("rate", rates[i]);
            assignmentTrend.add(monthData);
        }
        
        trend.put("studyTime", studyTimeTrend);
        trend.put("score", scoreTrend);
        trend.put("assignment", assignmentTrend);
        
        return trend;
    }
}

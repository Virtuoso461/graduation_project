package com.example.backend.service;

import com.example.backend.dto.ProfileDTO;
import com.example.backend.entity.AssignmentSubmission;
import com.example.backend.entity.ExamResult;
import com.example.backend.entity.Profile;
import com.example.backend.vo.ProfileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 学生个人资料服务接口
 */
public interface StudentProfileService {

    /**
     * 初始化学生个人资料
     *
     * @param email 学生邮箱
     * @return 初始化的个人资料
     */
    Profile initProfile(String email);

    /**
     * 获取学生个人资料
     *
     * @param email 学生邮箱
     * @return 个人资料
     */
    Profile getProfile(String email);

    /**
     * 更新学生个人资料
     *
     * @param profile 个人资料
     * @return 更新后的个人资料
     */
    Profile updateProfile(Profile profile);
    
    /**
     * 获取学生个人资料VO
     *
     * @param email 学生邮箱
     * @return 个人资料VO
     */
    ProfileVO getProfileVO(String email);
    
    /**
     * 根据DTO更新学生个人资料
     *
     * @param profileDTO 个人资料DTO
     * @return 更新后的个人资料VO
     */
    ProfileVO updateProfileFromDTO(ProfileDTO profileDTO);
    
    /**
     * 上传头像
     *
     * @param email 学生邮箱
     * @param file 头像文件
     * @return 头像URL信息
     * @throws IOException 文件处理异常
     */
    Map<String, String> uploadAvatar(String email, MultipartFile file) throws IOException;
    
    /**
     * 获取学习概况
     *
     * @param email 学生邮箱
     * @return 学习概况数据
     */
    Map<String, Object> getStudyOverview(String email);
    
    /**
     * 获取作业提交情况
     *
     * @param email 学生邮箱
     * @param status 作业状态（可选，pending-待完成，completed-已完成）
     * @return 作业提交列表
     */
    List<AssignmentSubmission> getAssignmentSubmissions(String email, String status);
    
    /**
     * 获取考试成绩
     *
     * @param email 学生邮箱
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 考试成绩列表
     */
    List<ExamResult> getExamResults(String email, Date startDate, Date endDate);
    
    /**
     * 获取学习统计数据
     *
     * @param email 学生邮箱
     * @return 学习统计数据
     */
    Map<String, Object> getStudyStatistics(String email);
    
    /**
     * 获取学习记录
     *
     * @param email 学生邮箱
     * @return 学习记录列表
     */
    List<Map<String, Object>> getLearningRecords(String email);
    
    /**
     * 获取学习趋势
     *
     * @param email 学生邮箱
     * @return 学习趋势数据
     */
    Map<String, Object> getStudyTrend(String email);
}

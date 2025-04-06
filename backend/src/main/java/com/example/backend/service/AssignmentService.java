package com.example.backend.service;

import com.example.backend.dto.AssignmentDTO;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.User;

import java.util.List;
import java.util.Map;

public interface AssignmentService {
    
    /**
     * 创建新作业
     * @param assignmentDTO 作业DTO
     * @param teacher 教师用户
     * @return 创建的作业
     */
    Assignment createAssignment(AssignmentDTO assignmentDTO, User teacher);
    
    /**
     * 更新作业
     * @param id 作业ID
     * @param assignment 更新后的作业
     * @return 更新后的作业
     */
    Assignment updateAssignment(Long id, Assignment assignment);
    
    /**
     * 删除作业
     * @param id 作业ID
     */
    void deleteAssignment(Long id);
    
    /**
     * 根据ID查找作业
     * @param id 作业ID
     * @return 作业对象
     */
    Assignment findAssignmentById(Long id);
    
    /**
     * 获取所有作业（学生用）
     * @return 作业列表
     */
    List<Assignment> findAllAssignments();

    List<Assignment> getAssignmentsByEmail(String email);
    Map<String, Object> getAssignmentStats(String email);
    Assignment getAssignmentById(Long id);
} 
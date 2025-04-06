package com.example.backend.service.impl;

import com.example.backend.dto.AssignmentDTO;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.User;
import com.example.backend.repository.AssignmentRepository;
import com.example.backend.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public Assignment createAssignment(AssignmentDTO assignmentDTO, User teacher) {
        Assignment assignment = assignmentDTO.toEntity();
        assignment.setEmail(teacher.getUsername());
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    @Override
    public List<Assignment> findAllAssignments() {
        return assignmentRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Assignment> getAssignmentsByEmail(String email) {
        return assignmentRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    @Override
    public Map<String, Object> getAssignmentStats(String email) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取不同状态的作业数量
        long pendingCount = assignmentRepository.countByEmailAndStatus(email, "PENDING");
        long submittedCount = assignmentRepository.countByEmailAndStatus(email, "SUBMITTED");
        long gradedCount = assignmentRepository.countByEmailAndStatus(email, "GRADED");
        
        // 获取待完成的作业
        List<Assignment> pendingAssignments = assignmentRepository.findByEmailAndStatusOrderByDueDateAsc(email, "PENDING");
        
        stats.put("pendingCount", pendingCount);
        stats.put("submittedCount", submittedCount);
        stats.put("gradedCount", gradedCount);
        stats.put("totalCount", pendingCount + submittedCount + gradedCount);
        stats.put("pendingAssignments", pendingAssignments);
        
        return stats;
    }

    @Override
    public Assignment findAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作业不存在"));
    }

    @Override
    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作业不存在"));
    }

    @Override
    @Transactional
    public Assignment updateAssignment(Long id, Assignment updatedAssignment) {
        Assignment assignment = findAssignmentById(id);
        
        assignment.setTitle(updatedAssignment.getTitle());
        assignment.setDescription(updatedAssignment.getDescription());
        assignment.setDueDate(updatedAssignment.getDueDate());
        assignment.setUpdatedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }
} 
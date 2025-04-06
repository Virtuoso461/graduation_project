package com.example.backend.repository;

import com.example.backend.entity.Assignment;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    // 根据教师查找作业
    List<Assignment> findByEmailOrderByCreatedAtDesc(String email);
    
    // 查找所有作业（用于学生）
    List<Assignment> findAllByOrderByCreatedAtDesc();

    List<Assignment> findByEmailAndStatusOrderByDueDateAsc(String email, String status);
    long countByEmailAndStatus(String email, String status);
} 
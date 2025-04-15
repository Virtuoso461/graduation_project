package com.example.backend.repository;

import com.example.backend.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // 根据创建者邮箱查询所有考试
    List<Exam> findByCreatorEmail(String creatorEmail);

    // 根据课程名称查询考试
    List<Exam> findByCourseName(String courseName);

    // 查询已发布且截止日期尚未到期的考试
    @Query("SELECT e FROM Exam e WHERE e.isPublished = true AND e.endTime > :now")
    List<Exam> findAvailableExams(@Param("now") Date now);

    // 查询特定类别且已发布的考试
    @Query("SELECT e FROM Exam e WHERE e.category = :category AND e.isPublished = true")
    List<Exam> findByCategory(@Param("category") String category);

    // 根据难度和发布状态查询考试
    @Query("SELECT e FROM Exam e WHERE e.difficulty = :difficulty AND e.isPublished = :isPublished")
    List<Exam> findByDifficultyAndPublished(
            @Param("difficulty") String difficulty,
            @Param("isPublished") Boolean isPublished);

    // 查询所有发布状态的考试
    List<Exam> findByIsPublished(Boolean isPublished);

    // 统计指定日期之后创建的考试数量
    long countByCreatedAtAfter(Date date);

    // 查询当前可参加的考试（开始时间小于等于当前时间，结束时间大于等于当前时间）
    List<Exam> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Date startTime, Date endTime);
}
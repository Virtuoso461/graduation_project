package com.example.backend.repository;

import com.example.backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程数据访问接口
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 根据课程名称模糊查询
     *
     * @param name 课程名称
     * @return 课程列表
     */
    List<Course> findByNameContaining(String name);

    /**
     * 根据课程难度级别查询
     *
     * @param difficulty 难度级别
     * @return 课程列表
     */
    List<Course> findByDifficulty(String difficulty);

    /**
     * 查询最新课程（按照创建时间排序）
     */
    @Query("SELECT c FROM Course c ORDER BY c.createdAt DESC")
    List<Course> findLatestCourses();

    /**
     * 查询优质课程（按照更新时间排序，最近更新的优先）
     */
    @Query("SELECT c FROM Course c ORDER BY c.updatedAt DESC")
    List<Course> findTopRatedCourses();

    /**
     * 根据教师ID查找课程
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> findByTeacherId(Long teacherId);

    /**
     * 查找已审核通过的课程
     *
     * @return 课程列表
     */
    List<Course> findByApprovedTrue();

    /**
     * 根据教师ID和课程名称模糊查询（带分页）
     *
     * @param teacherId 教师ID
     * @param name 课程名称
     * @param pageable 分页参数
     * @return 课程分页数据
     */
    Page<Course> findByTeacherIdAndNameContaining(Long teacherId, String name, Pageable pageable);

    /**
     * 根据教师ID查询（带分页）
     *
     * @param teacherId 教师ID
     * @param pageable 分页参数
     * @return 课程分页数据
     */
    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);

    /**
     * 查找待审核的课程
     *
     * @return 课程列表
     */
    List<Course> findByApprovedFalse();

    /**
     * 查找指定类别的课程
     *
     * @param category 课程类别
     * @return 课程列表
     */
    List<Course> findByCategory(String category);

    /**
     * 查找指定类别且已审核通过的课程
     *
     * @param category 课程类别
     * @return 课程列表
     */
    List<Course> findByCategoryAndApprovedTrue(String category);

    /**
     * 查找指定教师的已审核通过的课程
     *
     * @param teacherId 教师ID
     * @return 已审核通过的课程列表
     */
    List<Course> findByTeacherIdAndApprovedTrue(Long teacherId);
}
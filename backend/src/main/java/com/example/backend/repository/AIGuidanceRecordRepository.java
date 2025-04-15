package com.example.backend.repository;

import com.example.backend.entity.AIGuidanceRecord;
import com.example.backend.entity.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI指导记录数据访问接口
 */
@Repository
public interface AIGuidanceRecordRepository extends JpaRepository<AIGuidanceRecord, Long> {
    
    /**
     * 根据用户邮箱查询指导记录，按创建时间倒序排序
     * 
     * @param userEmail 用户邮箱
     * @param pageable 分页参数
     * @return 指导记录列表
     */
    List<AIGuidanceRecord> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);
    
    /**
     * 根据用户邮箱和主题查询指导记录
     * 
     * @param userEmail 用户邮箱
     * @param topic 主题
     * @param pageable 分页参数
     * @return 指导记录列表
     */
    List<AIGuidanceRecord> findByUserEmailAndTopicOrderByCreatedAtDesc(String userEmail, String topic, Pageable pageable);
    
    /**
     * 统计用户的指导记录数量
     * 
     * @param userEmail 用户邮箱
     * @return 记录数量
     */
    long countByUserEmail(String userEmail);
}

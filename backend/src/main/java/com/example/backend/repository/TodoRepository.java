package com.example.backend.repository;

import com.example.backend.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 待办事项数据访问接口
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * 根据用户ID查找所有待办事项
     * 
     * @param userId 用户ID
     * @return 待办事项列表
     */
    List<Todo> findByUserId(Long userId);

    /**
     * 根据用户ID和完成状态查找待办事项
     * 
     * @param userId 用户ID
     * @param completed 完成状态
     * @return 待办事项列表
     */
    List<Todo> findByUserIdAndCompleted(Long userId, Boolean completed);

    /**
     * 根据用户ID和相关类型查找待办事项
     * 
     * @param userId 用户ID
     * @param relatedType 相关类型
     * @return 待办事项列表
     */
    List<Todo> findByUserIdAndRelatedType(Long userId, String relatedType);

    /**
     * 根据用户ID、相关类型和相关ID查找待办事项
     * 
     * @param userId 用户ID
     * @param relatedType 相关类型
     * @param relatedId 相关ID
     * @return 待办事项列表
     */
    List<Todo> findByUserIdAndRelatedTypeAndRelatedId(Long userId, String relatedType, Long relatedId);

    /**
     * 检查待办事项是否属于指定用户
     * 
     * @param id 待办事项ID
     * @param userId 用户ID
     * @return 如果待办事项属于该用户，则返回true
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}

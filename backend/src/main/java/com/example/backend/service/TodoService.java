package com.example.backend.service;

import java.util.List;
import java.util.Map;

/**
 * 待办事项服务接口
 * 提供待办事项相关的功能
 */
public interface TodoService {

    /**
     * 获取用户的所有待办事项
     * 
     * @param userId 用户ID
     * @return 待办事项列表
     */
    List<Map<String, Object>> getUserTodos(Long userId);

    /**
     * 创建新待办事项
     * 
     * @param userId 用户ID
     * @param todoData 待办事项数据
     * @return 创建的待办事项
     */
    Map<String, Object> createTodo(Long userId, Map<String, Object> todoData);

    /**
     * 获取待办事项详情
     * 
     * @param todoId 待办事项ID
     * @return 待办事项详情
     */
    Map<String, Object> getTodoDetail(Long todoId);

    /**
     * 更新待办事项
     * 
     * @param todoId 待办事项ID
     * @param todoData 待办事项数据
     * @return 更新后的待办事项
     */
    Map<String, Object> updateTodo(Long todoId, Map<String, Object> todoData);

    /**
     * 删除待办事项
     * 
     * @param todoId 待办事项ID
     */
    void deleteTodo(Long todoId);

    /**
     * 标记待办事项为已完成
     * 
     * @param todoId 待办事项ID
     * @return 更新后的待办事项
     */
    Map<String, Object> completeTodo(Long todoId);

    /**
     * 获取用户未完成的待办事项
     * 
     * @param userId 用户ID
     * @return 未完成的待办事项列表
     */
    List<Map<String, Object>> getUserPendingTodos(Long userId);

    /**
     * 获取用户已完成的待办事项
     * 
     * @param userId 用户ID
     * @return 已完成的待办事项列表
     */
    List<Map<String, Object>> getUserCompletedTodos(Long userId);

    /**
     * 检查待办事项是否属于指定用户
     * 
     * @param userId 用户ID
     * @param todoId 待办事项ID
     * @return 如果待办事项属于该用户，则返回true
     */
    boolean isUserTodo(Long userId, Long todoId);
}

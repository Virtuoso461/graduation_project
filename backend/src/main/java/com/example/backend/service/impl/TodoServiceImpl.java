package com.example.backend.service.impl;

import com.example.backend.entity.Todo;
import com.example.backend.repository.TodoRepository;
import com.example.backend.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 待办事项服务实现类
 */
@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * 将Todo实体转换为Map
     */
    private Map<String, Object> convertToMap(Todo todo) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", todo.getId());
        map.put("title", todo.getTitle());
        map.put("description", todo.getDescription());
        map.put("completed", todo.getCompleted());
        map.put("dueDate", todo.getDueDate());
        map.put("priority", todo.getPriority());
        map.put("userId", todo.getUserId());
        map.put("relatedType", todo.getRelatedType());
        map.put("relatedId", todo.getRelatedId());
        map.put("createdAt", todo.getCreatedAt());
        map.put("updatedAt", todo.getUpdatedAt());
        map.put("completedAt", todo.getCompletedAt());
        return map;
    }

    @Override
    public List<Map<String, Object>> getUserTodos(Long userId) {
        List<Todo> todos = todoRepository.findByUserId(userId);
        return todos.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> createTodo(Long userId, Map<String, Object> todoData) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle((String) todoData.get("title"));
        todo.setDescription((String) todoData.get("description"));
        
        if (todoData.containsKey("dueDate")) {
            todo.setDueDate(LocalDateTime.parse((String) todoData.get("dueDate")));
        }
        
        if (todoData.containsKey("priority")) {
            todo.setPriority((Integer) todoData.get("priority"));
        }
        
        if (todoData.containsKey("relatedType")) {
            todo.setRelatedType((String) todoData.get("relatedType"));
        }
        
        if (todoData.containsKey("relatedId")) {
            todo.setRelatedId(Long.valueOf(todoData.get("relatedId").toString()));
        }
        
        Todo savedTodo = todoRepository.save(todo);
        return convertToMap(savedTodo);
    }

    @Override
    public Map<String, Object> getTodoDetail(Long todoId) {
        Optional<Todo> todoOpt = todoRepository.findById(todoId);
        if (todoOpt.isEmpty()) {
            throw new IllegalArgumentException("待办事项不存在");
        }
        
        return convertToMap(todoOpt.get());
    }

    @Override
    public Map<String, Object> updateTodo(Long todoId, Map<String, Object> todoData) {
        Optional<Todo> todoOpt = todoRepository.findById(todoId);
        if (todoOpt.isEmpty()) {
            throw new IllegalArgumentException("待办事项不存在");
        }
        
        Todo todo = todoOpt.get();
        
        if (todoData.containsKey("title")) {
            todo.setTitle((String) todoData.get("title"));
        }
        
        if (todoData.containsKey("description")) {
            todo.setDescription((String) todoData.get("description"));
        }
        
        if (todoData.containsKey("completed")) {
            boolean completed = (Boolean) todoData.get("completed");
            todo.setCompleted(completed);
            
            if (completed) {
                todo.setCompletedAt(LocalDateTime.now());
            } else {
                todo.setCompletedAt(null);
            }
        }
        
        if (todoData.containsKey("dueDate")) {
            if (todoData.get("dueDate") != null) {
                todo.setDueDate(LocalDateTime.parse((String) todoData.get("dueDate")));
            } else {
                todo.setDueDate(null);
            }
        }
        
        if (todoData.containsKey("priority")) {
            todo.setPriority((Integer) todoData.get("priority"));
        }
        
        if (todoData.containsKey("relatedType")) {
            todo.setRelatedType((String) todoData.get("relatedType"));
        }
        
        if (todoData.containsKey("relatedId")) {
            if (todoData.get("relatedId") != null) {
                todo.setRelatedId(Long.valueOf(todoData.get("relatedId").toString()));
            } else {
                todo.setRelatedId(null);
            }
        }
        
        Todo updatedTodo = todoRepository.save(todo);
        return convertToMap(updatedTodo);
    }

    @Override
    public void deleteTodo(Long todoId) {
        if (!todoRepository.existsById(todoId)) {
            throw new IllegalArgumentException("待办事项不存在");
        }
        
        todoRepository.deleteById(todoId);
    }

    @Override
    public Map<String, Object> completeTodo(Long todoId) {
        Optional<Todo> todoOpt = todoRepository.findById(todoId);
        if (todoOpt.isEmpty()) {
            throw new IllegalArgumentException("待办事项不存在");
        }
        
        Todo todo = todoOpt.get();
        todo.setCompleted(true);
        todo.setCompletedAt(LocalDateTime.now());
        
        Todo updatedTodo = todoRepository.save(todo);
        return convertToMap(updatedTodo);
    }

    @Override
    public List<Map<String, Object>> getUserPendingTodos(Long userId) {
        List<Todo> todos = todoRepository.findByUserIdAndCompleted(userId, false);
        return todos.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getUserCompletedTodos(Long userId) {
        List<Todo> todos = todoRepository.findByUserIdAndCompleted(userId, true);
        return todos.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserTodo(Long userId, Long todoId) {
        return todoRepository.existsByIdAndUserId(todoId, userId);
    }
}

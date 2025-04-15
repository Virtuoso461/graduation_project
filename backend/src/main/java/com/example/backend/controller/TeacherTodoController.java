package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.TodoService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师待办事项控制器
 * 提供教师待办事项管理功能
 */
@RestController
@RequestMapping("/api/teacher/todos")
public class TeacherTodoController {

    private final UserService userService;
    private final TodoService todoService;

    @Autowired
    public TeacherTodoController(UserService userService, TodoService todoService) {
        this.userService = userService;
        this.todoService = todoService;
    }

    /**
     * 获取当前登录的教师ID
     */
    private Long getCurrentTeacherId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findByUsername(userEmail);
        
        if (user == null || user.getRole() != Role.TEACHER) {
            throw new SecurityException("非教师账号，无权访问");
        }
        
        return user.getId();
    }

    /**
     * 获取教师的所有待办事项
     * 
     * @return 待办事项列表
     */
    @GetMapping()
    public Result<List<Map<String, Object>>> getTodos() {
        try {
            Long teacherId = getCurrentTeacherId();
            
            List<Map<String, Object>> todos = todoService.getUserTodos(teacherId);
            return Result.success(todos);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 创建新待办事项
     * 
     * @param todoData 待办事项数据
     * @return 创建结果
     */
    @PostMapping()
    public Result<Map<String, Object>> createTodo(@RequestBody Map<String, Object> todoData) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            Map<String, Object> createdTodo = todoService.createTodo(teacherId, todoData);
            return Result.success(createdTodo, "待办事项创建成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("创建待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 获取待办事项详情
     * 
     * @param id 待办事项ID
     * @return 待办事项详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getTodoDetail(@PathVariable Long id) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            // 验证待办事项是否属于该教师
            boolean todoOwned = todoService.isUserTodo(teacherId, id);
            if (!todoOwned) {
                return Result.forbidden("该待办事项不属于您，无权查看");
            }
            
            Map<String, Object> todo = todoService.getTodoDetail(id);
            return Result.success(todo);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取待办事项详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新待办事项
     * 
     * @param id 待办事项ID
     * @param todoData 待办事项数据
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateTodo(@PathVariable Long id, @RequestBody Map<String, Object> todoData) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            // 验证待办事项是否属于该教师
            boolean todoOwned = todoService.isUserTodo(teacherId, id);
            if (!todoOwned) {
                return Result.forbidden("该待办事项不属于您，无权修改");
            }
            
            Map<String, Object> updatedTodo = todoService.updateTodo(id, todoData);
            return Result.success(updatedTodo, "待办事项更新成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("更新待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 删除待办事项
     * 
     * @param id 待办事项ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteTodo(@PathVariable Long id) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            // 验证待办事项是否属于该教师
            boolean todoOwned = todoService.isUserTodo(teacherId, id);
            if (!todoOwned) {
                return Result.forbidden("该待办事项不属于您，无权删除");
            }
            
            todoService.deleteTodo(id);
            return Result.success("待办事项删除成功");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("删除待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 标记待办事项为已完成
     * 
     * @param id 待办事项ID
     * @return 标记结果
     */
    @PostMapping("/{id}/complete")
    public Result<Map<String, Object>> completeTodo(@PathVariable Long id) {
        try {
            Long teacherId = getCurrentTeacherId();
            
            // 验证待办事项是否属于该教师
            boolean todoOwned = todoService.isUserTodo(teacherId, id);
            if (!todoOwned) {
                return Result.forbidden("该待办事项不属于您，无权操作");
            }
            
            Map<String, Object> updatedTodo = todoService.completeTodo(id);
            return Result.success(updatedTodo, "待办事项已标记为完成");
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("标记待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 获取未完成的待办事项
     * 
     * @return 未完成的待办事项列表
     */
    @GetMapping("/pending")
    public Result<List<Map<String, Object>>> getPendingTodos() {
        try {
            Long teacherId = getCurrentTeacherId();
            
            List<Map<String, Object>> pendingTodos = todoService.getUserPendingTodos(teacherId);
            return Result.success(pendingTodos);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取未完成待办事项失败: " + e.getMessage());
        }
    }

    /**
     * 获取已完成的待办事项
     * 
     * @return 已完成的待办事项列表
     */
    @GetMapping("/completed")
    public Result<List<Map<String, Object>>> getCompletedTodos() {
        try {
            Long teacherId = getCurrentTeacherId();
            
            List<Map<String, Object>> completedTodos = todoService.getUserCompletedTodos(teacherId);
            return Result.success(completedTodos);
        } catch (SecurityException e) {
            return Result.forbidden(e.getMessage());
        } catch (Exception e) {
            return Result.failed("获取已完成待办事项失败: " + e.getMessage());
        }
    }
}

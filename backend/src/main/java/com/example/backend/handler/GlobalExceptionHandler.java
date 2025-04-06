package com.example.backend.handler;

import com.example.backend.common.Result;
import com.example.backend.common.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理类
 * 用于统一处理系统中的异常，返回标准的Result格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     * 
     * @param e 方法参数校验异常
     * @return 返回包含验证错误信息的Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder message = new StringBuilder();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            message.append(fieldError.getField())
                   .append(": ")
                   .append(fieldError.getDefaultMessage())
                   .append(", ");
        }
        
        if (message.length() > 0) {
            message.delete(message.length() - 2, message.length());
        }
        
        return Result.validateFailed(message.toString());
    }
    
    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @return 返回包含错误信息的Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder message = new StringBuilder();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            message.append(fieldError.getField())
                   .append(": ")
                   .append(fieldError.getDefaultMessage())
                   .append(", ");
        }
        
        if (message.length() > 0) {
            message.delete(message.length() - 2, message.length());
        }
        
        return Result.validateFailed(message.toString());
    }
    
    /**
     * 处理认证异常
     * 
     * @param e 认证异常
     * @return 返回未授权的Result
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public Result<String> handleAuthenticationException(Exception e) {
        return Result.unauthorized(e.getMessage());
    }
    
    /**
     * 处理访问拒绝异常
     * 
     * @param e 访问拒绝异常
     * @return 返回禁止访问的Result
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        return Result.forbidden(e.getMessage());
    }
    
    /**
     * 处理资源未找到异常
     * 
     * @param e 资源未找到异常
     * @return 返回资源未找到的Result
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<String> handleNotFoundException(NoHandlerFoundException e) {
        return Result.notFound("请求的资源不存在: " + e.getRequestURL());
    }
    
    /**
     * 处理所有其他未处理的异常
     * 
     * @param e 异常
     * @return 返回一般错误Result
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<String> handleAllExceptions(Exception e) {
        return Result.failed(e.getMessage());
    }
} 
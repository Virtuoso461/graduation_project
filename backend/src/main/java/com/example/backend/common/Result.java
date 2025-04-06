package com.example.backend.common;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 * 
 * @param <T> 返回数据的类型
 */
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 返回信息
     */
    private String message;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp = System.currentTimeMillis();
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 私有构造函数，禁止直接创建
     */
    private Result() {}
    
    /**
     * 构造函数
     *
     * @param code 状态码
     * @param message 返回信息
     * @param data 返回数据
     * @param success 是否成功
     */
    private Result(Integer code, String message, T data, Boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }
    
    /**
     * 成功返回结果
     *
     * @param <T> 返回数据类型
     * @return 成功结果，无数据
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, true);
    }
    
    /**
     * 成功返回结果
     *
     * @param <T> 返回数据类型
     * @param message 成功信息
     * @return 成功结果，无数据
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, null, true);
    }
    
    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param <T> 返回数据类型
     * @return 成功结果，带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, true);
    }
    
    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param message 成功信息
     * @param <T> 返回数据类型
     * @return 成功结果，带数据和自定义信息
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data, true);
    }
    
    /**
     * 失败返回结果
     *
     * @param <T> 返回数据类型
     * @param errorCode 错误码
     * @return 失败结果
     */
    public static <T> Result<T> failed(IErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null, false);
    }
    
    /**
     * 失败返回结果
     *
     * @param <T> 返回数据类型
     * @param message 错误信息
     * @return 失败结果
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCode.FAILED.getCode(), message, null, false);
    }
    
    /**
     * 失败返回结果
     *
     * @param <T> 返回数据类型
     * @return 失败结果
     */
    public static <T> Result<T> failed() {
        return failed(ResultCode.FAILED);
    }
    
    /**
     * 参数验证失败返回结果
     *
     * @param <T> 返回数据类型
     * @return 参数验证失败结果
     */
    public static <T> Result<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }
    
    /**
     * 参数验证失败返回结果
     *
     * @param <T> 返回数据类型
     * @param message 错误信息
     * @return 参数验证失败结果
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), message, null, false);
    }
    
    /**
     * 未登录返回结果
     *
     * @param <T> 返回数据类型
     * @return 未登录结果
     */
    public static <T> Result<T> unauthorized() {
        return failed(ResultCode.UNAUTHORIZED);
    }
    
    /**
     * 未登录返回结果
     *
     * @param <T> 返回数据类型
     * @param message 错误信息
     * @return 未登录结果
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), message, null, false);
    }
    
    /**
     * 未授权返回结果
     *
     * @param <T> 返回数据类型
     * @return 未授权结果
     */
    public static <T> Result<T> forbidden() {
        return failed(ResultCode.FORBIDDEN);
    }
    
    /**
     * 未授权返回结果
     *
     * @param <T> 返回数据类型
     * @param message 错误信息
     * @return 未授权结果
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), message, null, false);
    }
    
    /**
     * 资源不存在返回结果
     * 
     * @param <T> 返回数据类型
     * @return 资源不存在结果
     */
    public static <T> Result<T> notFound() {
        return failed(ResultCode.NOT_FOUND);
    }
    
    /**
     * 资源不存在返回结果
     * 
     * @param <T> 返回数据类型
     * @param message 错误信息
     * @return 资源不存在结果
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(ResultCode.NOT_FOUND.getCode(), message, null, false);
    }
    
    // Getters and Setters
    
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
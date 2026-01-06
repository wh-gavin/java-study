package com.custom.valve.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 阀门异常基类
 * 
 * 所有阀门相关的异常都应该继承此类
 * 提供阀门名称、错误码、上下文信息等额外信息
 * 
 * 设计模式：异常包装模式
 */
public class ValveException extends Exception {
    
    private final String valveName;      // 抛出异常的阀门名称
    private final String errorCode;      // 错误码，用于分类错误
    private final Map<String, Object> context; // 异常上下文信息
    
    /**
     * 构造函数
     * 
     * @param valveName 阀门名称
     * @param message 异常消息
     */
    public ValveException(String valveName, String message) {
        super(message);
        this.valveName = valveName;
        this.errorCode = "VALVE_ERROR";
        this.context = new HashMap<>();
    }
    
    /**
     * 构造函数
     * 
     * @param valveName 阀门名称
     * @param message 异常消息
     * @param cause 根本原因异常
     */
    public ValveException(String valveName, String message, Throwable cause) {
        super(message, cause);
        this.valveName = valveName;
        this.errorCode = "VALVE_ERROR";
        this.context = new HashMap<>();
    }
    
    /**
     * 构造函数
     * 
     * @param valveName 阀门名称
     * @param errorCode 错误码
     * @param message 异常消息
     */
    public ValveException(String valveName, String errorCode, String message) {
        super(message);
        this.valveName = valveName;
        this.errorCode = errorCode;
        this.context = new HashMap<>();
    }
    
    /**
     * 构造函数
     * 
     * @param valveName 阀门名称
     * @param errorCode 错误码
     * @param message 异常消息
     * @param cause 根本原因异常
     */
    public ValveException(String valveName, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.valveName = valveName;
        this.errorCode = errorCode;
        this.context = new HashMap<>();
    }
    
    // ========== Getter方法 ==========
    
    /**
     * 获取阀门名称
     * 
     * @return 阀门名称
     */
    public String getValveName() {
        return valveName;
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取异常上下文信息
     * 
     * @return 上下文信息的不可修改视图
     */
    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }
    
    // ========== 上下文操作 ==========
    
    /**
     * 添加上下文信息
     * 
     * @param key 键
     * @param value 值
     */
    public void addContext(String key, Object value) {
        context.put(key, value);
    }
    
    /**
     * 移除上下文信息
     * 
     * @param key 键
     * @return 被移除的值，如果不存在则返回null
     */
    public Object removeContext(String key) {
        return context.remove(key);
    }
    
    /**
     * 获取上下文信息
     * 
     * @param key 键
     * @return 值，如果不存在则返回null
     */
    public Object getContext(String key) {
        return context.get(key);
    }
    
    // ========== 重写方法 ==========
    
    /**
     * 获取详细的异常消息
     * 
     * @return 包含阀门名称、错误码和原始消息的字符串
     */
    @Override
    public String getMessage() {
        return String.format("[Valve: %s] [Code: %s] %s", 
            valveName, errorCode, super.getMessage());
    }
    
    /**
     * 转换为字符串表示
     * 
     * @return 异常的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(": ").append(getMessage());
        
        if (!context.isEmpty()) {
            sb.append("\nContext: ");
            context.forEach((key, value) -> 
                sb.append("\n  ").append(key).append(" = ").append(value));
        }
        
        if (getCause() != null) {
            sb.append("\nCaused by: ").append(getCause());
        }
        
        return sb.toString();
    }
}
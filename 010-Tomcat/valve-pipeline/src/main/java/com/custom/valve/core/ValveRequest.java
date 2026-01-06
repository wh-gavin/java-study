package com.custom.valve.core;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 阀门请求包装器
 * 
 * 包装原始的HttpServletRequest，提供更方便的API和额外的功能
 * 如：统一的属性存储、参数获取等
 * 
 * 设计模式：装饰器模式
 */
public class ValveRequest {
    
    private final HttpServletRequest originalRequest;
    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    
    /**
     * 构造函数
     * 
     * @param request 原始的HttpServletRequest
     */
    public ValveRequest(HttpServletRequest request) {
        this.originalRequest = request;
        // 复制请求头信息
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName.toLowerCase(), request.getHeader(headerName));
            }
        }
    }
    
    // ========== 原始请求访问器 ==========
    
    /**
     * 获取原始HttpServletRequest
     * 
     * @return 原始请求对象
     */
    public HttpServletRequest getOriginalRequest() {
        return originalRequest;
    }
    
    /**
     * 获取HTTP请求方法
     * 
     * @return 请求方法，如GET、POST等
     */
    public String getMethod() {
        return originalRequest.getMethod();
    }
    
    /**
     * 获取请求URI
     * 
     * @return 请求的URI路径
     */
    public String getRequestURI() {
        return originalRequest.getRequestURI();
    }
    
    /**
     * 获取查询字符串
     * 
     * @return URL中的查询字符串，可能为null
     */
    public String getQueryString() {
        return originalRequest.getQueryString();
    }
    
    /**
     * 获取客户端IP地址
     * 
     * @return 客户端IP地址
     */
    public String getRemoteAddr() {
        return originalRequest.getRemoteAddr();
    }
    
    /**
     * 获取请求参数
     * 
     * @param name 参数名
     * @return 参数值，如果不存在则返回null
     */
    public String getParameter(String name) {
        return originalRequest.getParameter(name);
    }
    
    // ========== 请求头操作 ==========
    
    /**
     * 获取请求头
     * 
     * @param name 请求头名称（不区分大小写）
     * @return 请求头值，如果不存在则返回null
     */
    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }
    
    /**
     * 获取所有请求头
     * 
     * @return 请求头Map，键已转为小写
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
    
    /**
     * 检查是否存在指定的请求头
     * 
     * @param name 请求头名称
     * @return true表示存在，false表示不存在
     */
    public boolean hasHeader(String name) {
        return headers.containsKey(name.toLowerCase());
    }
    
    // ========== 属性操作 ==========
    
    /**
     * 获取请求属性
     * 
     * @param name 属性名
     * @return 属性值，如果不存在则返回null
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
    /**
     * 设置请求属性
     * 
     * @param name 属性名
     * @param value 属性值
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    /**
     * 移除请求属性
     * 
     * @param name 属性名
     * @return 被移除的属性值，如果不存在则返回null
     */
    public Object removeAttribute(String name) {
        return attributes.remove(name);
    }
    
    /**
     * 获取所有请求属性
     * 
     * @return 属性Map的不可修改视图
     */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
    
    /**
     * 检查是否存在指定的属性
     * 
     * @param name 属性名
     * @return true表示存在，false表示不存在
     */
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }
    
    // ========== 其他便捷方法 ==========
    
    /**
     * 获取请求URL（包含查询字符串）
     * 
     * @return 完整的请求URL
     */
    public String getRequestURL() {
        StringBuilder url = new StringBuilder(originalRequest.getRequestURI());
        String queryString = originalRequest.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }
    
    /**
     * 获取请求上下文路径
     * 
     * @return 上下文路径，如"/app"
     */
    public String getContextPath() {
        return originalRequest.getContextPath();
    }
    
    /**
     * 获取请求的Servlet路径
     * 
     * @return Servlet路径
     */
    public String getServletPath() {
        return originalRequest.getServletPath();
    }
    
    /**
     * 获取请求的路径信息
     * 
     * @return 路径信息，可能为null
     */
    public String getPathInfo() {
        return originalRequest.getPathInfo();
    }
    
    /**
     * 获取内容类型
     * 
     * @return 请求的内容类型，如"application/json"
     */
    public String getContentType() {
        return originalRequest.getContentType();
    }
    
    /**
     * 获取内容长度
     * 
     * @return 请求内容长度，如果未知则返回-1
     */
    public int getContentLength() {
        return originalRequest.getContentLength();
    }
    
    /**
     * 获取字符编码
     * 
     * @return 请求的字符编码，如"UTF-8"
     */
    public String getCharacterEncoding() {
        return originalRequest.getCharacterEncoding();
    }
    
    /**
     * 获取会话ID
     * 
     * @return 请求的会话ID，如果没有会话则返回null
     */
    public String getRequestedSessionId() {
        return originalRequest.getRequestedSessionId();
    }
    
    /**
     * 检查请求是否使用安全连接（HTTPS）
     * 
     * @return true表示使用安全连接，false表示不是
     */
    public boolean isSecure() {
        return originalRequest.isSecure();
    }
}
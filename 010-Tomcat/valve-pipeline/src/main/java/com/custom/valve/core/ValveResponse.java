package com.custom.valve.core;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 阀门响应包装器
 * 
 * 包装原始的HttpServletResponse，提供更方便的API和额外的功能
 * 支持响应头管理、状态码设置、响应内容捕获等
 * 
 * 设计模式：装饰器模式 + 包装器模式
 */
public class ValveResponse {
    
    private final HttpServletResponse originalResponse;
    private final Map<String, List<String>> headers = new HashMap<>();
    private int status = HttpServletResponse.SC_OK;
    private String contentType;
    private String characterEncoding = "UTF-8";
    private boolean committed = false;
    private int bufferSize = 8192; // 默认缓冲区大小（8KB）
    
    /**
     * 构造函数
     * 
     * @param response 原始的HttpServletResponse
     */
    public ValveResponse(HttpServletResponse response) {
        this.originalResponse = response;
    }
    
    // ========== 原始响应访问器 ==========
    
    /**
     * 获取原始HttpServletResponse
     * 
     * @return 原始响应对象
     */
    public HttpServletResponse getOriginalResponse() {
        return originalResponse;
    }
    
    // ========== 状态码操作 ==========
    
    /**
     * 设置HTTP状态码
     * 
     * @param status HTTP状态码，如200、404、500等
     */
    public void setStatus(int status) {
        this.status = status;
        if (!committed) {
            originalResponse.setStatus(status);
        }
    }
    
    /**
     * 获取HTTP状态码
     * 
     * @return 当前设置的状态码
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * 发送错误状态
     * 
     * @param status HTTP状态码
     * @param message 错误消息
     * @throws IOException 当I/O操作失败时抛出
     */
    public void sendError(int status, String message) throws IOException {
        committed = true;
        originalResponse.sendError(status, message);
    }
    
    // ========== 响应头操作 ==========
    
    /**
     * 添加响应头（可添加多个同名的头）
     * 
     * @param name 响应头名称
     * @param value 响应头值
     */
    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>())
               .add(value);
        if (!committed) {
            originalResponse.addHeader(name, value);
        }
    }
    
    /**
     * 设置响应头（会覆盖同名的头）
     * 
     * @param name 响应头名称
     * @param value 响应头值
     */
    public void setHeader(String name, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(name.toLowerCase(), values);
        if (!committed) {
            originalResponse.setHeader(name, value);
        }
    }
    
    /**
     * 获取响应头
     * 
     * @param name 响应头名称（不区分大小写）
     * @return 第一个响应头值，如果不存在则返回null
     */
    public String getHeader(String name) {
        List<String> values = headers.get(name.toLowerCase());
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }
    
    /**
     * 获取所有响应头值
     * 
     * @param name 响应头名称（不区分大小写）
     * @return 响应头值列表，如果不存在则返回空列表
     */
    public List<String> getHeaders(String name) {
        List<String> values = headers.get(name.toLowerCase());
        return values != null ? new ArrayList<>(values) : Collections.emptyList();
    }
    
    /**
     * 获取所有响应头
     * 
     * @return 响应头Map的不可修改视图，键已转为小写
     */
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> copy = new HashMap<>();
        headers.forEach((key, value) -> copy.put(key, new ArrayList<>(value)));
        return Collections.unmodifiableMap(copy);
    }
    
    /**
     * 检查是否存在指定的响应头
     * 
     * @param name 响应头名称
     * @return true表示存在，false表示不存在
     */
    public boolean containsHeader(String name) {
        return headers.containsKey(name.toLowerCase());
    }
    
    // ========== 内容类型和编码操作 ==========
    
    /**
     * 设置内容类型
     * 
     * @param contentType 内容类型，如"application/json"
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
        if (!committed) {
            originalResponse.setContentType(contentType);
        }
    }
    
    /**
     * 获取内容类型
     * 
     * @return 当前设置的内容类型
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * 设置字符编码
     * 
     * @param encoding 字符编码，如"UTF-8"
     */
    public void setCharacterEncoding(String encoding) {
        this.characterEncoding = encoding;
        if (!committed) {
            originalResponse.setCharacterEncoding(encoding);
        }
    }
    
    /**
     * 获取字符编码
     * 
     * @return 当前设置的字符编码
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }
    
    // ========== 输出流操作 ==========
    
    /**
     * 获取PrintWriter用于写入文本响应
     * 
     * @return PrintWriter对象
     * @throws IOException 当I/O操作失败时抛出
     * 
     * 注：调用此方法会将响应标记为已提交
     */
    public PrintWriter getWriter() throws IOException {
        committed = true;
        return originalResponse.getWriter();
    }
    
    /**
     * 获取ServletOutputStream用于写入二进制响应
     * 
     * @return ServletOutputStream对象
     * @throws IOException 当I/O操作失败时抛出
     * 
     * 注：调用此方法会将响应标记为已提交
     */
    public ServletOutputStream getOutputStream() throws IOException {
        committed = true;
        return originalResponse.getOutputStream();
    }
    
    // ========== 缓冲区操作 ==========
    
    /**
     * 刷新缓冲区，将缓冲的内容发送到客户端
     * 
     * @throws IOException 当I/O操作失败时抛出
     * 
     * 注：调用此方法会将响应标记为已提交
     */
    public void flushBuffer() throws IOException {
        if (!committed) {
            originalResponse.flushBuffer();
            committed = true;
        }
    }
    
    /**
     * 重置缓冲区
     * 
     * 注：只能在响应未提交时调用，会清除所有已设置的响应头和状态码
     */
    public void resetBuffer() {
        if (!committed) {
            originalResponse.resetBuffer();
        }
    }
    
    /**
     * 重置响应
     * 
     * 注：只能在响应未提交时调用，会清除所有响应数据
     */
    public void reset() {
        if (!committed) {
            originalResponse.reset();
            headers.clear();
            status = HttpServletResponse.SC_OK;
            contentType = null;
        }
    }
    
    // ========== 缓冲区大小操作 ==========
    
    /**
     * 设置缓冲区大小
     * 
     * @param size 缓冲区大小（字节）
     */
    public void setBufferSize(int size) {
        if (!committed) {
            originalResponse.setBufferSize(size);
            this.bufferSize = size;
        }
    }
    
    /**
     * 获取缓冲区大小
     * 
     * @return 当前缓冲区大小
     */
    public int getBufferSize() {
        return bufferSize;
    }
    
    // ========== 状态检查 ==========
    
    /**
     * 检查响应是否已提交
     * 
     * @return true表示响应已提交，false表示未提交
     */
    public boolean isCommitted() {
        return committed || originalResponse.isCommitted();
    }
    
    /**
     * 设置响应为已提交状态
     */
    public void setCommitted(boolean committed) {
        this.committed = committed;
    }
    
    // ========== 其他便捷方法 ==========
    
    /**
     * 发送重定向响应
     * 
     * @param location 重定向目标URL
     * @throws IOException 当I/O操作失败时抛出
     */
    public void sendRedirect(String location) throws IOException {
        committed = true;
        originalResponse.sendRedirect(location);
    }
    
    /**
     * 设置响应头：内容长度
     * 
     * @param length 内容长度（字节）
     */
    public void setContentLength(int length) {
        setHeader("Content-Length", String.valueOf(length));
    }
    
    /**
     * 设置响应头：缓存控制
     * 
     * @param directive 缓存控制指令，如"no-cache, no-store"
     */
    public void setCacheControl(String directive) {
        setHeader("Cache-Control", directive);
    }
    
    /**
     * 设置响应头：过期时间
     * 
     * @param expires 过期时间字符串
     */
    public void setExpires(String expires) {
        setHeader("Expires", expires);
    }
    
    /**
     * 写入JSON响应
     * 
     * @param json JSON字符串
     * @throws IOException 当I/O操作失败时抛出
     */
    public void writeJson(String json) throws IOException {
        setContentType("application/json;charset=" + characterEncoding);
        getWriter().write(json);
    }
    
    /**
     * 写入纯文本响应
     * 
     * @param text 文本内容
     * @throws IOException 当I/O操作失败时抛出
     */
    public void writeText(String text) throws IOException {
        setContentType("text/plain;charset=" + characterEncoding);
        getWriter().write(text);
    }
    
    /**
     * 写入HTML响应
     * 
     * @param html HTML内容
     * @throws IOException 当I/O操作失败时抛出
     */
    public void writeHtml(String html) throws IOException {
        setContentType("text/html;charset=" + characterEncoding);
        getWriter().write(html);
    }
}
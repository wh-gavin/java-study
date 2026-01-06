package com.custom.valve.valves;

import com.custom.valve.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志阀门
 * 
 * 记录请求和响应的详细信息，支持慢请求检测
 * 可以配置是否记录请求头、响应头等详细信息
 * 
 * 使用场景：监控API调用、分析请求性能、排查问题
 */
public class LoggingValve extends AbstractValve {
    
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(LoggingValve.class);
    
    // 配置属性
    private boolean logRequest = true;           // 是否记录请求
    private boolean logResponse = true;          // 是否记录响应
    private boolean logHeaders = false;          // 是否记录请求头
    private boolean logParameters = false;       // 是否记录请求参数
    private int slowThreshold = 1000;            // 慢请求阈值（毫秒）
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * 构造函数
     */
    public LoggingValve() {
        super("loggingValve");
        setOrder(100); // 较早执行，以便记录完整的请求信息
    }
    
    @Override
    protected void doInvoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException {
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        String startTimeStr = LocalDateTime.now().format(timeFormatter);
        
        // 记录请求信息
        if (logRequest) {
            logRequest(request, startTimeStr);
        }
        
        // 执行下一个阀门
        chain.invokeNext(request, response);
        
        // 记录响应信息
        if (logResponse) {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(request, response, duration, startTimeStr);
            
            // 记录慢请求
            if (duration > slowThreshold) {
                logger.warn("慢请求检测 - URI: {}, 耗时: {}ms, 客户端IP: {}", 
                    request.getRequestURI(), duration, request.getRemoteAddr());
                
                // 设置慢请求标记到请求属性
                request.setAttribute("slowRequest", true);
                request.setAttribute("requestDuration", duration);
            }
        }
    }
    
    /**
     * 记录请求信息
     */
    private void logRequest(ValveRequest request, String timestamp) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append("请求开始 [").append(timestamp).append("] ");
        logMsg.append(request.getMethod()).append(" ").append(request.getRequestURL());
        logMsg.append(" - 客户端: ").append(request.getRemoteAddr());
        
        if (logHeaders) {
            logMsg.append(" - 请求头: ").append(request.getHeaders());
        }
        
        if (logParameters && request.getOriginalRequest().getParameterMap() != null) {
            logMsg.append(" - 参数: ").append(request.getOriginalRequest().getParameterMap());
        }
        
        logger.info(logMsg.toString());
    }
    
    /**
     * 记录响应信息
     */
    private void logResponse(ValveRequest request, ValveResponse response, long duration, String startTime) {
        String endTime = LocalDateTime.now().format(timeFormatter);
        
        StringBuilder logMsg = new StringBuilder();
        logMsg.append("请求结束 [开始: ").append(startTime).append(", 结束: ").append(endTime).append("] ");
        logMsg.append(request.getMethod()).append(" ").append(request.getRequestURL());
        logMsg.append(" - 状态: ").append(response.getStatus());
        logMsg.append(" - 耗时: ").append(duration).append("ms");
        logMsg.append(" - 客户端: ").append(request.getRemoteAddr());
        
        logger.info(logMsg.toString());
    }
    
    @Override
    protected void beforeInvoke(ValveRequest request, ValveResponse response) {
        // 设置请求开始时间
        request.setAttribute("requestStartTime", System.currentTimeMillis());
    }
    
    @Override
    protected void afterInvoke(ValveRequest request, ValveResponse response) {
        // 记录一些额外的统计信息
        Long startTime = (Long) request.getAttribute("requestStartTime");
        if (startTime != null) {
            long totalDuration = System.currentTimeMillis() - startTime;
            request.setAttribute("totalProcessingTime", totalDuration);
        }
    }
    
    // ========== 配置方法 ==========
    
    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }
    
    public void setLogResponse(boolean logResponse) {
        this.logResponse = logResponse;
    }
    
    public void setLogHeaders(boolean logHeaders) {
        this.logHeaders = logHeaders;
    }
    
    public void setLogParameters(boolean logParameters) {
        this.logParameters = logParameters;
    }
    
    public void setSlowThreshold(int slowThreshold) {
        if (slowThreshold < 0) {
            throw new IllegalArgumentException("Slow threshold must be non-negative");
        }
        this.slowThreshold = slowThreshold;
    }
    
    public void setTimeFormatter(String pattern) {
        this.timeFormatter = DateTimeFormatter.ofPattern(pattern);
    }
    
    // ========== Getter方法 ==========
    
    public boolean isLogRequest() { return logRequest; }
    public boolean isLogResponse() { return logResponse; }
    public boolean isLogHeaders() { return logHeaders; }
    public boolean isLogParameters() { return logParameters; }
    public int getSlowThreshold() { return slowThreshold; }
}
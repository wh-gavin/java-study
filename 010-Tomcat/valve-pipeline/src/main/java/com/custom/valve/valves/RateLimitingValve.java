package com.custom.valve.valves;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;

import com.custom.valve.core.AbstractValve;
import com.custom.valve.core.ValveChain;
import com.custom.valve.core.ValveRequest;
import com.custom.valve.core.ValveResponse;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 限流阀门
 * 
 * 限制请求速率，防止系统被过多请求压垮
 * 支持全局限流和客户端级别限流
 * 支持白名单和排除路径
 * 
 * 使用场景：API限流、防止DDoS攻击、保护后端服务
 */
public class RateLimitingValve extends AbstractValve {
    
    // 限流器
    private RateLimiter globalRateLimiter;      // 全局限流器
    private final ConcurrentHashMap<String, RateLimiter> clientRateLimiters = new ConcurrentHashMap<>();
    
    // 配置属性
    private double globalRate = 100.0;           // 全局每秒请求数
    private double clientRate = 10.0;            // 每个客户端每秒请求数
    private int clientBurstSize = 20;            // 每个客户端突发请求数
    private long blockDuration = 60;             // 被阻止后的重试时间（秒）
    
    private boolean enabled = true;              // 是否启用限流
    private Set<String> excludePaths = new HashSet<>(); // 排除路径
    private Set<String> whiteList = new HashSet<>();    // 白名单（不限流）
    
    /**
     * 构造函数
     */
    public RateLimitingValve() {
        super("rateLimitingValve");
        setOrder(150); // 在认证阀门之前执行
        
        // 初始化全局限流器
        this.globalRateLimiter = RateLimiter.create(globalRate);
        
        // 默认排除路径
        excludePaths.add("/health");
        excludePaths.add("/favicon.ico");
    }
    
    @Override
    protected void doInvoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException {
        
        // 检查是否启用
        if (!enabled) {
            chain.invokeNext(request, response);
            return;
        }
        
        String path = request.getRequestURI();
        
        // 检查是否在排除路径中
        if (isExcludedPath(path)) {
            chain.invokeNext(request, response);
            return;
        }
        
        String clientId = getClientId(request);
        
        // 检查白名单
        if (whiteList.contains(clientId)) {
            chain.invokeNext(request, response);
            return;
        }
        
        // 1. 全局限流
        if (!globalRateLimiter.tryAcquire()) {
            handleRateLimitExceeded(request, response, "全局请求速率超过限制");
            return;
        }
        
        // 2. 客户端限流
        RateLimiter clientLimiter = clientRateLimiters.computeIfAbsent(clientId,
            id -> RateLimiter.create(clientRate));
        
        if (!clientLimiter.tryAcquire()) {
            handleRateLimitExceeded(request, response, 
                String.format("客户端请求速率超过限制: %s", clientId));
            return;
        }
        
        // 3. 统计请求次数（可选）
        incrementRequestCount(clientId);
        
        // 设置限流信息到请求属性
        request.setAttribute("rateLimiting.clientId", clientId);
        request.setAttribute("rateLimiting.globalRate", globalRate);
        request.setAttribute("rateLimiting.clientRate", clientRate);
        
        // 继续执行下一个阀门
        chain.invokeNext(request, response);
    }
    
    /**
     * 获取客户端标识符
     */
    private String getClientId(ValveRequest request) {
        // 优先使用认证用户ID
        Object user = request.getAttribute("currentUser");
        if (user instanceof AuthenticationValve.UserPrincipal) {
            return ((AuthenticationValve.UserPrincipal) user).getUserId();
        }
        
        // 使用IP地址作为客户端标识
        String ip = request.getRemoteAddr();
        
        // 处理X-Forwarded-For头（如果有代理）
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // 取第一个IP（客户端原始IP）
            String[] ips = xForwardedFor.split(",");
            ip = ips[0].trim();
        }
        
        return ip;
    }
    
    /**
     * 检查是否为排除路径
     */
    private boolean isExcludedPath(String path) {
        return excludePaths.stream().anyMatch(path::startsWith);
    }
    
    /**
     * 统计请求次数（用于监控和分析）
     */
    private void incrementRequestCount(String clientId) {
        // 这里可以实现请求统计逻辑
        // 例如：记录到数据库、发送到监控系统等
        // 简化实现：只记录日志
        if (logger.isDebugEnabled()) {
            logger.debug("Request from client: {}", clientId);
        }
    }
    
    /**
     * 处理限流超限
     */
    private void handleRateLimitExceeded(ValveRequest request, ValveResponse response, String reason)
            throws IOException {
        
        response.setStatus(429); // Too Many Requests
        response.setContentType("application/json");
        response.addHeader("Retry-After", String.valueOf(blockDuration));
        
        // 添加限流相关的响应头
        response.addHeader("X-RateLimit-Limit-Global", String.valueOf(globalRate));
        response.addHeader("X-RateLimit-Limit-Client", String.valueOf(clientRate));
        response.addHeader("X-RateLimit-Retry-After", String.valueOf(blockDuration));
        
        // 构建错误响应
        String json = String.format(
            "{\"error\":\"TooManyRequests\",\"message\":\"%s\",\"retryAfter\":%d,\"timestamp\":%d}",
            reason, blockDuration, System.currentTimeMillis());
        
        response.writeJson(json);
        
        // 记录限流日志
        logger.warn("请求被限流: {} {} - 客户端: {} - 原因: {}", 
            request.getMethod(), request.getRequestURI(), getClientId(request), reason);
    }
    
    @Override
    protected void handleException(ValveRequest request, ValveResponse response, Exception e) {
        // 限流阀门异常处理
        logger.error("Rate limiting valve error", e);
        super.handleException(request, response, e);
    }
    
    // ========== 配置方法 ==========
    
    public void setGlobalRate(double globalRate) {
        this.globalRate = globalRate;
        this.globalRateLimiter = RateLimiter.create(globalRate);
    }
    
    public void setClientRate(double clientRate) {
        this.clientRate = clientRate;
        // 更新现有限流器的速率
        clientRateLimiters.replaceAll((clientId, limiter) -> {
            limiter.setRate(clientRate);
            return limiter;
        });
    }
    
    public void setClientBurstSize(int clientBurstSize) {
        this.clientBurstSize = clientBurstSize;
    }
    
    public void setBlockDuration(long blockDuration) {
        this.blockDuration = blockDuration;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setExcludePaths(Set<String> excludePaths) {
        this.excludePaths = new HashSet<>(excludePaths);
    }
    
    public void addExcludePath(String path) {
        this.excludePaths.add(path);
    }
    
    public void setWhiteList(Set<String> whiteList) {
        this.whiteList = new HashSet<>(whiteList);
    }
    
    public void addToWhiteList(String clientId) {
        this.whiteList.add(clientId);
    }
    
    // ========== 管理方法 ==========
    
    /**
     * 获取客户端请求统计信息
     */
    public Map<String, Double> getClientStatistics() {
        Map<String, Double> stats = new HashMap<>();
        clientRateLimiters.forEach((clientId, limiter) -> {
            stats.put(clientId, limiter.getRate());
        });
        return stats;
    }
    
    /**
     * 重置所有客户端限流器
     */
    public void resetClientLimiters() {
        clientRateLimiters.clear();
        logger.info("All client rate limiters have been reset");
    }
    
    /**
     * 获取活跃客户端数量
     */
    public int getActiveClientCount() {
        return clientRateLimiters.size();
    }
    
    /**
     * 检查客户端是否被限流
     */
    public boolean isClientLimited(String clientId) {
        RateLimiter limiter = clientRateLimiters.get(clientId);
        return limiter != null && !limiter.tryAcquire();
    }
}
package com.custom.valve.valves;

import com.custom.valve.core.*;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

/**
 * 认证阀门
 * 
 * 处理用户认证，支持Token认证和Session认证
 * 可以配置排除路径（如登录页面、静态资源等）
 * 支持角色和权限检查
 * 
 * 使用场景：API接口认证、Web应用登录检查、权限控制
 */
public class AuthenticationValve extends AbstractValve {
    
    // 配置属性
    private Set<String> excludePaths = new HashSet<>();          // 不需要认证的路径
    private String tokenHeader = "Authorization";                // Token请求头名称
    private String tokenPrefix = "Bearer ";                      // Token前缀
    private String sessionCookie = "SESSIONID";                  // Session Cookie名称
    private boolean requireHttps = false;                        // 是否要求HTTPS
    private Map<String, Set<String>> rolePermissions = new HashMap<>(); // 角色权限映射
    
    /**
     * 构造函数
     */
    public AuthenticationValve() {
        super("authenticationValve");
        setOrder(200); // 在日志阀门之后执行
        
        // 默认排除路径
        excludePaths.add("/login");
        excludePaths.add("/public/");
        excludePaths.add("/static/");
        excludePaths.add("/health");
        excludePaths.add("/favicon.ico");
    }
    
    @Override
    protected void doInvoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException {
        
        // 获取请求路径
        String path = request.getRequestURI();
        
        // 检查是否在排除路径中
        if (isExcludedPath(path)) {
            chain.invokeNext(request, response);
            return;
        }
        
        // 检查HTTPS要求
        if (requireHttps && !request.isSecure()) {
            sendError(response, 403, "HTTPS required");
            return;
        }
        
        // 认证用户
        UserPrincipal user = authenticate(request);
        
        if (user == null) {
            // 认证失败
            sendUnauthorized(response, "Authentication required");
            return;
        }
        
        // 检查权限
        if (!hasPermission(user, request)) {
            sendForbidden(response, "Insufficient permissions");
            return;
        }
        
        // 设置用户信息到请求属性，供后续阀门使用
        request.setAttribute("currentUser", user);
        request.setAttribute("userId", user.getUserId());
        request.setAttribute("userRoles", user.getRoles());
        
        // 继续执行下一个阀门
        chain.invokeNext(request, response);
    }
    
    /**
     * 认证用户
     */
    private UserPrincipal authenticate(ValveRequest request) {
        // 1. 尝试从Header获取Token
        String token = extractTokenFromHeader(request);
        if (token != null) {
            UserPrincipal user = validateToken(token);
            if (user != null) {
                logger.info("User authenticated via token: {}", user.getUserId());
                return user;
            }
        }
        
        // 2. 尝试从Cookie获取Session
        String sessionId = extractSessionFromCookie(request);
        if (sessionId != null) {
            UserPrincipal user = validateSession(sessionId);
            if (user != null) {
                logger.info("User authenticated via session: {}", user.getUserId());
                return user;
            }
        }
        
        // 3. 尝试从请求参数获取（用于调试）
        String debugToken = request.getParameter("debug_token");
        if (debugToken != null && "development".equals(System.getProperty("env", "production"))) {
            // 开发环境中允许通过参数认证
            return new UserPrincipal("debug-user-" + debugToken.hashCode(), "Debug User");
        }
        
        return null;
    }
    
    /**
     * 从Header提取Token
     */
    private String extractTokenFromHeader(ValveRequest request) {
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            return authHeader.substring(tokenPrefix.length()).trim();
        }
        return null;
    }
    
    /**
     * 从Cookie提取Session
     */
    private String extractSessionFromCookie(ValveRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                cookie = cookie.trim();
                if (cookie.startsWith(sessionCookie + "=")) {
                    return cookie.substring(sessionCookie.length() + 1);
                }
            }
        }
        return null;
    }
    
    /**
     * 验证Token（简化实现，实际项目中应该使用JWT或其他验证机制）
     */
    private UserPrincipal validateToken(String token) {
        try {
            // 这里应该实现真正的Token验证逻辑
            // 例如：解析JWT、检查签名、验证有效期等
            
            // 简化实现：假设Token是有效的，解析用户ID
            // 实际项目中应该替换为真实的Token验证逻辑
            if (token != null && !token.trim().isEmpty() && token.length() > 10) {
                String userId = "user-" + token.hashCode();
                UserPrincipal user = new UserPrincipal(userId, "Token User");
                user.addRole("USER");
                
                // 从Token中提取额外信息（如果有）
                if (token.contains("admin")) {
                    user.addRole("ADMIN");
                }
                
                return user;
            }
        } catch (Exception e) {
            logger.error("Token validation error", e);
        }
        return null;
    }
    
    /**
     * 验证Session（简化实现）
     */
    private UserPrincipal validateSession(String sessionId) {
        try {
            // 这里应该实现真正的Session验证逻辑
            // 例如：从Session存储中获取用户信息、检查Session是否过期等
            
            // 简化实现：假设Session是有效的
            if (sessionId != null && !sessionId.trim().isEmpty()) {
                String userId = "session-user-" + sessionId.hashCode();
                UserPrincipal user = new UserPrincipal(userId, "Session User");
                user.addRole("USER");
                return user;
            }
        } catch (Exception e) {
            logger.error("Session validation error", e);
        }
        return null;
    }
    
    /**
     * 检查权限
     */
    private boolean hasPermission(UserPrincipal user, ValveRequest request) {
        // 获取请求路径和方法
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // 构建权限标识符
        String permission = method + ":" + path;
        
        // 检查用户角色是否有权限
        for (String role : user.getRoles()) {
            Set<String> permissions = rolePermissions.get(role);
            if (permissions != null && (permissions.contains("*") || permissions.contains(permission))) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否为排除路径
     */
    private boolean isExcludedPath(String path) {
        for (String excludePath : excludePaths) {
            if (path.startsWith(excludePath)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 发送未认证响应
     */
    private void sendUnauthorized(ValveResponse response, String message) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.addHeader("WWW-Authenticate", "Bearer realm=\"API\"");
        response.writeJson(String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message));
    }
    
    /**
     * 发送禁止访问响应
     */
    private void sendForbidden(ValveResponse response, String message) throws IOException {
        response.setStatus(403);
        response.setContentType("application/json");
        response.writeJson(String.format("{\"error\":\"Forbidden\",\"message\":\"%s\"}", message));
    }
    
    /**
     * 发送错误响应
     */
    private void sendError(ValveResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.writeJson(String.format("{\"error\":\"Access Denied\",\"message\":\"%s\"}", message));
    }
    
    // ========== 用户主体类 ==========
    
    /**
     * 用户主体类，表示认证的用户
     */
    public static class UserPrincipal {
        private final String userId;
        private final String username;
        private final Set<String> roles = new HashSet<>();
        private final Map<String, Object> attributes = new HashMap<>();
        
        public UserPrincipal(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }
        
        // Getter方法
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public Set<String> getRoles() { return new HashSet<>(roles); }
        public Map<String, Object> getAttributes() { return new HashMap<>(attributes); }
        
        // 角色管理
        public void addRole(String role) { roles.add(role); }
        public void removeRole(String role) { roles.remove(role); }
        public boolean hasRole(String role) { return roles.contains(role); }
        
        // 属性管理
        public void setAttribute(String name, Object value) { attributes.put(name, value); }
        public Object getAttribute(String name) { return attributes.get(name); }
        public void removeAttribute(String name) { attributes.remove(name); }
        
        @Override
        public String toString() {
            return String.format("UserPrincipal{userId='%s', username='%s', roles=%s}", 
                userId, username, roles);
        }
    }
    
    // ========== 配置方法 ==========
    
    public void setExcludePaths(Set<String> excludePaths) {
        this.excludePaths = new HashSet<>(excludePaths);
    }
    
    public void addExcludePath(String path) {
        this.excludePaths.add(path);
    }
    
    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }
    
    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
    
    public void setSessionCookie(String sessionCookie) {
        this.sessionCookie = sessionCookie;
    }
    
    public void setRequireHttps(boolean requireHttps) {
        this.requireHttps = requireHttps;
    }
    
    public void addRolePermission(String role, String permission) {
        rolePermissions.computeIfAbsent(role, k -> new HashSet<>()).add(permission);
    }
    
    public void removeRolePermission(String role, String permission) {
        Set<String> permissions = rolePermissions.get(role);
        if (permissions != null) {
            permissions.remove(permission);
        }
    }
}
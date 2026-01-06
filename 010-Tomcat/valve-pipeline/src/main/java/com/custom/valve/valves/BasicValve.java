package com.custom.valve.valves;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.custom.valve.core.AbstractValve;
import com.custom.valve.core.ValveChain;
import com.custom.valve.core.ValveRequest;
import com.custom.valve.core.ValveResponse;

/**
 * 基础阀门
 * 
 * 管道中的最后一个阀门，处理实际业务逻辑
 * 可以作为实际业务处理的入口点，或者调用Servlet/Controller
 * 
 * 使用场景：业务逻辑处理、API路由、静态资源服务
 */
public class BasicValve extends AbstractValve {
    
    /**
     * 构造函数
     */
    public BasicValve() {
        super("basicValve");
        setOrder(Integer.MAX_VALUE); // 最后执行
    }
    
    @Override
    protected void doInvoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException {
        
        // 基础阀门是管道中的最后一个阀门，不需要调用chain.invokeNext()
        // 直接处理请求
        
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        // 简单的路由处理
        if ("GET".equals(method) && "/health".equals(uri)) {
            handleHealthCheck(request, response);
        } else if ("GET".equals(method) && "/api/hello".equals(uri)) {
            handleHello(request, response);
        } else if ("GET".equals(method) && "/api/user".equals(uri)) {
            handleUserInfo(request, response);
        } else if ("GET".equals(method) && "/api/status".equals(uri)) {
            handleApiStatus(request, response);
        } else if ("POST".equals(method) && "/api/echo".equals(uri)) {
            handleEcho(request, response);
        } else {
            handleNotFound(request, response);
        }
    }
    
    /**
     * 处理健康检查请求
     */
    private void handleHealthCheck(ValveRequest request, ValveResponse response) throws IOException {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", System.currentTimeMillis());
        healthInfo.put("service", "valve-pipeline");
        healthInfo.put("version", "1.0.0");
        
        // 添加系统信息
        healthInfo.put("system", Map.of(
            "javaVersion", System.getProperty("java.version"),
            "os", System.getProperty("os.name"),
            "arch", System.getProperty("os.arch")
        ));
        
        // 添加阀门执行时间信息
        Map<String, Object> valveTimes = new HashMap<>();
        request.getAttributes().forEach((key, value) -> {
            if (key.endsWith(".duration") && value instanceof Long) {
                String valveName = key.substring(0, key.length() - 9);
                valveTimes.put(valveName, value);
            }
        });
        healthInfo.put("valveTimes", valveTimes);
        
        response.writeJson(toJson(healthInfo));
    }
    
    /**
     * 处理Hello请求
     */
    private void handleHello(ValveRequest request, ValveResponse response) throws IOException {
        String name = request.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            name = "World";
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello, " + name + "!");
        result.put("timestamp", System.currentTimeMillis());
        result.put("method", request.getMethod());
        result.put("path", request.getRequestURI());
        
        // 添加客户端信息
        result.put("client", Map.of(
            "ip", request.getRemoteAddr(),
            "userAgent", request.getHeader("User-Agent")
        ));
        
        response.writeJson(toJson(result));
    }
    
    /**
     * 处理用户信息请求
     */
    private void handleUserInfo(ValveRequest request, ValveResponse response) throws IOException {
        // 从请求属性获取用户信息（由认证阀门设置）
        Object user = request.getAttribute("currentUser");
        
        Map<String, Object> result = new HashMap<>();
        
        if (user != null) {
            if (user instanceof AuthenticationValve.UserPrincipal) {
                AuthenticationValve.UserPrincipal userPrincipal = (AuthenticationValve.UserPrincipal) user;
                
                result.put("authenticated", true);
                result.put("userId", userPrincipal.getUserId());
                result.put("username", userPrincipal.getUsername());
                result.put("roles", userPrincipal.getRoles());
                result.put("attributes", userPrincipal.getAttributes());
            } else {
                result.put("authenticated", true);
                result.put("user", user.toString());
            }
        } else {
            result.put("authenticated", false);
            result.put("message", "No user information available");
        }
        
        result.put("timestamp", System.currentTimeMillis());
        
        response.writeJson(toJson(result));
    }
    
    /**
     * 处理API状态请求
     */
    private void handleApiStatus(ValveRequest request, ValveResponse response) throws IOException {
        Map<String, Object> status = new HashMap<>();
        
        // 请求信息
        status.put("request", Map.of(
            "method", request.getMethod(),
            "uri", request.getRequestURI(),
            "query", request.getQueryString(),
            "client", request.getRemoteAddr()
        ));
        
        // 阀门执行信息
        Map<String, Object> valveInfo = new HashMap<>();
        request.getAttributes().forEach((key, value) -> {
            if (key.endsWith(".duration")) {
                String valveName = key.substring(0, key.length() - 9);
                valveInfo.put(valveName + ".duration", value);
            } else if (key.equals("cache.hit")) {
                valveInfo.put("cache.hit", value);
            } else if (key.equals("slowRequest")) {
                valveInfo.put("slowRequest", value);
            }
        });
        status.put("valves", valveInfo);
        
        // 系统状态
        Runtime runtime = Runtime.getRuntime();
        status.put("system", Map.of(
            "memory", Map.of(
                "free", runtime.freeMemory(),
                "total", runtime.totalMemory(),
                "max", runtime.maxMemory(),
                "used", runtime.totalMemory() - runtime.freeMemory()
            ),
            "processors", runtime.availableProcessors(),
            "timestamp", System.currentTimeMillis()
        ));
        
        response.writeJson(toJson(status));
    }
    
    /**
     * 处理Echo请求
     */
    private void handleEcho(ValveRequest request, ValveResponse response) throws IOException {
        Map<String, Object> echo = new HashMap<>();
        
        // 请求信息
        echo.put("method", request.getMethod());
        echo.put("uri", request.getRequestURI());
        echo.put("query", request.getQueryString());
        echo.put("client", request.getRemoteAddr());
        
        // 请求头
        Map<String, String> headers = new HashMap<>();
        request.getHeaders().forEach(headers::put);
        echo.put("headers", headers);
        
        // 请求参数
        Map<String, String[]> params = request.getOriginalRequest().getParameterMap();
        if (params != null && !params.isEmpty()) {
            Map<String, Object> parameters = new HashMap<>();
            params.forEach((key, values) -> {
                if (values.length == 1) {
                    parameters.put(key, values[0]);
                } else {
                    parameters.put(key, values);
                }
            });
            echo.put("parameters", parameters);
        }
        
        echo.put("timestamp", System.currentTimeMillis());
        
        response.writeJson(toJson(echo));
    }
    
    /**
     * 处理未找到的请求
     */
    private void handleNotFound(ValveRequest request, ValveResponse response) throws IOException {
        response.setStatus(404);
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", "The requested resource was not found");
        error.put("path", request.getRequestURI());
        error.put("method", request.getMethod());
        error.put("timestamp", System.currentTimeMillis());
        
        response.writeJson(toJson(error));
    }
    
    /**
     * 对象转换为JSON字符串（简化实现）
     */
    private String toJson(Map<String, Object> map) {
        // 简化实现，实际项目中应该使用Jackson或Gson
        StringBuilder json = new StringBuilder("{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                String nestedJson = toJson((Map<String, Object>) value);
                json.append(nestedJson);
            } else if (value instanceof Iterable) {
                json.append("[");
                boolean firstItem = true;
                for (Object item : (Iterable<?>) value) {
                    if (!firstItem) {
                        json.append(",");
                    }
                    firstItem = false;
                    
                    if (item instanceof String) {
                        json.append("\"").append(escapeJson((String) item)).append("\"");
                    } else if (item instanceof Number || item instanceof Boolean) {
                        json.append(item);
                    } else {
                        json.append("\"").append(escapeJson(item.toString())).append("\"");
                    }
                }
                json.append("]");
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * 转义JSON字符串中的特殊字符
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '/': sb.append("\\/"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
    
    @Override
    protected void beforeInvoke(ValveRequest request, ValveResponse response) {
        // 记录基础阀门开始执行
        logger.debug("Basic valve processing: {} {}", 
            request.getMethod(), request.getRequestURI());
    }
    
    @Override
    protected void afterInvoke(ValveRequest request, ValveResponse response) {
        // 记录基础阀门执行完成
        Long startTime = (Long) request.getAttribute("requestStartTime");
        if (startTime != null) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.debug("Basic valve completed in {}ms", totalTime);
        }
    }
}
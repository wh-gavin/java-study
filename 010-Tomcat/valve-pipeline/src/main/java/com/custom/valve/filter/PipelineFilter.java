package com.custom.valve.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.valve.core.Pipeline;
import com.custom.valve.core.ValveException;
import com.custom.valve.core.ValveRequest;
import com.custom.valve.core.ValveResponse;
import com.custom.valve.factory.PipelineFactory;

/**
 * 管道过滤器
 * 
 * 将Valve Pipeline集成到Servlet容器中
 * 拦截HTTP请求，选择合适的管道进行处理
 * 
 * 使用场景：Servlet容器集成、Web应用入口点、请求路由
 */
public class PipelineFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(PipelineFilter.class);
    
    private PipelineFactory pipelineFactory;
    private boolean initialized = false;
    
    /**
     * 过滤器初始化
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("PipelineFilter initializing...");
        
        try {
            // 获取管道工厂实例
            pipelineFactory = PipelineFactory.getInstance();
            
            // 初始化默认管道
            pipelineFactory.initDefaultPipelines();
            
            // 将工厂实例存储到ServletContext，供其他组件使用
            ServletContext context = filterConfig.getServletContext();
            context.setAttribute("valve.pipelineFactory", pipelineFactory);
            
            initialized = true;
            logger.info("PipelineFilter initialized successfully");
            
        } catch (ValveException e) {
            logger.error("Failed to initialize PipelineFilter", e);
            throw new ServletException("Failed to initialize PipelineFilter", e);
        }
    }
    
    /**
     * 过滤器执行
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain filterChain) throws IOException, ServletException {
        
        // 检查是否已初始化
        if (!initialized) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 转换为HTTP请求/响应
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 创建Valve请求/响应包装器
        ValveRequest valveRequest = new ValveRequest(httpRequest);
        ValveResponse valveResponse = new ValveResponse(httpResponse);
        
        try {
            // 根据请求选择管道
            Pipeline pipeline = selectPipeline(httpRequest);
            
            // 记录请求开始时间
            long startTime = System.currentTimeMillis();
            valveRequest.setAttribute("requestStartTime", startTime);
            
            // 执行管道
            pipeline.invoke(valveRequest, valveResponse);
            
            // 记录请求处理时间
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 1000) {
                logger.warn("Slow request processing: {} {} took {}ms", 
                    httpRequest.getMethod(), httpRequest.getRequestURI(), duration);
            }
            
            // 如果管道没有处理完请求，继续执行默认过滤器链
            if (!valveResponse.isCommitted() && !httpResponse.isCommitted()) {
                filterChain.doFilter(request, response);
            }
            
//        } catch (ValveException e) {
//            // 处理阀门异常
//            handleValveException(e, valveResponse);
        } catch (Exception e) {
            // 处理其他异常
            handleException(e, valveResponse);
        }
    }
    
    /**
     * 根据请求选择管道
     */
    private Pipeline selectPipeline(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // 1. 管理接口（需要特殊权限）
        if (path.startsWith("/admin/") || path.startsWith("/manage/")) {
            return pipelineFactory.getPipeline("admin");
        }
        
        // 2. 静态资源
        if (path.startsWith("/static/") || 
            path.startsWith("/public/") ||
            path.startsWith("/images/") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.endsWith(".css") ||
            path.endsWith(".js") ||
            path.endsWith(".png") ||
            path.endsWith(".jpg") ||
            path.endsWith(".gif") ||
            path.endsWith(".ico")) {
            return pipelineFactory.getPipeline("static");
        }
        
        // 3. 公共API（不需要认证）
        if (path.equals("/health") ||
            path.equals("/favicon.ico") ||
            path.startsWith("/public-api/")) {
            return pipelineFactory.getPipeline("public");
        }
        
        // 4. 默认使用API管道（需要认证）
        return pipelineFactory.getPipeline("api");
    }
    
    /**
     * 处理阀门异常
     */
    private void handleValveException(ValveException e, ValveResponse response) throws IOException {
        logger.error("Valve pipeline error", e);
        
        // 构建错误响应
        response.setStatus(500);
        response.setContentType("application/json");
        
        // 添加错误信息
        String errorJson = String.format(
            "{\"error\":\"Pipeline Error\",\"valve\":\"%s\",\"code\":\"%s\",\"message\":\"%s\",\"timestamp\":%d}",
            e.getValveName(), e.getErrorCode(), e.getMessage(), System.currentTimeMillis()
        );
        
        response.writeJson(errorJson);
    }
    
    /**
     * 处理其他异常
     */
    private void handleException(Exception e, ValveResponse response) throws IOException {
        logger.error("Unexpected error in pipeline", e);
        
        response.setStatus(500);
        response.setContentType("application/json");
        response.writeJson(String.format(
            "{\"error\":\"Internal Server Error\",\"message\":\"%s\",\"timestamp\":%d}",
            "An unexpected error occurred", System.currentTimeMillis()
        ));
    }
    
    /**
     * 过滤器销毁
     */
    @Override
    public void destroy() {
        if (pipelineFactory != null) {
            pipelineFactory.destroyAll();
        }
        initialized = false;
        logger.info("PipelineFilter destroyed");
    }
    
    // ========== 状态查询 ==========
    
    /**
     * 检查是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }
}
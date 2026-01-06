package com.custom.valve.example;


import com.custom.valve.filter.PipelineFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Valve Pipeline示例应用
 * 
 * 演示如何使用Valve Pipeline处理HTTP请求
 * 使用Jetty作为嵌入式Servlet容器
 * 
 * 启动后访问：
 *   http://localhost:8080/health            - 健康检查（公共）
 *   http://localhost:8080/api/hello         - Hello接口（需要认证）
 *   http://localhost:8080/api/user          - 用户信息（需要认证）
 *   http://localhost:8080/api/status        - API状态（需要认证）
 *   http://localhost:8080/api/echo          - Echo接口（POST）
 */
public class Application {
    
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        try {
            logger.info("Starting Valve Pipeline example application...");
            
            // 创建Jetty服务器
            Server server = new Server(PORT);
            
            // 创建Servlet上下文处理器
            ServletContextHandler context = new ServletContextHandler();
            context.setContextPath("/");
            
            // 添加PipelineFilter
            FilterHolder filterHolder = new FilterHolder(PipelineFilter.class);
            context.addFilter(filterHolder, "/*", 
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
            
            // 添加一个演示Servlet（可选）
            context.addServlet(DemoServlet.class, "/demo");
            
            // 设置处理器
            server.setHandler(context);
            
            // 启动服务器
            server.start();
            
            logger.info("Server started on port {}", PORT);
            logger.info("Health check: http://localhost:{}/health", PORT);
            logger.info("Hello API: http://localhost:{}/api/hello?name=World", PORT);
            logger.info("Press Ctrl+C to stop...");
            
            // 等待服务器停止
            server.join();
            
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            System.exit(1);
        }
    }
    
    /**
     * 演示Servlet（用于对比）
     */
    public static class DemoServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"message\":\"This is from Demo Servlet\"}");
        }
    }
}
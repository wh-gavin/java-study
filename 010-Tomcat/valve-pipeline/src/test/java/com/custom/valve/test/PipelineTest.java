package com.custom.valve.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.custom.valve.core.AbstractValve;
import com.custom.valve.core.StandardPipeline;
import com.custom.valve.core.Valve;
import com.custom.valve.core.ValveChain;
import com.custom.valve.core.ValveRequest;
import com.custom.valve.core.ValveResponse;
import com.custom.valve.valves.AuthenticationValve;
import com.custom.valve.valves.BasicValve;
import com.custom.valve.valves.CacheValve;
import com.custom.valve.valves.LoggingValve;
import com.custom.valve.valves.RateLimitingValve;

/**
 * Valve Pipeline 单元测试
 */
public class PipelineTest {
    
    @BeforeEach
    public void setUp() {
        // 测试前的准备工作
    }
    
    @AfterEach
    public void tearDown() {
        // 测试后的清理工作
    }
    
    @Test
    public void testLoggingValve() throws Exception {
        // 创建日志阀门
        LoggingValve valve = new LoggingValve();
        valve.setName("testLoggingValve");
        
        // 模拟请求和响应
        ValveRequest request = mock(ValveRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        ValveResponse response = mock(ValveResponse.class);
        when(response.getStatus()).thenReturn(200);
        
        ValveChain chain = mock(ValveChain.class);
        
        // 执行阀门
        valve.invoke(request, response, chain);
        
        // 验证
        verify(chain, times(1)).invokeNext(request, response);
    }
    
    @Test
    public void testAuthenticationValve() throws Exception {
        // 创建认证阀门
        AuthenticationValve valve = new AuthenticationValve();
        valve.setName("testAuthValve");
        
        // 模拟请求和响应
        ValveRequest request = mock(ValveRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");
        
        ValveResponse response = mock(ValveResponse.class);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(printWriter);
        
        ValveChain chain = mock(ValveChain.class);
        
        // 执行阀门
        valve.invoke(request, response, chain);
        
        // 验证
        verify(chain, times(1)).invokeNext(request, response);
        assertNotNull(request.getAttribute("currentUser"));
    }
    
    @Test
    public void testRateLimitingValve() throws Exception {
        // 创建限流阀门
        RateLimitingValve valve = new RateLimitingValve();
        valve.setName("testRateLimitValve");
        valve.setGlobalRate(10.0);
        valve.setClientRate(1.0);
        
        // 模拟请求和响应
        ValveRequest request = mock(ValveRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        
        ValveResponse response = mock(ValveResponse.class);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(printWriter);
        
        ValveChain chain = mock(ValveChain.class);
        
        // 第一次请求应该通过
        valve.invoke(request, response, chain);
        verify(chain, times(1)).invokeNext(request, response);
        
        // 重置mock
        reset(chain);
        
        // 快速第二次请求应该被限流
        valve.invoke(request, response, chain);
        verify(chain, never()).invokeNext(request, response);
        verify(response, times(1)).setStatus(429);
    }
    
    @Test
    public void testCacheValve() throws Exception {
        // 创建缓存阀门
        CacheValve valve = new CacheValve();
        valve.setName("testCacheValve");
        valve.setMaxSize(10);
        valve.setExpireAfterWrite(10);
        
        // 模拟请求和响应
        ValveRequest request = mock(ValveRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("param=value");
        
        ValveResponse response = mock(ValveResponse.class);
        
        ValveChain chain = mock(ValveChain.class);
        
        // 第一次请求应该执行后续阀门
        valve.invoke(request, response, chain);
        verify(chain, times(1)).invokeNext(request, response);
        
        // 重置mock
        reset(chain);
        reset(response);
        
        // 第二次相同请求应该从缓存获取
        valve.invoke(request, response, chain);
        verify(chain, never()).invokeNext(request, response);
    }
    
    @Test
    public void testBasicValve() throws Exception {
        // 创建基础阀门
        BasicValve valve = new BasicValve();
        valve.setName("testBasicValve");
        
        
        // 模拟请求和响应
//        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
//        
//        when(httpRequest.getMethod()).thenReturn("GET");
//        when(httpRequest.getRequestURI()).thenReturn("/health");
//        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
//        
//        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
//        StringWriter writer = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(writer);
//        when(httpResponse.getWriter()).thenReturn(printWriter);
//        
//        ValveRequest request = new ValveRequest(httpRequest);
//        ValveResponse response = new ValveResponse(httpResponse);
        
        // 模拟请求和响应
        ValveRequest request = mock(ValveRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/health");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        ValveResponse response = mock(ValveResponse.class);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(printWriter);
        
        ValveChain chain = mock(ValveChain.class);
        
        // 执行阀门
        valve.invoke(request, response, chain);
        
        // 验证响应内容包含健康检查信息
        assertTrue(writer.toString().contains("\"UP\""));
    }
    
    @Test
    public void testStandardPipeline() throws Exception {
        // 创建标准管道
        StandardPipeline pipeline = new StandardPipeline();
        
        // 添加测试阀门
        LoggingValve loggingValve = new LoggingValve();
        loggingValve.setOrder(100);
        pipeline.addValve(loggingValve);
        
        // 设置基础阀门
        BasicValve basicValve = new BasicValve();
        pipeline.setBasicValve(basicValve);
        
        // 初始化管道
        pipeline.init();
        
        // 模拟请求和响应
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getMethod()).thenReturn("GET");
        when(httpRequest.getRequestURI()).thenReturn("/health");
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        when(httpResponse.getWriter()).thenReturn(printWriter);
        
        ValveRequest request = new ValveRequest(httpRequest);
        ValveResponse response = new ValveResponse(httpResponse);
        
        // 执行管道
        pipeline.invoke(request, response);
        
        // 验证
        verify(httpResponse, atLeastOnce()).setContentType(anyString());
        assertTrue(writer.toString().contains("UP"));
        
        // 清理
        pipeline.destroy();
    }
    
    @Test
    public void testValveException() {
        // 创建会抛出异常的阀门
        Valve errorValve = new AbstractValve("errorValve") {
            @Override
            protected void doInvoke(ValveRequest request, ValveResponse response, ValveChain chain) throws Exception {
                throw new RuntimeException("Test exception");
            }
        };
        
        // 创建管道
        StandardPipeline pipeline = new StandardPipeline();
        pipeline.addValve(errorValve);
        
        // 模拟请求和响应
        ValveRequest request = mock(ValveRequest.class);
        ValveResponse response = mock(ValveResponse.class);
        
        // 验证会抛出异常
        assertThrows(Exception.class, () -> pipeline.invoke(request, response));
    }
}
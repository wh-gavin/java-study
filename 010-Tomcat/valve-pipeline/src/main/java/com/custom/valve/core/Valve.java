package com.custom.valve.core;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * 阀门接口
 * 
 * 定义阀门的基本契约，所有自定义阀门都需要实现此接口
 * 阀门是管道中的基本处理单元，可以执行认证、日志、限流等操作
 * 
 * 设计模式：责任链模式 + 管道过滤器模式
 */
public interface Valve {
    
    /**
     * 获取阀门名称
     * 
     * @return 阀门名称，用于唯一标识阀门
     */
    String getName();
    
    /**
     * 设置下一个阀门
     * 
     * @param valve 下一个阀门实例
     * 
     * 注：通过设置下一个阀门，可以构建阀门链
     */
    void setNext(Valve valve);
    
    /**
     * 获取下一个阀门
     * 
     * @return 下一个阀门实例，可能为null（如果是最后一个阀门）
     */
    Valve getNext();
    
    /**
     * 执行阀门逻辑
     * 
     * @param request Valve请求对象，包装了原始的HttpServletRequest
     * @param response Valve响应对象，包装了原始的HttpServletResponse
     * @param chain 阀门链，用于控制执行流程
     * @throws IOException 当I/O操作失败时抛出
     * @throws ServletException 当Servlet相关操作失败时抛出
     * 
     * 注：这是阀门的核心方法，包含主要的业务逻辑
     */
    void invoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException;
    
    /**
     * 是否支持异步处理
     * 
     * @return true表示支持异步，false表示不支持
     * 
     * 注：默认实现返回true，子类可以覆盖此方法
     */
    default boolean isAsyncSupported() {
        return true;
    }
    
    /**
     * 初始化阀门
     * 
     * @throws ValveException 当初始化失败时抛出
     * 
     * 注：在阀门被添加到管道后调用，用于执行初始化逻辑
     */
    default void init() throws ValveException {
        // 默认实现为空
    }
    
    /**
     * 销毁阀门
     * 
     * 注：在阀门从管道移除或容器关闭时调用，用于清理资源
     */
    default void destroy() {
        // 默认实现为空
    }
}
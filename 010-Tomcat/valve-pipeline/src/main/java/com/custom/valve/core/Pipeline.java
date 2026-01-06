package com.custom.valve.core;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * 管道接口
 * 
 * 定义管道的基本契约，管理阀门链的执行
 * 管道是阀门链的容器，控制请求的处理流程
 * 
 * 设计模式：管道过滤器模式 + 责任链模式
 */
public interface Pipeline {
    
    /**
     * 执行管道
     * 
     * @param request Valve请求对象
     * @param response Valve响应对象
     * @throws IOException 当I/O操作失败时抛出
     * @throws ServletException 当Servlet相关操作失败时抛出
     * 
     * 注：这是管道的核心方法，启动整个阀门链的执行
     */
    void invoke(ValveRequest request, ValveResponse response)
            throws IOException, ServletException;
    
    /**
     * 添加阀门到管道
     * 
     * @param valve 要添加的阀门
     * @throws IllegalArgumentException 如果阀门名为空或已存在
     */
    void addValve(Valve valve);
    
    /**
     * 移除阀门
     * 
     * @param name 阀门名称
     * @return 被移除的阀门，如果不存在则返回null
     */
    Valve removeValve(String name);
    
    /**
     * 获取阀门
     * 
     * @param name 阀门名称
     * @return 阀门实例，如果不存在则返回null
     */
    Valve getValve(String name);
    
    /**
     * 获取所有阀门
     * 
     * @return 阀门列表的不可修改视图
     */
    java.util.List<Valve> getValves();
    
    /**
     * 设置基础阀门
     * 
     * @param basicValve 基础阀门（通常是处理实际业务的阀门）
     */
    void setBasicValve(Valve basicValve);
    
    /**
     * 获取基础阀门
     * 
     * @return 基础阀门
     */
    Valve getBasicValve();
    
    /**
     * 初始化管道
     * 
     * @throws ValveException 当初始化失败时抛出
     */
    void init() throws ValveException;
    
    /**
     * 销毁管道
     */
    void destroy();
    
    /**
     * 检查管道是否已初始化
     * 
     * @return true表示已初始化，false表示未初始化
     */
    boolean isInitialized();
    
    /**
     * 检查管道是否为空
     * 
     * @return true表示管道为空，false表示不为空
     */
    boolean isEmpty();
    
    /**
     * 获取阀门数量
     * 
     * @return 阀门数量（不包括基础阀门）
     */
    int getValveCount();
}
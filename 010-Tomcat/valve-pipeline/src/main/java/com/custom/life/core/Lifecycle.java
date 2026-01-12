package com.custom.life.core;

import java.util.List;

/**
 * 生命周期接口
 */
public interface Lifecycle {
    // 添加监听器
    void addLifecycleListener(LifecycleListener listener);

    // 获取所有监听器
    List<LifecycleListener> findLifecycleListeners();
    
    // 移除监听器
    void removeLifecycleListener(LifecycleListener listener);
    
    // 初始化方法
    void init() throws LifecycleException;
    
    // 启动方法
    void start() throws LifecycleException;
    
    // 停止方法
    void stop() throws LifecycleException;
    
    // 销毁方法
    void destroy() throws LifecycleException;
    
    // 获取当前状态
    LifecycleState getState();
    
    // 获取状态名称
    String getStateName();
}
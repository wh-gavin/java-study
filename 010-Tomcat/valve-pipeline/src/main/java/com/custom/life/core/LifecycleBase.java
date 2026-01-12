package com.custom.life.core;

import java.util.List;

/**
 * 生命周期基类（模板方法实现）
 */
public abstract class LifecycleBase implements Lifecycle {
    
    // 生命周期支持对象
    private final LifecycleSupport lifecycleSupport = new LifecycleSupport(this);
    
    // 当前状态
    private volatile LifecycleState state = LifecycleState.NEW;
    
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleSupport.addLifecycleListener(listener);
    }
    @Override
    public List<LifecycleListener> findLifecycleListeners() {
        return lifecycleSupport.findLifecycleListeners();
    }
    
    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleSupport.removeLifecycleListener(listener);
    }
    
    @Override
    public void init() throws LifecycleException {
        // 状态检查
        if (!state.equals(LifecycleState.NEW)) {
            throw new LifecycleException("组件当前状态不能执行初始化操作");
        }
        
        // 触发初始化前事件
        lifecycleSupport.fireLifecycleEvent("before_init");
        
        try {
            // 设置状态为初始化中
            setState(LifecycleState.INITIALIZING);
            
            // 调用具体的初始化逻辑（由子类实现）
            initInternal();
            
            // 设置状态为初始化完成
            setState(LifecycleState.INITIALIZED);
            
            // 触发初始化完成事件
            lifecycleSupport.fireLifecycleEvent("after_init");
        } catch (Exception e) {
            setState(LifecycleState.FAILED);
            throw new LifecycleException("初始化失败", e);
        }
    }
    
    @Override
    public void start() throws LifecycleException {
        // 如果已经启动，直接返回
        if (state.equals(LifecycleState.STARTED)) {
            return;
        }
        
        // 如果处于新建状态，先初始化
        if (state.equals(LifecycleState.NEW)) {
            init();
        }
        
        // 触发启动前事件
        lifecycleSupport.fireLifecycleEvent("before_start");
        
        try {
            // 启动准备
            setState(LifecycleState.STARTING_PREP);
            
            // 启动中
            setState(LifecycleState.STARTING);
            
            // 调用具体的启动逻辑
            startInternal();
            
            // 启动完成
            setState(LifecycleState.STARTED);
            
            // 触发启动完成事件
            lifecycleSupport.fireLifecycleEvent("after_start");
        } catch (Exception e) {
            setState(LifecycleState.FAILED);
            throw new LifecycleException("启动失败", e);
        }
    }
    
    @Override
    public void stop() throws LifecycleException {
        // 触发停止前事件
        lifecycleSupport.fireLifecycleEvent("before_stop");
        
        try {
            // 停止准备
            setState(LifecycleState.STOPPING_PREP);
            
            // 停止中
            setState(LifecycleState.STOPPING);
            
            // 调用具体的停止逻辑
            stopInternal();
            
            // 停止完成
            setState(LifecycleState.STOPPED);
            
            // 触发停止完成事件
            lifecycleSupport.fireLifecycleEvent("after_stop");
        } catch (Exception e) {
            setState(LifecycleState.FAILED);
            throw new LifecycleException("停止失败", e);
        }
    }
    
    @Override
    public void destroy() throws LifecycleException {
        // 触发销毁前事件
        lifecycleSupport.fireLifecycleEvent("before_destroy");
        
        try {
            // 销毁中
            setState(LifecycleState.DESTROYING);
            
            // 调用具体的销毁逻辑
            destroyInternal();
            
            // 销毁完成
            setState(LifecycleState.DESTROYED);
            
            // 触发销毁完成事件
            lifecycleSupport.fireLifecycleEvent("after_destroy");
        } catch (Exception e) {
            setState(LifecycleState.FAILED);
            throw new LifecycleException("销毁失败", e);
        }
    }
    
    @Override
    public LifecycleState getState() {
        return state;
    }
    
    @Override
    public String getStateName() {
        return state.name();
    }
    
    // 设置状态
    protected synchronized void setState(LifecycleState state) {
        this.state = state;
        // 触发状态变更事件
        lifecycleSupport.fireLifecycleEvent("state_changed", state);
    }
    
    // 获取当前状态
    protected LifecycleState getStateInternal() {
        return state;
    }
    
    // 抽象方法，由子类实现具体逻辑
    protected abstract void initInternal() throws LifecycleException;
    protected abstract void startInternal() throws LifecycleException;
    protected abstract void stopInternal() throws LifecycleException;
    protected abstract void destroyInternal() throws LifecycleException;
}
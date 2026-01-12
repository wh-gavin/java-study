package com.custom.life.core;


/**
 * 生命周期状态枚举
 */
public enum LifecycleState {
    NEW,              // 新建
    INITIALIZING,     // 初始化中
    INITIALIZED,      // 初始化完成
    STARTING_PREP,    // 启动准备中
    STARTING,         // 启动中
    STARTED,          // 已启动
    STOPPING_PREP,    // 停止准备中
    STOPPING,         // 停止中
    STOPPED,          // 已停止
    DESTROYING,       // 销毁中
    DESTROYED,        // 已销毁
    FAILED;           // 失败
}
package com.custom.life;

import java.util.ArrayList;
import java.util.List;

import com.custom.life.core.LifecycleBase;
import com.custom.life.core.LifecycleException;

/**
 * 容器组件基类
 */
public abstract class ContainerBase extends LifecycleBase {
    
    // 子容器列表
    protected final List<ContainerBase> children = new ArrayList<>();
    
    // 父容器
    protected ContainerBase parent;
    
    // 容器名称
    protected String name;
    
    public ContainerBase(String name) {
        this.name = name;
    }
    
    public void addChild(ContainerBase child) {
        child.parent = this;
        children.add(child);
    }
    
    public void removeChild(ContainerBase child) {
        children.remove(child);
        child.parent = null;
    }
    
    public ContainerBase getParent() {
        return parent;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        System.out.println(name + ": 初始化内部资源");
        // 初始化子容器
        for (ContainerBase child : children) {
            child.init();
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println(name + ": 启动内部服务");
        // 启动子容器
        for (ContainerBase child : children) {
            child.start();
        }
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        System.out.println(name + ": 停止内部服务");
        // 停止子容器（逆序）
        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).stop();
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        System.out.println(name + ": 销毁内部资源");
        // 销毁子容器（逆序）
        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).destroy();
        }
        children.clear();
    }
}
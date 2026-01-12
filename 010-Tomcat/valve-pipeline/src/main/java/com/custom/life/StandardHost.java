package com.custom.life;

import com.custom.life.core.LifecycleException;

/**
 * Host组件
 */
public class StandardHost extends ContainerBase {
    
    public StandardHost(String name) {
        super(name);
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println(getName() + ": 启动虚拟主机");
        super.startInternal();
    }
}
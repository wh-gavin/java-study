package com.custom.life;

import com.custom.life.core.LifecycleException;

/**
 * Service组件
 */
public class StandardService extends ContainerBase {
    
    public StandardService(String name) {
        super(name);
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println(getName() + ": 启动服务");
        super.startInternal();
    }
}
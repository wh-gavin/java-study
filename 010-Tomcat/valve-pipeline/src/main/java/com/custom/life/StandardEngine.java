package com.custom.life;

import com.custom.life.core.LifecycleException;

/**
 * Engine组件
 */
public class StandardEngine extends ContainerBase {
    
    public StandardEngine(String name) {
        super(name);
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println(getName() + ": 启动引擎");
        super.startInternal();
    }
}

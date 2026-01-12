package com.custom.life;

import com.custom.life.core.LifecycleException;

/**
 * Context组件（Web应用）
 */
public class StandardContext extends ContainerBase {
    
    private String docBase;
    
    public StandardContext(String name, String docBase) {
        super(name);
        this.docBase = docBase;
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println(getName() + ": 启动Web应用，文档根目录: " + docBase);
        super.startInternal();
    }
}
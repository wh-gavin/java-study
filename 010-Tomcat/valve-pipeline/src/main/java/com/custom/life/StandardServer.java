package com.custom.life;

import com.custom.life.core.LifecycleException;

/**
 * Server组件（最顶层容器）
 */
public class StandardServer extends ContainerBase {
    
    public StandardServer() {
        super("Server");
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        System.out.println("StandardServer: 初始化服务器配置");
        super.initInternal();
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println("StandardServer: 启动服务器");
        super.startInternal();
    }
}

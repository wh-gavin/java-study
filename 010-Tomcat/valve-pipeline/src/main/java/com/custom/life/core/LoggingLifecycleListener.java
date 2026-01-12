package com.custom.life.core;

import com.custom.life.ContainerBase;

/**
 * 监听器实现示例
 */
public class LoggingLifecycleListener implements LifecycleListener {
    
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        String componentName = event.getLifecycle() instanceof ContainerBase ?
                ((ContainerBase) event.getLifecycle()).getName() : 
                event.getLifecycle().getClass().getSimpleName();
        
        String message = String.format("[监听器] %s - 事件类型: %s", 
                componentName, event.getType());
        
        if (event.getData() != null) {
            message += ", 数据: " + event.getData();
        }
        
        System.out.println(message);
    }
}
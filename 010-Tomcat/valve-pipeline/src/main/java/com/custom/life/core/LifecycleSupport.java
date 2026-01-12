package com.custom.life.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 生命周期支持类（管理监听器）
 */
public class LifecycleSupport {
    private final Lifecycle lifecycle;
    private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();
    
    public LifecycleSupport(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }
    
    public void addLifecycleListener(LifecycleListener listener) {
        listeners.add(listener);
    }
    
    public List<LifecycleListener> findLifecycleListeners() {
        return new ArrayList<>(listeners);
    }
    
    public void removeLifecycleListener(LifecycleListener listener) {
        listeners.remove(listener);
    }
    
    public void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        for (LifecycleListener listener : listeners) {
            listener.lifecycleEvent(event);
        }
    }
    
    public void fireLifecycleEvent(String type) {
        fireLifecycleEvent(type, null);
    }
}

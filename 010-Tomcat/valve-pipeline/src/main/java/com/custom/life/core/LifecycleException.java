package com.custom.life.core;

/**
 * 生命周期异常
 */
public class LifecycleException extends Exception {
    public LifecycleException(String message) {
        super(message);
    }
    
    public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
}

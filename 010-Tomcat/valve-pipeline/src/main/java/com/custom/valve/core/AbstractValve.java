package com.custom.valve.core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 抽象阀门基类
 * 
 * 提供阀门的基本实现，包括前置处理、后置处理、异常处理等
 * 所有自定义阀门都应该继承此类，只需实现doInvoke方法
 * 
 * 模板方法模式：定义了阀门执行的模板，子类实现具体逻辑
 */
public abstract class AbstractValve implements Valve {
    
    protected String name;      // 阀门名称
    protected Valve next;       // 下一个阀门
    protected int order = 0;    // 执行顺序，数值小的先执行
    protected boolean enabled = true; // 是否启用
    
    // 日志记录器，子类可以使用
    protected org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
    
    /**
     * 构造函数
     * 
     * @param name 阀门名称，必须唯一
     */
    public AbstractValve(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Valve name cannot be null or empty");
        }
        this.name = name;
    }
    
    // ========== 实现Valve接口的方法 ==========
    
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    
    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }
    
    @Override
    public Valve getNext() {
        return next;
    }
    
    @Override
    public void invoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException {
        
        // 如果阀门被禁用，直接跳过
        if (!enabled) {
            chain.invokeNext(request, response);
            return;
        }
        
        // 记录执行开始时间
        long startTime = System.currentTimeMillis();
        
        try {
            // 前置处理
            beforeInvoke(request, response);
            
            // 执行阀门核心逻辑（由子类实现）
            doInvoke(request, response, chain);
            
            // 后置处理
            afterInvoke(request, response);
            
        } catch (Exception e) {
            // 异常处理
            handleException(request, response, e);
            
            // 重新抛出异常，让上层处理
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof ServletException) {
                throw (ServletException) e;
            } else {
                throw new ServletException("Valve execution failed: " + name, e);
            }
            
        } finally {
            // 记录执行时间
            long duration = System.currentTimeMillis() - startTime;
            request.setAttribute(name + ".duration", duration);
            
            // 记录慢请求
            if (duration > 1000) { // 超过1秒视为慢请求
                logger.warn("Slow valve execution: {} took {}ms", name, duration);
            }
        }
    }
    
    // ========== 模板方法（子类需要实现或覆盖） ==========
    
    /**
     * 执行阀门核心逻辑
     * 
     * 这是阀门的主要逻辑，子类必须实现此方法
     * 注意：子类应该调用chain.invokeNext()来继续执行下一个阀门
     * 
     * @param request Valve请求对象
     * @param response Valve响应对象
     * @param chain 阀门链
     * @throws IOException 当I/O操作失败时抛出
     * @throws ServletException 当Servlet相关操作失败时抛出
     * @throws Exception 
     */
    protected abstract void doInvoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException, Exception;
    
    /**
     * 前置处理（在doInvoke之前调用）
     * 
     * 子类可以覆盖此方法，在阀门执行前做一些准备工作
     * 如：记录日志、设置请求属性等
     * 
     * @param request Valve请求对象
     * @param response Valve响应对象
     */
    protected void beforeInvoke(ValveRequest request, ValveResponse response) {
        // 默认实现为空，子类可以覆盖
    }
    
    /**
     * 后置处理（在doInvoke之后调用）
     * 
     * 子类可以覆盖此方法，在阀门执行后做一些清理工作
     * 如：记录响应信息、清理资源等
     * 
     * @param request Valve请求对象
     * @param response Valve响应对象
     */
    protected void afterInvoke(ValveRequest request, ValveResponse response) {
        // 默认实现为空，子类可以覆盖
    }
    
    /**
     * 异常处理
     * 
     * 当阀门执行过程中发生异常时调用
     * 子类可以覆盖此方法，实现自定义的异常处理逻辑
     * 
     * @param request Valve请求对象
     * @param response Valve响应对象
     * @param e 发生的异常
     */
    protected void handleException(ValveRequest request, ValveResponse response, Exception e) {
        // 默认实现：记录错误日志，并设置错误信息到请求属性
        logger.error("Valve execution error: {}", name, e);
        request.setAttribute("lastError", e);
        request.setAttribute(name + ".error", e.getMessage());
    }
    
    // ========== 配置相关方法 ==========
    
    /**
     * 设置执行顺序
     * 
     * @param order 执行顺序，数值小的先执行
     */
    public void setOrder(int order) {
        this.order = order;
    }
    
    /**
     * 获取执行顺序
     * 
     * @return 执行顺序
     */
    public int getOrder() {
        return order;
    }
    
    /**
     * 启用阀门
     */
    public void enable() {
        this.enabled = true;
    }
    
    /**
     * 禁用阀门
     */
    public void disable() {
        this.enabled = false;
    }
    
    /**
     * 设置启用状态
     * 
     * @param enabled true表示启用，false表示禁用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * 检查是否启用
     * 
     * @return true表示启用，false表示禁用
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    // ========== 生命周期方法 ==========
    
    @Override
    public void init() throws ValveException {
        // 默认实现：记录初始化日志
        logger.info("Valve initialized: {}", name);
    }
    
    @Override
    public void destroy() {
        // 默认实现：记录销毁日志
        logger.info("Valve destroyed: {}", name);
    }
    
    // ========== 其他方法 ==========
    
    /**
     * 检查阀门配置是否有效
     * 
     * @return true表示配置有效，false表示无效
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }
    
    /**
     * 转换为字符串表示
     * 
     * @return 阀门的字符串表示
     */
    @Override
    public String toString() {
        return String.format("Valve{name='%s', order=%d, enabled=%s}", 
            name, order, enabled);
    }
}
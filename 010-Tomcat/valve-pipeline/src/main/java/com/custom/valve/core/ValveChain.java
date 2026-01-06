package com.custom.valve.core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 阀门链接口
 * 
 * 控制阀门执行流程，提供执行下一个阀门的能力
 * 通过阀门链可以实现请求的链式处理
 */
public interface ValveChain {
    
    /**
     * 执行下一个阀门
     * 
     * @param request Valve请求对象
     * @param response Valve响应对象
     * @throws IOException 当I/O操作失败时抛出
     * @throws ServletException 当Servlet相关操作失败时抛出
     * 
     * 注：阀门在执行完自身逻辑后，通常需要调用此方法继续执行下一个阀门
     */
    void invokeNext(ValveRequest request, ValveResponse response)
            throws IOException, ServletException;
    
    /**
     * 获取当前执行的阀门索引
     * 
     * @return 当前阀门在链中的索引，从0开始
     */
    int getCurrentIndex();
    
    /**
     * 获取阀门总数
     * 
     * @return 链中阀门的总数，包括基础阀门
     */
    int getValveCount();
    
    /**
     * 是否还有下一个阀门
     * 
     * @return true表示还有下一个阀门，false表示当前是最后一个阀门
     */
    boolean hasNext();
    
    /**
     * 重置阀门链
     * 
     * 注：将当前索引重置为-1，使得阀门链可以重新使用
     */
    void reset();
}
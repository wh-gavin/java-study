package com.custom.valve.core;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

/**
 * 标准管道实现
 * 
 * 管理阀门链的执行流程，支持阀门的添加、移除、排序等操作
 * 提供阀门链的完整生命周期管理
 * 
 * 设计模式：责任链模式 + 管道过滤器模式 + 组合模式
 */
public class StandardPipeline implements Pipeline, ValveChain {
    
    // ========== 核心数据结构 ==========
    
    private Valve firstValve;      // 第一个阀门
    private Valve basicValve;      // 基础阀门（最后一个阀门，处理实际业务）
    private final List<Valve> valves = new ArrayList<>();          // 阀门列表
    private final Map<String, Valve> valveMap = new HashMap<>();   // 阀门名称映射
    
    // 线程局部变量，用于跟踪当前执行的阀门索引
    private final ThreadLocal<Integer> currentIndex = ThreadLocal.withInitial(() -> -1);
    
    // 管道状态
    private boolean initialized = false;
    private boolean destroyed = false;
    
    // ========== 阀门管理 ==========
    
    /**
     * 添加阀门到管道
     * 
     * @param valve 要添加的阀门
     * @throws IllegalArgumentException 如果阀门名为空或已存在
     * @throws IllegalStateException 如果管道已销毁
     */
    public void addValve(Valve valve) {
        checkDestroyed();
        
        synchronized (valves) {
            String name = valve.getName();
            
            // 参数验证
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Valve name cannot be null or empty");
            }
            
            if (valveMap.containsKey(name)) {
                throw new IllegalArgumentException("Valve with name '" + name + "' already exists");
            }
            
            // 添加到数据结构
            valves.add(valve);
            valveMap.put(name, valve);
            
            // 重新排序和构建链
            sortValves();
            rebuildChain();
            
            // 记录日志
            logger().info("Valve added: {}", name);
        }
    }
    
    /**
     * 移除阀门
     * 
     * @param name 阀门名称
     * @return 被移除的阀门，如果不存在则返回null
     * @throws IllegalStateException 如果管道已销毁
     */
    public Valve removeValve(String name) {
        checkDestroyed();
        
        synchronized (valves) {
            Valve removed = valveMap.remove(name);
            if (removed != null) {
                valves.remove(removed);
                rebuildChain();
                logger().info("Valve removed: {}", name);
            }
            return removed;
        }
    }
    
    /**
     * 获取阀门
     * 
     * @param name 阀门名称
     * @return 阀门实例，如果不存在则返回null
     */
    public Valve getValve(String name) {
        return valveMap.get(name);
    }
    
    /**
     * 获取所有阀门
     * 
     * @return 阀门列表的不可修改视图
     */
    public List<Valve> getValves() {
        synchronized (valves) {
            return Collections.unmodifiableList(new ArrayList<>(valves));
        }
    }
    
    /**
     * 按执行顺序排序阀门
     */
    private void sortValves() {
        valves.sort(Comparator.comparingInt(v -> {
            if (v instanceof AbstractValve) {
                return ((AbstractValve) v).getOrder();
            }
            return 0; // 默认顺序为0
        }));
    }
    
    /**
     * 重新构建阀门链
     */
    private void rebuildChain() {
        if (valves.isEmpty()) {
            firstValve = basicValve;
            return;
        }
        
        // 构建阀门链表
        Valve previous = null;
        for (Valve valve : valves) {
            if (previous == null) {
                firstValve = valve;
            } else {
                previous.setNext(valve);
            }
            previous = valve;
        }
        
        // 连接到基础阀门
        if (previous != null) {
            previous.setNext(basicValve);
        }
        
        // 基础阀门的下一个为null
        if (basicValve != null) {
            basicValve.setNext(null);
        }
    }
    
    // ========== 基础阀门操作 ==========
    
    /**
     * 设置基础阀门
     * 
     * @param basicValve 基础阀门（通常是处理实际业务的阀门）
     * @throws IllegalStateException 如果管道已销毁
     */
    public void setBasicValve(Valve basicValve) {
        checkDestroyed();
        this.basicValve = basicValve;
        rebuildChain();
    }
    
    /**
     * 获取基础阀门
     * 
     * @return 基础阀门
     */
    public Valve getBasicValve() {
        return basicValve;
    }
    
    // ========== 管道执行 ==========
    
    /**
     * 执行管道
     * 
     * @param request Valve请求对象
     * @param response Valve响应对象
     * @throws IOException 当I/O操作失败时抛出
     * @throws ServletException 当Servlet相关操作失败时抛出
     * @throws IllegalStateException 如果管道未初始化或已销毁
     */
    public void invoke(ValveRequest request, ValveResponse response)
            throws IOException, ServletException {
        
        checkInitialized();
        
        // 重置当前索引
        currentIndex.set(-1);
        
        try {
            // 从第一个阀门开始执行
            if (firstValve != null) {
                firstValve.invoke(request, response, this);
            } else if (basicValve != null) {
                basicValve.invoke(request, response, this);
            } else {
                logger().warn("Pipeline has no valves to execute");
            }
            
        } finally {
            // 清理线程局部变量
            currentIndex.remove();
        }
    }
    
    /**
     * 执行下一个阀门（实现ValveChain接口）
     */
    @Override
    public void invokeNext(ValveRequest request, ValveResponse response)
            throws IOException, ServletException {
        
        int index = currentIndex.get();
        Valve nextValve;
        
        // 确定下一个要执行的阀门
        if (index == -1 && firstValve != null) {
            // 第一次调用，执行第一个阀门
            nextValve = firstValve;
            currentIndex.set(0);
        } else if (index >= 0 && index < valves.size()) {
            // 执行当前阀门的下一个阀门
            nextValve = valves.get(index).getNext();
            currentIndex.set(index + 1);
        } else {
            // 执行基础阀门
            nextValve = basicValve;
            currentIndex.set(valves.size());
        }
        
        // 执行阀门
        if (nextValve != null) {
            nextValve.invoke(request, response, this);
        }
    }
    
    // ========== ValveChain接口实现 ==========
    
    @Override
    public int getCurrentIndex() {
        return currentIndex.get();
    }
    
    @Override
    public int getValveCount() {
        return valves.size() + (basicValve != null ? 1 : 0);
    }
    
    @Override
    public boolean hasNext() {
        int index = currentIndex.get();
        return (index < valves.size()) || (index == valves.size() && basicValve != null);
    }
    
    @Override
    public void reset() {
        currentIndex.set(-1);
    }
    
    // ========== 管道生命周期管理 ==========
    
    /**
     * 初始化管道
     * 
     * @throws ValveException 当初始化失败时抛出
     * @throws IllegalStateException 如果管道已销毁
     */
    public void init() throws ValveException {
        checkDestroyed();
        
        synchronized (this) {
            if (initialized) {
                return;
            }
            
            try {
                // 初始化所有阀门
                for (Valve valve : valves) {
                    valve.init();
                }
                
                if (basicValve != null) {
                    basicValve.init();
                }
                
                initialized = true;
                logger().info("Pipeline initialized with {} valves", valves.size());
                
            } catch (ValveException e) {
                throw e;
            } catch (Exception e) {
                throw new ValveException("Pipeline", "Pipeline initialization failed", e);
            }
        }
    }
    
    /**
     * 销毁管道
     */
    public void destroy() {
        synchronized (this) {
            if (destroyed) {
                return;
            }
            
            // 销毁所有阀门
            for (Valve valve : valves) {
                try {
                    valve.destroy();
                } catch (Exception e) {
                    logger().error("Error destroying valve: {}", valve.getName(), e);
                }
            }
            
            if (basicValve != null) {
                try {
                    basicValve.destroy();
                } catch (Exception e) {
                    logger().error("Error destroying basic valve", e);
                }
            }
            
            // 清理数据结构
            valves.clear();
            valveMap.clear();
            firstValve = null;
            basicValve = null;
            initialized = false;
            destroyed = true;
            
            logger().info("Pipeline destroyed");
        }
    }
    
    /**
     * 检查管道是否已初始化
     * 
     * @throws IllegalStateException 如果管道未初始化
     */
    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Pipeline not initialized");
        }
    }
    
    /**
     * 检查管道是否已销毁
     * 
     * @throws IllegalStateException 如果管道已销毁
     */
    private void checkDestroyed() {
        if (destroyed) {
            throw new IllegalStateException("Pipeline already destroyed");
        }
    }
    
    // ========== 状态查询 ==========
    
    /**
     * 检查管道是否已初始化
     * 
     * @return true表示已初始化，false表示未初始化
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * 检查管道是否已销毁
     * 
     * @return true表示已销毁，false表示未销毁
     */
    public boolean isDestroyed() {
        return destroyed;
    }
    
    /**
     * 获取阀门数量
     * 
     * @return 阀门数量（不包括基础阀门）
     */
    public int getValveCountExcludingBasic() {
        return valves.size();
    }
    
    // ========== 日志记录器 ==========
    
    private org.slf4j.Logger logger() {
        return org.slf4j.LoggerFactory.getLogger(StandardPipeline.class);
    }
    
    // ========== 其他方法 ==========
    
    /**
     * 检查管道是否为空
     * 
     * @return true表示管道为空，false表示不为空
     */
    public boolean isEmpty() {
        return valves.isEmpty() && basicValve == null;
    }
    
    /**
     * 转换为字符串表示
     * 
     * @return 管道的字符串表示
     */
    @Override
    public String toString() {
        return String.format("Pipeline{valves=%d, initialized=%s, destroyed=%s}", 
            valves.size(), initialized, destroyed);
    }
}
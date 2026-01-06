package com.custom.valve.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.valve.core.Pipeline;
import com.custom.valve.core.StandardPipeline;
import com.custom.valve.core.ValveException;
import com.custom.valve.valves.AuthenticationValve;
import com.custom.valve.valves.BasicValve;
import com.custom.valve.valves.CacheValve;
import com.custom.valve.valves.LoggingValve;
import com.custom.valve.valves.RateLimitingValve;

/**
 * 管道工厂
 * 
 * 创建和管理管道实例，支持不同类型的管道配置
 * 单例模式，确保全局只有一个工厂实例
 * 
 * 使用场景：管道配置管理、动态管道创建、管道生命周期管理
 */
public class PipelineFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(PipelineFactory.class);
    private static final PipelineFactory INSTANCE = new PipelineFactory();
    
    private final Map<String, Pipeline> pipelines = new HashMap<>();
    private boolean initialized = false;
    
    /**
     * 私有构造函数（单例模式）
     */
    private PipelineFactory() {
        // 防止外部实例化
    }
    
    /**
     * 获取工厂实例
     */
    public static PipelineFactory getInstance() {
        return INSTANCE;
    }
    
    /**
     * 初始化默认管道
     */
    public synchronized void initDefaultPipelines() throws ValveException {
        if (initialized) {
            return;
        }
        
        try {
            // 1. API管道（包含所有阀门）
            Pipeline apiPipeline = createApiPipeline();
            pipelines.put("api", apiPipeline);
            logger.info("API pipeline created");
            
            // 2. 公共管道（不需要认证）
            Pipeline publicPipeline = createPublicPipeline();
            pipelines.put("public", publicPipeline);
            logger.info("Public pipeline created");
            
            // 3. 静态资源管道（包含缓存）
            Pipeline staticPipeline = createStaticPipeline();
            pipelines.put("static", staticPipeline);
            logger.info("Static pipeline created");
            
            // 4. 管理管道（需要认证和特殊权限）
            Pipeline adminPipeline = createAdminPipeline();
            pipelines.put("admin", adminPipeline);
            logger.info("Admin pipeline created");
            
            // 初始化所有管道
            for (Pipeline pipeline : pipelines.values()) {
                if (pipeline instanceof StandardPipeline) {
                    StandardPipeline sp = (StandardPipeline) pipeline;
                    sp.init();
                }
            }
            
            initialized = true;
            logger.info("All pipelines initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize pipelines", e);
            throw new ValveException("PipelineFactory", "Failed to initialize pipelines", e);
        }
    }
    
    /**
     * 创建API管道
     */
    private Pipeline createApiPipeline() {
        StandardPipeline pipeline = new StandardPipeline();
        
        // 添加阀门（按执行顺序）
        
        // 1. 日志阀门（最先执行）
        LoggingValve loggingValve = new LoggingValve();
        loggingValve.setOrder(100);
        loggingValve.setLogRequest(true);
        loggingValve.setLogResponse(true);
        loggingValve.setSlowThreshold(500); // 500ms视为慢请求
        pipeline.addValve(loggingValve);
        
        // 2. 缓存阀门
        CacheValve cacheValve = new CacheValve();
        cacheValve.setOrder(150);
        cacheValve.setMaxSize(500);
        cacheValve.setExpireAfterWrite(60); // 1分钟
        cacheValve.setExpireAfterAccess(30); // 30秒
        pipeline.addValve(cacheValve);
        
        // 3. 限流阀门
        RateLimitingValve rateLimitingValve = new RateLimitingValve();
        rateLimitingValve.setOrder(200);
        rateLimitingValve.setGlobalRate(1000); // 1000请求/秒
        rateLimitingValve.setClientRate(100);  // 100请求/秒/客户端
        rateLimitingValve.setBlockDuration(30); // 被阻止后30秒重试
        pipeline.addValve(rateLimitingValve);
        
        // 4. 认证阀门
        AuthenticationValve authenticationValve = new AuthenticationValve();
        authenticationValve.setOrder(300);
        authenticationValve.setTokenHeader("Authorization");
        authenticationValve.setTokenPrefix("Bearer ");
        authenticationValve.setRequireHttps(false); // 开发环境不要求HTTPS
        pipeline.addValve(authenticationValve);
        
        // 设置基础阀门
        BasicValve basicValve = new BasicValve();
        pipeline.setBasicValve(basicValve);
        
        return pipeline;
    }
    
    /**
     * 创建公共管道
     */
    private Pipeline createPublicPipeline() {
        StandardPipeline pipeline = new StandardPipeline();
        
        // 日志阀门
        LoggingValve loggingValve = new LoggingValve();
        loggingValve.setOrder(100);
        loggingValve.setLogRequest(true);
        loggingValve.setLogResponse(true);
        pipeline.addValve(loggingValve);
        
        // 限流阀门（公共接口也需要限流）
        RateLimitingValve rateLimitingValve = new RateLimitingValve();
        rateLimitingValve.setOrder(150);
        rateLimitingValve.setGlobalRate(500);
        rateLimitingValve.setClientRate(50);
        pipeline.addValve(rateLimitingValve);
        
        // 缓存阀门
        CacheValve cacheValve = new CacheValve();
        cacheValve.setOrder(200);
        cacheValve.setMaxSize(1000);
        cacheValve.setExpireAfterWrite(300); // 5分钟
        pipeline.addValve(cacheValve);
        
        // 设置基础阀门
        BasicValve basicValve = new BasicValve();
        pipeline.setBasicValve(basicValve);
        
        return pipeline;
    }
    
    /**
     * 创建静态资源管道
     */
    private Pipeline createStaticPipeline() {
        StandardPipeline pipeline = new StandardPipeline();
        
        // 日志阀门
        LoggingValve loggingValve = new LoggingValve();
        loggingValve.setOrder(100);
        loggingValve.setLogRequest(false); // 静态资源不记录请求
        loggingValve.setLogResponse(false);
        pipeline.addValve(loggingValve);
        
        // 缓存阀门（静态资源需要长时间缓存）
        CacheValve cacheValve = new CacheValve();
        cacheValve.setOrder(150);
        cacheValve.setMaxSize(5000); // 更大的缓存
        cacheValve.setExpireAfterWrite(3600); // 1小时
        cacheValve.setExpireAfterAccess(1800); // 30分钟
        cacheValve.setIncludePaths(Set.of("/static/", "/public/", "/images/", "/css/", "/js/"));
        pipeline.addValve(cacheValve);
        
        // 设置基础阀门
        BasicValve basicValve = new BasicValve();
        pipeline.setBasicValve(basicValve);
        
        return pipeline;
    }
    
    /**
     * 创建管理管道
     */
    private Pipeline createAdminPipeline() {
        StandardPipeline pipeline = new StandardPipeline();
        
        // 日志阀门（详细日志）
        LoggingValve loggingValve = new LoggingValve();
        loggingValve.setOrder(100);
        loggingValve.setLogRequest(true);
        loggingValve.setLogResponse(true);
        loggingValve.setLogHeaders(true); // 记录请求头
        loggingValve.setLogParameters(true); // 记录参数
        pipeline.addValve(loggingValve);
        
        // 认证阀门（需要管理员权限）
        AuthenticationValve authenticationValve = new AuthenticationValve();
        authenticationValve.setOrder(200);
        authenticationValve.setTokenHeader("X-Admin-Token"); // 特殊的管理员Token头
        authenticationValve.setRequireHttps(true); // 管理接口必须使用HTTPS
        
        // 添加管理员角色权限
        authenticationValve.addRolePermission("ADMIN", "*"); // 管理员有所有权限
        pipeline.addValve(authenticationValve);
        
        // 设置基础阀门
        BasicValve basicValve = new BasicValve();
        pipeline.setBasicValve(basicValve);
        
        return pipeline;
    }
    
    // ========== 管道管理方法 ==========
    
    /**
     * 获取管道
     */
    public Pipeline getPipeline(String name) {
        Pipeline pipeline = pipelines.get(name);
        if (pipeline == null) {
            throw new IllegalArgumentException("Pipeline not found: " + name);
        }
        return pipeline;
    }
    
    /**
     * 创建自定义管道
     */
    public Pipeline createPipeline(String name) {
        if (pipelines.containsKey(name)) {
            throw new IllegalArgumentException("Pipeline already exists: " + name);
        }
        
        Pipeline pipeline = new StandardPipeline();
        pipelines.put(name, pipeline);
        logger.info("Custom pipeline created: {}", name);
        
        return pipeline;
    }
    
    /**
     * 注册管道
     */
    public void registerPipeline(String name, Pipeline pipeline) {
        pipelines.put(name, pipeline);
        logger.info("Pipeline registered: {}", name);
    }
    
    /**
     * 注销管道
     */
    public void unregisterPipeline(String name) {
        Pipeline pipeline = pipelines.remove(name);
        if (pipeline != null) {
            pipeline.destroy();
            logger.info("Pipeline unregistered: {}", name);
        }
    }
    
    /**
     * 获取所有管道名称
     */
    public List<String> getPipelineNames() {
        return new ArrayList<>(pipelines.keySet());
    }
    
    /**
     * 获取所有管道
     */
    public Map<String, Pipeline> getPipelines() {
        return new HashMap<>(pipelines);
    }
    
    /**
     * 重新初始化所有管道
     */
    public void reinitializeAll() throws ValveException {
        destroyAll();
        initDefaultPipelines();
    }
    
    // ========== 生命周期管理 ==========
    
    /**
     * 初始化所有管道
     */
    public void initAll() throws ValveException {
        for (Pipeline pipeline : pipelines.values()) {
            if (pipeline instanceof StandardPipeline) {
                StandardPipeline sp = (StandardPipeline) pipeline;
                if (!sp.isInitialized()) {
                    sp.init();
                }
            }
        }
    }
    
    /**
     * 销毁所有管道
     */
    public void destroyAll() {
        for (Pipeline pipeline : pipelines.values()) {
            try {
                pipeline.destroy();
            } catch (Exception e) {
                logger.error("Error destroying pipeline", e);
            }
        }
        pipelines.clear();
        initialized = false;
        logger.info("All pipelines destroyed");
    }
    
    // ========== 状态查询 ==========
    
    /**
     * 检查是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * 获取管道数量
     */
    public int getPipelineCount() {
        return pipelines.size();
    }
    
    /**
     * 检查管道是否存在
     */
    public boolean hasPipeline(String name) {
        return pipelines.containsKey(name);
    }
}
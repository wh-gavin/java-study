package com.custom.valve.valves;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import com.custom.valve.core.AbstractValve;
import com.custom.valve.core.ValveChain;
import com.custom.valve.core.ValveRequest;
import com.custom.valve.core.ValveResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 缓存阀门
 * 
 * 缓存HTTP响应，减少重复计算和数据库查询
 * 支持条件缓存、ETag、缓存失效策略等
 * 
 * 使用场景：静态资源缓存、API响应缓存、热点数据缓存
 */
public class CacheValve extends AbstractValve {
    
    // 缓存存储（使用Guava Cache）
    private Cache<String, CachedResponse> cache;
    
    // 配置属性
    private long maxSize = 1000;                     // 最大缓存条目数
    private long expireAfterWrite = 300;             // 写入后过期时间（秒）
    private long expireAfterAccess = 60;             // 访问后过期时间（秒）
    
    private Set<String> cacheableMethods = new HashSet<>(Arrays.asList("GET", "HEAD"));
    private Set<String> excludePaths = new HashSet<>();
    private Set<String> includePaths = new HashSet<>();
    
    private boolean enabled = true;
    private boolean recordStats = false;             // 是否记录缓存统计
    
    /**
     * 构造函数
     */
    public CacheValve() {
        super("cacheValve");
        setOrder(50); // 较早执行，以便尽早返回缓存
        
        initializeCache();
        
        // 默认排除路径
        excludePaths.add("/api/admin/");
        excludePaths.add("/api/private/");
    }
    
    /**
     * 初始化缓存
     */
    private void initializeCache() {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS)
                .expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
        
        if (recordStats) {
            builder.recordStats();
        }
        
        this.cache = builder.build();
    }
    
    @Override
    protected void doInvoke(ValveRequest request, ValveResponse response, ValveChain chain)
            throws IOException, ServletException {
        
        // 检查是否启用
        if (!enabled) {
            chain.invokeNext(request, response);
            return;
        }
        
        // 检查是否可缓存
        if (!isCacheable(request)) {
            chain.invokeNext(request, response);
            return;
        }
        
        // 生成缓存键
        String cacheKey = generateCacheKey(request);
        
        // 尝试从缓存获取
        CachedResponse cached = cache.getIfPresent(cacheKey);
        
        if (cached != null) {
            // 检查缓存是否有效
            if (isCacheValid(cached, request)) {
                // 从缓存提供服务
                serveFromCache(cached, response);
                request.setAttribute("cache.hit", true);
                return;
            } else {
                // 缓存已过期，移除
                cache.invalidate(cacheKey);
            }
        }
        
        // 缓存未命中，使用包装器捕获响应
        CacheableResponseWrapper wrapper = new CacheableResponseWrapper(response);
        
        try {
            // 执行后续阀门
            chain.invokeNext(request, wrapper);
            
            // 如果响应成功，进行缓存
            if (shouldCacheResponse(wrapper)) {
                cacheResponse(cacheKey, wrapper, request);
            }
            
            request.setAttribute("cache.hit", false);
            
        } catch (Exception e) {
            // 异常处理
            if (!response.isCommitted()) {
                response.sendError(500, "Internal Server Error");
            }
            throw e;
        }
    }
    
    /**
     * 检查请求是否可缓存
     */
    private boolean isCacheable(ValveRequest request) {
        // 检查HTTP方法
        String method = request.getMethod();
        if (!cacheableMethods.contains(method)) {
            return false;
        }
        
        String path = request.getRequestURI();
        
        // 检查排除路径
        if (excludePaths.stream().anyMatch(path::startsWith)) {
            return false;
        }
        
        // 如果指定了包含路径，只缓存这些路径
        if (!includePaths.isEmpty() && includePaths.stream().noneMatch(path::startsWith)) {
            return false;
        }
        
        // 检查请求头（如no-cache）
        String cacheControl = request.getHeader("Cache-Control");
        if (cacheControl != null && cacheControl.contains("no-cache")) {
            return false;
        }
        
        // 检查Pragma头（HTTP/1.0兼容）
        String pragma = request.getHeader("Pragma");
        if (pragma != null && pragma.contains("no-cache")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 生成缓存键
     */
    private String generateCacheKey(ValveRequest request) {
        StringBuilder key = new StringBuilder();
        
        // 方法 + URI
        key.append(request.getMethod()).append(":").append(request.getRequestURI());
        
        // 查询参数
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            key.append("?").append(queryString);
        }
        
        // 考虑Accept头（内容协商）
        String accept = request.getHeader("Accept");
        if (accept != null) {
            key.append("|Accept:").append(accept);
        }
        
        // 考虑认证状态
        Object user = request.getAttribute("currentUser");
        if (user != null) {
            key.append("|User:").append(user.hashCode());
        }
        
        // 考虑语言偏好
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null) {
            key.append("|Lang:").append(acceptLanguage);
        }
        
        return key.toString();
    }
    
    /**
     * 检查缓存是否有效
     */
    private boolean isCacheValid(CachedResponse cached, ValveRequest request) {
        long currentTime = System.currentTimeMillis();
        
        // 检查缓存是否过期
        if (currentTime > cached.getExpireTime()) {
            return false;
        }
        
        // 检查ETag（如果请求中有If-None-Match）
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && cached.getEtag() != null) {
            return ifNoneMatch.equals(cached.getEtag());
        }
        
        // 检查Last-Modified（如果请求中有If-Modified-Since）
        String ifModifiedSince = request.getHeader("If-Modified-Since");
        if (ifModifiedSince != null && cached.getLastModified() > 0) {
            try {
                // 解析HTTP日期格式
                // 简化实现：直接比较时间戳
                long ifModifiedSinceTime = parseHttpDate(ifModifiedSince);
                return cached.getLastModified() <= ifModifiedSinceTime;
            } catch (Exception e) {
                // 忽略格式错误
                logger.debug("Failed to parse If-Modified-Since header", e);
            }
        }
        
        return true;
    }
    
    /**
     * 解析HTTP日期（简化实现）
     */
    private long parseHttpDate(String dateStr) {
        // 简化实现：返回当前时间
        // 实际项目中应该使用更完善的HTTP日期解析
        return System.currentTimeMillis();
    }
    
    /**
     * 从缓存提供服务
     */
    private void serveFromCache(CachedResponse cached, ValveResponse response) throws IOException {
        // 设置状态码
        response.setStatus(cached.getStatusCode());
        
        // 设置响应头
        cached.getHeaders().forEach((name, values) -> {
            for (String value : values) {
                response.addHeader(name, value);
            }
        });
        
        // 设置缓存相关的响应头
        response.addHeader("X-Cache", "HIT");
        response.addHeader("X-Cache-Key", cached.getCacheKey().hashCode() + "");
        
        // 设置ETag
        if (cached.getEtag() != null) {
            response.addHeader("ETag", cached.getEtag());
        }
        
        // 设置过期时间
        if (cached.getExpireTime() > 0) {
            response.addHeader("Expires", formatHttpDate(cached.getExpireTime()));
        }
        
        // 写入响应体
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(cached.getBody());
        outputStream.flush();
    }
    
    /**
     * 格式化HTTP日期（简化实现）
     */
    private String formatHttpDate(long timestamp) {
        // 简化实现：返回固定格式
        // 实际项目中应该使用HTTP日期格式
        return new Date(timestamp).toString();
    }
    
    /**
     * 检查响应是否应该被缓存
     */
    private boolean shouldCacheResponse(CacheableResponseWrapper response) {
        // 只缓存成功的响应
        int status = response.getStatus();
        if (status < 200 || status >= 300) {
            return false;
        }
        
        // 检查响应头（如no-store）
        String cacheControl = response.getHeader("Cache-Control");
        if (cacheControl != null && cacheControl.contains("no-store")) {
            return false;
        }
        
        // 检查内容类型
        String contentType = response.getContentType();
        if (contentType == null) {
            return false;
        }
        
        // 只缓存特定类型的内容
        boolean cacheableType = contentType.contains("application/json") ||
                               contentType.contains("text/") ||
                               contentType.contains("image/") ||
                               contentType.contains("application/javascript") ||
                               contentType.contains("text/css");
        
        if (!cacheableType) {
            return false;
        }
        
        // 检查响应大小（避免缓存过大内容）
        byte[] content = response.getCapturedContent();
        if (content.length > 10 * 1024 * 1024) { // 10MB
            return false;
        }
        
        return true;
    }
    
    /**
     * 缓存响应
     */
    private void cacheResponse(String cacheKey, CacheableResponseWrapper wrapper, ValveRequest request) {
        // 创建缓存响应对象
        CachedResponse cached = new CachedResponse();
        cached.setCacheKey(cacheKey);
        cached.setStatusCode(wrapper.getStatus());
        cached.setHeaders(wrapper.getHeaders());
        cached.setBody(wrapper.getCapturedContent());
        cached.setContentType(wrapper.getContentType());
        cached.setCharacterEncoding(wrapper.getCharacterEncoding());
        
        // 设置ETag（基于内容生成）
        String etag = generateETag(cached.getBody());
        cached.setEtag(etag);
        
        // 设置过期时间
        long cacheTime = getCacheTimeFromResponse(wrapper);
        cached.setExpireTime(System.currentTimeMillis() + cacheTime * 1000);
        
        // 设置最后修改时间
        cached.setLastModified(System.currentTimeMillis());
        
        // 设置创建时间
        cached.setCreateTime(System.currentTimeMillis());
        
        // 放入缓存
        cache.put(cacheKey, cached);
        
        // 添加缓存头到实际响应
        wrapper.getOriginalResponse().addHeader("X-Cache", "MISS");
        wrapper.getOriginalResponse().addHeader("ETag", etag);
        wrapper.getOriginalResponse().addHeader("Cache-Control", "max-age=" + cacheTime);
        
        // 记录缓存统计
        if (logger.isDebugEnabled()) {
            logger.debug("Response cached: {} ({} bytes)", cacheKey, cached.getBody().length);
        }
    }
    
    /**
     * 生成ETag
     */
    private String generateETag(byte[] content) {
        // 基于内容哈希生成ETag
        int hashCode = Arrays.hashCode(content);
        return "\"" + Integer.toHexString(hashCode) + "\"";
    }
    
    /**
     * 从响应头获取缓存时间
     */
    private long getCacheTimeFromResponse(CacheableResponseWrapper response) {
        String cacheControl = response.getHeader("Cache-Control");
        if (cacheControl != null) {
            // 解析max-age
            String[] parts = cacheControl.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("max-age=")) {
                    try {
                        return Long.parseLong(part.substring(8));
                    } catch (NumberFormatException e) {
                        // 忽略格式错误
                    }
                }
            }
        }
        
        // 默认缓存时间
        return expireAfterWrite;
    }
    
    // ========== 配置方法 ==========
    
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        initializeCache();
    }
    
    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        initializeCache();
    }
    
    public void setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        initializeCache();
    }
    
    public void setCacheableMethods(Set<String> cacheableMethods) {
        this.cacheableMethods = new HashSet<>(cacheableMethods);
    }
    
    public void setExcludePaths(Set<String> excludePaths) {
        this.excludePaths = new HashSet<>(excludePaths);
    }
    
    public void setIncludePaths(Set<String> includePaths) {
        this.includePaths = new HashSet<>(includePaths);
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setRecordStats(boolean recordStats) {
        this.recordStats = recordStats;
        initializeCache();
    }
    
    // ========== 管理方法 ==========
    
    /**
     * 清空缓存
     */
    public void clearCache() {
        cache.invalidateAll();
        logger.info("Cache cleared");
    }
    
    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (recordStats) {
            com.google.common.cache.CacheStats cacheStats = cache.stats();
            stats.put("hitCount", cacheStats.hitCount());
            stats.put("missCount", cacheStats.missCount());
            stats.put("loadSuccessCount", cacheStats.loadSuccessCount());
            stats.put("loadFailureCount", cacheStats.loadExceptionCount());
            stats.put("totalLoadTime", cacheStats.totalLoadTime());
            stats.put("evictionCount", cacheStats.evictionCount());
            stats.put("hitRate", cacheStats.hitRate());
            stats.put("missRate", cacheStats.missRate());
        }
        
        stats.put("size", cache.size());
        stats.put("maxSize", maxSize);
        stats.put("expireAfterWrite", expireAfterWrite);
        stats.put("expireAfterAccess", expireAfterAccess);
        
        return stats;
    }
    
    /**
     * 获取缓存大小
     */
    public long getCacheSize() {
        return cache.size();
    }
    
    /**
     * 获取缓存命中率
     */
    public double getHitRate() {
        if (recordStats) {
            return cache.stats().hitRate();
        }
        return 0.0;
    }
    
    // ========== 内部类 ==========
    
    /**
     * 可缓存的响应包装器
     */
    private static class CacheableResponseWrapper extends ValveResponse {
        
        private final ByteArrayOutputStream capturedContent = new ByteArrayOutputStream();
        private ServletOutputStream outputStream;
        private PrintWriter writer;
        private boolean outputStreamUsed = false;
        private boolean writerUsed = false;
        
        public CacheableResponseWrapper(ValveResponse response) {
            super(response.getOriginalResponse());
        }
        
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writerUsed) {
                throw new IllegalStateException("getWriter() has already been called");
            }
            
            if (outputStream == null) {
                outputStream = new CachingServletOutputStream(super.getOutputStream());
            }
            outputStreamUsed = true;
            return outputStream;
        }
        
        @Override
        public PrintWriter getWriter() throws IOException {
            if (outputStreamUsed) {
                throw new IllegalStateException("getOutputStream() has already been called");
            }
            
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(
                    new CachingOutputStream(), getCharacterEncoding()));
            }
            writerUsed = true;
            return writer;
        }
        
        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            }
            if (outputStream != null) {
                outputStream.flush();
            }
            super.flushBuffer();
        }
        
        public byte[] getCapturedContent() {
            if (writer != null) {
                writer.flush();
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    // 忽略刷新异常
                }
            }
            return capturedContent.toByteArray();
        }
        
        /**
         * 缓存输出流
         */
        private class CachingOutputStream extends OutputStream {
            @Override
            public void write(int b) throws IOException {
                capturedContent.write(b);
                getOriginalResponse().getOutputStream().write(b);
            }
            
            @Override
            public void write(byte[] b) throws IOException {
                capturedContent.write(b);
                getOriginalResponse().getOutputStream().write(b);
            }
            
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                capturedContent.write(b, off, len);
                getOriginalResponse().getOutputStream().write(b, off, len);
            }
            
            @Override
            public void flush() throws IOException {
                capturedContent.flush();
                getOriginalResponse().getOutputStream().flush();
            }
        }
        
        /**
         * 缓存Servlet输出流
         */
        private class CachingServletOutputStream extends ServletOutputStream {
            private final ServletOutputStream original;
            
            CachingServletOutputStream(ServletOutputStream original) {
                this.original = original;
            }
            
            @Override
            public void write(int b) throws IOException {
                capturedContent.write(b);
                original.write(b);
            }
            
            @Override
            public void write(byte[] b) throws IOException {
                capturedContent.write(b);
                original.write(b);
            }
            
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                capturedContent.write(b, off, len);
                original.write(b, off, len);
            }
            
            @Override
            public void flush() throws IOException {
                capturedContent.flush();
                original.flush();
            }
            
            @Override
            public void close() throws IOException {
                original.close();
            }
            
            @Override
            public boolean isReady() {
                return original.isReady();
            }
            
            @Override
            public void setWriteListener(WriteListener writeListener) {
                original.setWriteListener(writeListener);
            }
        }
    }
    
    /**
     * 缓存响应对象
     */
    private static class CachedResponse {
        private String cacheKey;
        private int statusCode;
        private Map<String, List<String>> headers = new HashMap<>();
        private byte[] body;
        private String contentType;
        private String characterEncoding = "UTF-8";
        private String etag;
        private long expireTime;
        private long lastModified;
        private long createTime;
        
        // Getter和Setter方法
        public String getCacheKey() { return cacheKey; }
        public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }
        
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        
        public Map<String, List<String>> getHeaders() { return new HashMap<>(headers); }
        public void setHeaders(Map<String, List<String>> headers) { this.headers = new HashMap<>(headers); }
        
        public byte[] getBody() { return body != null ? body.clone() : new byte[0]; }
        public void setBody(byte[] body) { this.body = body != null ? body.clone() : new byte[0]; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public String getCharacterEncoding() { return characterEncoding; }
        public void setCharacterEncoding(String characterEncoding) { this.characterEncoding = characterEncoding; }
        
        public String getEtag() { return etag; }
        public void setEtag(String etag) { this.etag = etag; }
        
        public long getExpireTime() { return expireTime; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
        
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
        
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
    }
}
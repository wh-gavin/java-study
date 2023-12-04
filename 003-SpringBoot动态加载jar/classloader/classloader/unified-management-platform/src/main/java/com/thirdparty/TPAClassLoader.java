package com.thirdparty;

import cn.hutool.core.lang.Console;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 为加载三方平台 A 提供的 Jar 自定义的类加载器
 *
 * @since 2023/1/14 10:02
 */
public class TPAClassLoader extends URLClassLoader {

    /**
     * 用于缓存相应平台的类加载器，防止重复创建和加载类，造成内存泄漏
     */
    private static final ConcurrentMap<String, TPAClassLoader> CLASS_LOADER_CACHE = new ConcurrentHashMap<>();


    private TPAClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * 用于获取相应三方平台 Jar 包中的类，如果已经加载直接返回，未加载通过 TAPClassLoader 加载类，完成后返回
     *
     * @param internalPlatformCode 内部平台编码，例如：内部平台 A 的编码就是 A
     * @param tapJarPath           为相应内部平台定制的三方平台 Jar 路径
     * @param className            待获取类的全限定类名
     * @return 类的 Class 对象
     */
    @SneakyThrows
    public static Class<?> getClass(String internalPlatformCode, String tapJarPath, String className) {
        TPAClassLoader classLoader = getInstance(internalPlatformCode, tapJarPath);
        Console.log("获取内部平台 {} 的类：{}", internalPlatformCode, className);
        return classLoader.loadClass(className);
    }

    /**
     * 用于获取对应内部平台的类加载器，类加载器相对于内部平台是单例的，保证单例使用单例设计模式 DCL 的方式
     *
     * @param internalPlatformCode 内部平台编码，例如：内部平台 A 的编码就是 A
     * @param tapJarPath           为相应内部平台定制的三方平台 Jar 路径
     * @return 内部平台对应的类加载器
     */
    private static TPAClassLoader getInstance(String internalPlatformCode, String tapJarPath) throws Exception {
        final String key = buildKey(internalPlatformCode, tapJarPath);
        TPAClassLoader classLoader = CLASS_LOADER_CACHE.get(key);
        if (classLoader != null) {
            return classLoader;
        }
        synchronized (TPAClassLoader.class) {
            classLoader = CLASS_LOADER_CACHE.get(key);
            if (classLoader != null) {
                return classLoader;
            }

            File jarFile = new File(tapJarPath);
            if (!jarFile.exists()) {
                throw new FileNotFoundException("未找到三方平台 A Jar 包文件：" + tapJarPath);
            }
            classLoader = new TPAClassLoader(new URL[]{jarFile.toURI().toURL()}, getSystemClassLoader());
            Console.log("为内部平台 {} 创建类加载器：{}", internalPlatformCode, classLoader);
            CLASS_LOADER_CACHE.put(key, classLoader);
            
            return classLoader;
        }
    }

    /**
     * 用于生成缓存对应内部平台类加载器的 Key
     *
     * @param internalPlatformCode 内部平台编码，例如：内部平台 A 的编码就是 A
     * @param tapJarPath           为相应内部平台定制的三方平台 Jar 路径
     * @return 缓存 Key
     */
    private static String buildKey(String internalPlatformCode, String tapJarPath) {
        return internalPlatformCode.concat("::").concat(tapJarPath);
    }
}
package com.thirdparty;


import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

/**
 * Main
 *
 * @author ZhaoHaichun
 * @since 2023/1/14 10:34
 */
public class Main {

    /**
     * 该 Map 只是测试使用，用于临时保持三方平台 A 提供的 Jar 包路径，实际开发会通过文件上传到服务器，然后获取上传路径，通过路径加载
     */
    private static final Map<String, String> TPA_JAR_PATH_MAP = new HashMap<>();

    private static final String TAP_ACCESS_SERVICE_NAME = "com.thirdparty.TPAAccessService";


    static {
        TPA_JAR_PATH_MAP.put("A", "D:\\workspace\\java-study\\003-SpringBoot动态加载jar\\classloader\\classloader\\third-party-A\\target\\third-party-A-0.0.1-SNAPSHOT.jar");
        TPA_JAR_PATH_MAP.put("B", "D:\\workspace\\java-study\\003-SpringBoot动态加载jar\\classloader\\classloader\\third-party-B\\target\\third-party-B-0.0.1-SNAPSHOT.jar");
    }


    @SneakyThrows
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            // 用于随机生成待访问的内部平台
            String internalPlatformCode = String.valueOf((char) RandomUtil.randomInt('A', 'B' + 1));
            // 通过访问的内部平台查询三方平台 A 为其提供的 Jar 路径
            String jarPath = TPA_JAR_PATH_MAP.get(internalPlatformCode);
            // 通过上述信息，使用相应的类加载器加载或直接获取类 "com.thirdparty.TPAAccessService"
            Class<?> clazz = TPAClassLoader.getClass(internalPlatformCode, jarPath, TAP_ACCESS_SERVICE_NAME);
            // 调用其相应的方法
            ReflectUtil.invokeStatic(clazz.getMethod("send"));
            Console.log("================================================================");
        }
    }
}
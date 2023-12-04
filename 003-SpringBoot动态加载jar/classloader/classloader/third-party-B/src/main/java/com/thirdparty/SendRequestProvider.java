package com.thirdparty;

import cn.hutool.core.lang.Console;

/**
 * 该类提供向三方平台 A 发送请求的方法
 *
 * @since 2023/1/14 9:48
 */
class SendRequestProvider {

    /**
     * 三方平台 A 为内部平台 A 预设的密钥，用于加解密
     */
    private static final String SECRET_KEY = "BBBBBBBBBBB";


    /**
     * 发送请求到三方平台 A
     */
    public static void send() {
        Console.log("[B -> TPA] 密钥：{}   ClassLoader:{}", SECRET_KEY, SendRequestProvider.class.getClassLoader());
    }
}

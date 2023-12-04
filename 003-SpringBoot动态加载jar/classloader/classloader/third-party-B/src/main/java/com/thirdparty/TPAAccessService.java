package com.thirdparty;

/**
 * TPA（Third Party A：三方平台 A 简称）
 * 该类为调用方提供统一的方法调用入口，调用三方 A 只需要使用该类即可
 *
 * @since 2023/1/14 9:45
 */
public class TPAAccessService {

    public static void send() {
        SendRequestProvider.send();
    }
}

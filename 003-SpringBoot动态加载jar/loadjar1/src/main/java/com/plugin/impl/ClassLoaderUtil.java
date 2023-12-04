package com.plugin.impl;


import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassLoaderUtil {
    public static ClassLoader getClassLoader(String url) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            //url = URLEncoder.encode(url);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
            method.invoke(classLoader, new URL(url));
            return classLoader;
        } catch (Exception e) {
            log.error("getClassLoader-error", e);
            return null;
        }
    }
}
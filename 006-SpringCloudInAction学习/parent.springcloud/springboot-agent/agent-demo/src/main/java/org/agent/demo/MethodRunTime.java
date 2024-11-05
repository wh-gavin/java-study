package org.agent.demo;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class MethodRunTime {
	 @RuntimeType
	 public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
    	//public static Object intercept(@AllArguments Object[] args, @Origin Method method, @SuperCall Callable<?> callable) throws Exception {
		long start = System.currentTimeMillis();
		Object result = null;
		try {
			// 原有函数执行
			result = callable.call();
		} finally {
			System.out.println(method + " 方法耗时：" + (System.currentTimeMillis() - start) + "ms");
		}
		return result;
	}
}

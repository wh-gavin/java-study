package org.itstack.demo.bytecode.j02;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class HelloWorld {
	private static void outputClazz(byte[] bytes) {
	    FileOutputStream out = null;
	    try {
	        String pathName = Hi.class.getResource("/").getPath() + "ByteBuddyHelloWorld.class";
	        out = new FileOutputStream(new File("D://ByteBuddyHelloWorld.class"));
	        System.out.println("类输出路径：" + pathName);
	        out.write(bytes);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (null != out) try {
	            out.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		String helloWorld = new ByteBuddy()
	            .subclass(Object.class)
	            .method(ElementMatchers.named("toString"))
	            .intercept(FixedValue.value("Hello World!"))
	            .make()
	            .load(HelloWorld.class.getClassLoader())
	            .getLoaded()
	            .newInstance()
	            .toString();    

		


		
		System.out.println(helloWorld);  // Hello World!

	
		Class<?> dynamicType  = new ByteBuddy()
	            .subclass(Object.class)
	            .method(ElementMatchers.named("toString"))
	            .intercept(FixedValue.value("Hello World!"))
	            .make()
	            .load(HelloWorld.class.getClassLoader())
	            .getLoaded();    
		
		Object instance = dynamicType.newInstance();
		String toString = instance.toString();
		System.out.println(toString);
		System.out.println(instance.getClass().getCanonicalName());
		
		
		DynamicType.Unloaded<?> dynamicType1 = new ByteBuddy()
		        .subclass(Object.class)
		        .name("org.itstack.demo.bytebuddy.HelloWorld")
		        .defineMethod("main", void.class, Modifier.PUBLIC + Modifier.STATIC)
		        .withParameter(String[].class, "args")
		        .intercept(MethodDelegation.to(Hi.class))
		        .make();
		 

		// 输出类字节码
		outputClazz(dynamicType1.getBytes());

	}
}

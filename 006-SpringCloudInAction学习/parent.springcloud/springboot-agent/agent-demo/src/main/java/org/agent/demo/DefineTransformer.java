package org.agent.demo;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class DefineTransformer implements ClassFileTransformer {
//    @Override
//    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//        System.out.println("premain load class"+className); //打印加载的类
//        return new byte[0];
//    }

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		System.out.println("premain load class"+className); //打印加载的类
		if ("org/itstack/demo/test/A".equals(className)) {

			try {
				ClassPool classPool = ClassPool.getDefault();
				CtClass ctClass = classPool.get("org.itstack.demo.test.A");
				CtMethod call = ctClass.getDeclaredMethod("call");
				// 打印后加了一个弹计算器的操作
				String MethodBody = "{System.out.println(\"say hello ...\");"
						+ "java.lang.Runtime.getRuntime().exec(\"open -a Calculator\");}";
				call.setBody(MethodBody);
				byte[] bytes = ctClass.toBytecode();

				// detach的意思是将内存中曾经被javassist加载过的a对象移除，如果下次有需要在内存中找不到会重新走javassist加载
				ctClass.detach();
				return bytes;

			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (CannotCompileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new byte[0];
	}
}

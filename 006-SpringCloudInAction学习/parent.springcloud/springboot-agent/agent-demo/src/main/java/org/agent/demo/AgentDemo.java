package org.agent.demo;

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

/**
 * Hello world!
 *
 */
public class AgentDemo {
	private final static String TARGET_CLASS = "org.main.demo.MainDemo";
	
	public static void premain(String agentArgs, Instrumentation inst) throws Exception {
		System.out.println("premain(String agentArgs, Instrumentation inst)");
		System.out.println("参数:" + agentArgs);
		System.out.println("参数:" + inst);

		//inst.addTransformer(new DefineTransformer(),true);
		// inst.addTransformer(new DefineTransformer(),true);//调用addTransformer添加一个Transformer
        
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> {
            return builder
            		.method(ElementMatchers.any()) 
                    .intercept(MethodDelegation.to(MethodRunTime.class)); // 委托
        };

        AgentBuilder.Listener listener = new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            	//System.out.println("s=" + s + ",b=" + b);
            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
            	System.out.println("onTransformation - {}" + typeDescription);
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            	
            }

            @Override
            public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
            	System.out.println("error=" + s + ",b=" + b);
            }

            @Override
            public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            	System.out.println("onComplete=" + s + ",b=" + b);
            }

        };

		new AgentBuilder
				.Default()
				//.type(ElementMatchers.nameStartsWith("org.main.demo")) // 指定需要拦截的类
				.type(ElementMatchers.named(TARGET_CLASS))
				//.and(ElementMatchers.isAnnotatedWith(Service.class))
				.transform(transformer)
				.with(listener)
				.installOn(inst);
	}

	public static void premain(String agentArgs) throws Exception {
		System.out.println("premain(String agentArgs)");
		System.out.println("参数:" + agentArgs);
	}
//    static class DefineTransformer implements ClassFileTransformer {
//
//        @Override
//        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//            System.out.println("premain load Class:" + className);
//            return classfileBuffer;
//        }
//    }
}
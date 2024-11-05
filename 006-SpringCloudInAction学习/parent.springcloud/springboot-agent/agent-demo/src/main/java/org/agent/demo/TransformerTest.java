package org.agent.demo;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class TransformerTest implements ClassFileTransformer {
    public final String TEST_CLASS_NAME = "com.bilibili.test.javaweb.HellofoolController";

    public final String METHOD_NAME = "test";


    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        String finalClassName = className.replace("/", ".");

        if (TEST_CLASS_NAME.equals(finalClassName)) {
            System.err.println("启动中执行");

            CtClass ctClass;

            try {
                ctClass = ClassPool.getDefault().get(finalClassName);
                System.err.println("启动中执行1");
                CtMethod ctMethod = ctClass.getDeclaredMethod(METHOD_NAME);
                System.err.println("启动中执行2");
                ctMethod.insertBefore("System.err.println(\"通过javaAgebt插入了代码\");");
                ctMethod.insertBefore("text = \"text被javaAgebt篡改了\";");
                return ctClass.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        return null;
    }
}

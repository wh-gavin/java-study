package springboot.loadjar1.test1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MyClassLoaderCustom extends ClassLoader{

    private Map<String, String> classPathMap = new HashMap<>();

    public MyClassLoaderCustom(ClassLoader jdkClassLoader) {
        classPathMap.put("springboot.loadjar1.test1.TestA",
                "D:\\workspace\\java-study\\003-SpringBoot动态加载jar\\classloader\\classloader\\loadclass\\bin\\springboot\\loadjar1\\test1/TestA.class");
        classPathMap.put("springboot.loadjar1.test1.TestB",
                "D:\\workspace\\java-study\\003-SpringBoot动态加载jar\\classloader\\classloader\\loadclass\\bin\\springboot\\loadjar1\\test1/TestB.class");
        this.jdkClassLoader = jdkClassLoader;
    }
    private ClassLoader jdkClassLoader;

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class result = null;
        try {
            //这里先使用jdkClassLoader来加载jdk自带的类
            result = jdkClassLoader.loadClass(name);
        } catch (Exception e) {
            //忽略
        }
        if (result != null) {
            return result;
        }
        // jdkClassLoader找不到类时，就自行加载
        String classPath = classPathMap.get(name);
        File file = new File(classPath);
        if (!file.exists()) {
            throw new ClassNotFoundException();
        }

        byte[] classBytes = getClassData(file);
        if (classBytes == null || classBytes.length == 0) {
            throw new ClassNotFoundException();
        }
        return defineClass(name, classBytes, 0, classBytes.length);
    }

    private byte[] getClassData(File file) {
        try (InputStream ins = new FileInputStream(file); ByteArrayOutputStream baos = new
                ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesNumRead = 0;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[] {};
    }
}

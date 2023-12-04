package springboot.loadjar1.test1;

import java.lang.reflect.Method;

public class MyTest {

	
    public static void main(String[] args) throws Exception {
    	
        // 这里取AppClassLoader的父加载器也就是ExtClassLoader
        // 作为MyClassLoaderCustom的成员jdkClassLoader
        MyClassLoaderCustom myClassLoaderCustom = new MyClassLoaderCustom(Thread.currentThread().getContextClassLoader().getParent());
        Class testA = myClassLoaderCustom.loadClass("springboot.loadjar1.test1.TestA");
        Method testAmainMethod = testA.getDeclaredMethod("main", String[].class);
        testAmainMethod.invoke(null, new Object[] {args});

    	// 这里用自定义类加载器来加载TestA
        MyClassLoader myClassLoader = new MyClassLoader();
        Class testAClass = myClassLoader.findClass("springboot.loadjar1.test1.TestA");
        Method mainMethod = testAClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, new Object[] {args});
    }
}

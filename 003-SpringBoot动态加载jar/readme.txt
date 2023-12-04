springboot动态加载第三方jar包，可随时卸载和添加jar包
--https://www.5axxw.com/questions/simple/0n7s60


SpringBoot动态加载jar包
--https://www.finclip.com/news/f/38462.html

--https://blog.51cto.com/u_16213559/7517469

https://pan.baidu.com/s/14JOLJiwQJWieOWgaj8V2BA?pwd=wiux

ClassLoaderUtil 类
public class ClassLoaderUtil {
    public static ClassLoader getClassLoader(String url) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            URLClassLoader classLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
            method.invoke(classLoader, new URL(url));
            return classLoader;
        } catch (Exception e) {
            log.error("getClassLoader-error", e);
            return null;
        }
    }
}
https://blog.csdn.net/qq_45584746/article/details/130501254
Java-springboot动态加载jar包，动态配置

https://blog.51cto.com/u_16213559/7517469
https://mp.weixin.qq.com/s/Fg-jsoFon5LwsPAaBbeiew
Spring Boot 如何热加载jar实现动态插件？

https://blog.csdn.net/zhangtao0417/article/details/125164873
SpringBoot第十二篇：热加载第三方jar包（解决嵌套jar读取、加载、动态配置、bean注册、依赖等问题），及其精髓

https://blog.csdn.net/weixin_42263951/article/details/122862081?spm=1001.2101.3001.6650.16&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-16-122862081-blog-125164873.235%5Ev38%5Epc_relevant_sort_base3&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-16-122862081-blog-125164873.235%5Ev38%5Epc_relevant_sort_base3&utm_relevant_index=22
一种基于Spring Boot实现的支持热插拔的插件化方案


https://blog.csdn.net/Appleyk/article/details/128166621?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-0-128166621-blog-125164873.235^v38^pc_relevant_sort_base3&spm=1001.2101.3001.4242.1&utm_relevant_index=3
SpringBoot应用项目插件开发☞Jar包热更新
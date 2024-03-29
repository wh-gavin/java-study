package com.plugin.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RestController;

import com.plugin.impl.utils.SpringBeanUtils;

import lombok.extern.slf4j.Slf4j;
 
/**
 * <p>自定义类加载器，主要用来加载指定目录下的所有以.jar结尾的文件</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on  下午9:16 2022/11/23
 */
@Slf4j
public class HotClassLoader extends URLClassLoader {
 
    /**设定插件默认放置的路径*/
    private static final String PLUGINS_DIR = "classpath:plugins";
    /**jar更新时间键值对，通过value来判断jar包是否被修改了*/
    private static final Map<String,Long> jarUpdateTime;
    /**jar包对应的类加载器，即1个jar包对应1个类加载器，但是1个类加载器可以对应N个jar包*/
    private static final Map<String,HotClassLoader> jarClassLoaders;
    /**jar包中类的完全限定名键值对，即1个jar包包含N个class*/
    private static final Map<String, List<String>> jarClassName;
 
    static {
        jarUpdateTime = new HashMap<>(16);
        jarClassLoaders = new HashMap<>(16);
        jarClassName  = new HashMap<>(16);
    }
 
    public HotClassLoader(ClassLoader parent) {
        super(new URL[0],parent);
    }
 
    /**
     * 一次性加载plugins目录下的所有jar（这个可以放在定时扫描中，n秒执行一次）
     * 但是前提必须是jar包有更新，也就是第一次是全量加载，后面扫描只会基于更新的jar做热替换
     */
    public static void loadAllJar() throws Exception{
        File file = null;
        /** 首先先判断classpath下plugins是否存在，如果不存在，帮用户创建 */
        try{
            file = ResourceUtils.getFile(PLUGINS_DIR);
        }catch (Exception e){
            String classesPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
            String pluginsDir = classesPath+"plugins";
            file = new File(pluginsDir);
            if (!file.exists()){
                /**不存在就创建*/
                file.mkdirs();
            }
        }
 
        /** 如果存在，遍历目录下面的所有子文件对象*/
        File[] files = file.listFiles();
        if (files == null || files.length == 0){
            log.info("no plugins resource need loading...");
            return;
        }
        List<String> updatedJars = new ArrayList<>();
        for (File childFile : files) {
            String name = childFile.getName();
            /**如果子文件对象是文件夹，则不处理*/
            if (childFile.isDirectory()){
                log.warn("not support the folder of " + name);
                continue;
            }
            /**如果文件不以jar结尾，也不处理*/
            if (!name.endsWith(".jar")){
                log.warn("not support the plugin file of " + name);
                continue;
            }
            /**构建jar类路径*/
            String jarPath = String.format("%s/%s",PLUGINS_DIR,name);
            long lastModifyTime = childFile.getAbsoluteFile().lastModified();
            if (Objects.equals(lastModifyTime,jarUpdateTime.get(jarPath))){
                continue;
            }
            /**将修改过的jar路径保存起来s*/
            System.out.println(String.format("%s changed, need to reload",jarPath));
            updatedJars.add(jarPath);
        }
 
        if (updatedJars.size() == 0){
            System.out.println("There are no Jars to update !");
            return;
        }
 
        /**
         * 如果本次扫描发现有jar包更新，则从ioc容器里取出新的classLoader实例以加载这些jar包中的class
         * 这个地方很巧妙。即同一批次更新的jar包会使用用同一个类加载器去加载，这就避免了类加载器不会平白无故的多出很多！
         * 为什么这里要重新加载呢？我们知道，判断类对象在JVM中是否独有一份并不取决于它的完全限定名，即com.appleyk.xxx
         * 是唯一的，还取决于把它载入JVM内存中的类加载器是不是同一个，也就是同样是User.class,我们可以让它在JVM中存在多份，
         * 这个只需要用不同的用户自定义类加载器实例去loadClass即可实现，话又说回来，如果不是需要热更新正常情况下我们肯定不会这么做的！
         * 这里使用新的类加载对象去加载这一批更新的jar包的目的就是实现Class的热卸载和热替换。
         * 具体怎么做的，可以细看loadJar方法的实现，最好一边调试一边看，效果最佳！
         */
        HotClassLoader classLoader = SpringBeanUtils.getBean(HotClassLoader.class);
        for (String updatedJar : updatedJars) {
            loadJar(updatedJar,classLoader);
        }
    }
 
    /**
     * 使用指定的类加载加载单个jar文件中的所有class文件到JVM中，同时向Spring IOC容器中注入BD
     * @param jarPath jar类路径，格式如：classpath:plugins/xxxx.jar
     * @param classLoader 类加载器
     */
    public static void loadJar(String jarPath,HotClassLoader classLoader) throws Exception{
        /**先尝试从jar更新时间map中取出jarPath的更新时间*/
        Long lastModifyTime = jarUpdateTime.get(jarPath);
        /**如果等于0L,说明这个jar包还处于加载中，直接退出*/
        if (Objects.equals(lastModifyTime,0L)){
            log.warn("HotClassLoader.loadJar loading ,please not repeat the operation, jarPath = {}", jarPath);
            return;
        }
 
        /**拿到jar文件对象*/
        File file = jarPath.startsWith("classpath:") ? ResourceUtils.getFile(jarPath) : new File(jarPath);
        /**为了保险，还是判断下jarPath（有可能是外部传进来的非法jarPath）是否存在*/
        if (!file.exists()) {
            log.warn("HotClassLoader.loadJar fail file not exist, jarPath = {}", jarPath);
            return;
        }
 
        /**获取真实物理jarPath文件的修改时间*/
        long currentJarModifyTime = file.getAbsoluteFile().lastModified();
        /**如果通过对比发现jar包没有做任何修改，则不予重新加载，退出*/
        if(Objects.equals(lastModifyTime,currentJarModifyTime)){
            log.warn("HotClassLoader.loadJar current version has bean loaded , jarPath = {}", jarPath);
            return;
        }
 
        /**获取新的类加载器*/
        if (classLoader == null){
            classLoader = SpringBeanUtils.getBean(HotClassLoader.class);
        }
 
        /**
         * 如果jar包做了修改，则进行卸载流程
         * 用户自定义类加载器加载出来的Class被JVM回收的三个苛刻条件分别是：
         * 1、Class对应的所有的实例在JVM中不存在，即需要手动设置clzInstance = null;
         * 2、加载该类的ClassLoader在JVM中不存在，即需要手动设置classLoader = null;
         * 3、Class对象没有在任何地方被引用，比如不能再使用反射API，即需要手动设置class = null;
         */
        if (jarUpdateTime.containsKey(jarPath)){
            unloadJar(jarPath);
        }
 
        /**保存或更新当前jarPath的类加载器*/
        jarClassLoaders.put(jarPath,classLoader);
 
        try {
            if (jarPath.startsWith("classpath:")) {
                classLoader.addURL(new URI(jarPath).toURL());
            } else {
                classLoader.addURL(file.toURI().toURL());
            }
 
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("通过url 添加 jar 失败");
        }
 
        /**开始（重新）加载前，初始化jarPth的更新时间为0*/
        jarUpdateTime.put(jarPath, 0L);
 
        List<String> classNameList = new ArrayList<>();
        /** 遍历 jar 包中的类 */
        try (JarFile jarFile = new JarFile(file.getAbsolutePath())) {
            List<JarEntry> jarEntryList = jarFile.stream().sequential().collect(Collectors.toList());
            for (JarEntry jarEntry : jarEntryList) {
                String jarName = jarEntry.getName();
                if (!jarName.endsWith(".class")) {
                    continue;
                }
                /**类的完全限定名处理*/
                String className = jarName.replace(".class", "").replace("/", ".");
                boolean beanExist = SpringBeanUtils.contains(className);
                /**如果存在，更新*/
                if(beanExist){
                    SpringBeanUtils.removeBean(className);
                }
                /**使用指定的类加载器加载该类*/
                Class<?> clz = classLoader.loadClass(className, false);
 
                /**
                 * 这个地方要反射一下，判断下，clazz上是否有注解（@Service、@Component等）
                 * 并不是所有的类都要注入到spring ioc容器中
                 */
                boolean withBean =
                        AnnotationUtils.findAnnotation(clz, Service.class) != null
                                || AnnotationUtils.findAnnotation(clz, Component.class) != null;
                if (withBean){
                    /**将class包装成BeanDefinition注册到Spring容器中*/
                    SpringBeanUtils.registerBean(className, clz);
                    /**
                     * 动态替换bean，这个地方从常用的角度来看，我们只需处理@Controller类，
                     * 给@AutoWired修饰的类字段做替换即可
                     */
                    doAutowired(className, clz);
                }
                classNameList.add(className);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("jar包解析失败");
        }
 
        /** 记录jarPath包含的所有的类 */
        jarClassName.put(jarPath, classNameList);
 
        /** 记录jarPath的更新时间 */
        jarUpdateTime.put(jarPath, currentJarModifyTime);
    }
 
    /**卸载指定jar*/
    private static void unloadJar(String jarPath) throws Exception{
        /** 校验文件是否存在*/
        File file =  ResourceUtils.getFile(jarPath);
        if (!file.exists()) {
            log.warn("HotClassLoader.loadJar fail file not exist, jarPath = {}", jarPath);
            return;
        }
        List<String> classNameList = jarClassName.get(jarPath);
        if(CollectionUtils.isEmpty(classNameList)){
            log.warn("HotClassLoader.loadJar fail,the jar no class, jarPath = {}", jarPath);
            return;
        }
 
        HotClassLoader oldClassLoader = jarClassLoaders.get(jarPath);
        /** 遍历移除spring中对应的bean,移除引用 */
        for (String className : classNameList) {
            boolean beanExist = SpringBeanUtils.contains(className);
            if(beanExist){
                /**把旧的类实例移除，切断对象引用*/
                SpringBeanUtils.removeBean(className);
            }
            /**把旧的类加载器加载的Class对象置为null*/
            Class<?> oldClz = oldClassLoader.loadClass(className, false);
            oldClz = null;
        }
        /** 移除jarPath */
        jarUpdateTime.remove(jarPath);
        /**关闭类加载，然后切断引用*/
        if (oldClassLoader!=null){
            oldClassLoader.close();
            oldClassLoader = null;
        }
    }
 
    /**
     * 处理bean的自动注入（手动）
     * 这一块代码逻辑稍显复杂，但是还好，有spring源码基础的小伙伴一定不陌生！
     * 这块的逻辑思路主要是借鉴了nacos的源码：
     * nacos不仅是配置中心还是服务注册与发现中心，其作为配置中心的时候，我们知道，
     * 项目中的Bean类中只要使用了@NacosValue注解去修饰属性字段，那么，一旦我们在
     * nacos的web端修改了指定配置属性字段的值并保存后，那么项目端无需重启，
     * 就可以获取到最新的配置值，它是怎么做到的呢？ 首先抛开tcp连接不说，就说更新这块，
     * 那必然是先通过网络请求拿到nacos数据库中最新的配置值（值改变了会触发回调），然后
     * 找到这个字段所在的bean，然后再定位到bean实例的属性字段，然后通过反射set新值，
     * 也就是内存中保存的是旧值，然后运维或开发人员在nacos端修改了某项配置值，
     * 然后会通知App端进行值更新，App端获取到新的值后，会找到该值所在的beans，
     * 然后通过反射修改这些beans中的这个字段的值，修改成功后，内存中的旧值就被“热替换”了！
     */
    private static void doAutowired(String className,Class clz){
        Map<String, Object> beanMap = SpringBeanUtils.getBeanMap(RestController.class);
        if (beanMap == null || beanMap.size() == 0){
            return;
        }
        /**拿到clz的接口*/
        Class[] clzInterfaces = clz.getInterfaces();
        beanMap.forEach((k,v)->{
            Class<?> cz = v.getClass();
            /**拿到class所有的字段（private，protected，public，但不包括父类的）*/
            Field[] declaredFields = cz.getDeclaredFields();
            if (declaredFields == null || declaredFields.length == 0){
                return;
            }
            /**遍历字段，只处理@Autowired注解的字段值的注入*/
            for (Field declaredField : declaredFields) {
                if (!declaredField.isAnnotationPresent(Autowired.class)){
                    return;
                }
                /**推断下字段类型是否是接口（如果是接口的话，注入的条件稍显"复杂"些）*/
                boolean bInterface = declaredField.getType().isInterface();
                /**拿到字段的类型完全限定名*/
                String fieldTypeName = declaredField.getType().getName();
 
                /**设置字段可以被修改，这一版本，先不考虑多态bean的情况，下一个版本完善时再考虑*/
                declaredField.setAccessible(true);
                try{
                    /**如果字段的类型非接口并且字段的类的完全限定名就等于clz的名，那就直接setter设置*/
                    if (!bInterface && fieldTypeName == clz.getName()){
                        declaredField.set(v,SpringBeanUtils.getBean(className,clz));
                    }
                    /**如果字段类型是接口，还得判断下clz是不是实现了某些接口，如果是，得判断两边接口类型是否一致才能注入值*/
                    if (bInterface){
                        if (clzInterfaces !=null || clzInterfaces.length > 0){
                            for (Class inter : clzInterfaces) {
                                if (fieldTypeName == inter.getName()){
                                    declaredField.set(v,SpringBeanUtils.getBean(className,clz));
                                    break;
                                }
                            }
                        }
                    }
                }catch (IllegalAccessException e){
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }
 
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if(name.startsWith("java.")){
            return ClassLoader.getSystemClassLoader().loadClass(name);
        }
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            if (resolve) {
                return loadClass(name);
            }
            return clazz;
        }
        return super.loadClass(name, resolve);
    }
 
}
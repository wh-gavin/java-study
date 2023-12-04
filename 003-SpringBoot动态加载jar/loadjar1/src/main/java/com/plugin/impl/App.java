package com.plugin.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Hello world!
 *
 */
@SpringBootApplication
//@Import(PluginJarRegister.class)
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

//	public static void main(String[] args) {
//		SpringApplication application = new SpringApplication(App.class);
//		application.addListeners(new ApplicationListener<ApplicationEvent>() {
//			@Override
//			public void onApplicationEvent(ApplicationEvent event) {
//				if (event instanceof ApplicationStartingEvent) {
//					try {
//						URLClassLoader classLoader = (URLClassLoader) App.class.getClassLoader();
//						Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//						method.setAccessible(true);
//						File libDir = new File("lib");
//						File[] jarFiles = libDir.listFiles(new FilenameFilter() {
//							@Override
//							public boolean accept(File dir, String name) {
//								return name.endsWith(".jar");
//							}
//						});
//						if (jarFiles != null) {
//							for (File jarFile : jarFiles) {
//								method.invoke(classLoader, jarFile.toURI().toURL());
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		application.run(args);
//	}
}

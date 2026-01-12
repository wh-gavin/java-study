package com.custom.life;

import com.custom.life.core.LifecycleListener;
import com.custom.life.core.LoggingLifecycleListener;

/**
 * 测试主类
 */
public class TomcatLifecycleDemo {
    
    public static void main(String[] args) {
        try {
            // 创建日志监听器
            LifecycleListener loggingListener = new LoggingLifecycleListener();
            
            // 构建Tomcat容器层次结构
            StandardServer server = new StandardServer();
            server.addLifecycleListener(loggingListener);
            
            StandardService service = new StandardService("Catalina");
            service.addLifecycleListener(loggingListener);
            server.addChild(service);
            
            StandardEngine engine = new StandardEngine("Catalina");
            engine.addLifecycleListener(loggingListener);
            service.addChild(engine);
            
            StandardHost host = new StandardHost("localhost");
            host.addLifecycleListener(loggingListener);
            engine.addChild(host);
            
            StandardContext app1 = new StandardContext("app1", "/webapps/app1");
            app1.addLifecycleListener(loggingListener);
            host.addChild(app1);
            
            StandardContext app2 = new StandardContext("app2", "/webapps/app2");
            app2.addLifecycleListener(loggingListener);
            host.addChild(app2);
            
            System.out.println("========== 启动过程 ==========");
            server.start();
            
            System.out.println("\n当前服务器状态: " + server.getStateName());
            
            Thread.sleep(1000);
            
            System.out.println("\n========== 停止过程 ==========");
            server.stop();
            
            System.out.println("\n当前服务器状态: " + server.getStateName());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
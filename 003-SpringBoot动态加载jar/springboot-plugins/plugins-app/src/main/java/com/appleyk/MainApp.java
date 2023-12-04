package com.appleyk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>主应用</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on 2022/11/23-9:22
 */
@SpringBootApplication
@EnableScheduling
public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class,args);
    }
}

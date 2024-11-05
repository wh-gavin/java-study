package org.spring.bean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
		RestClientUtils rest = context.getBean("RestClientUtils", RestClientUtils.class);
		rest.test();
	}
}
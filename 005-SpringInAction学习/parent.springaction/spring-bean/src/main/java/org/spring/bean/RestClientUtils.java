package org.spring.bean;


public class RestClientUtils {
	
	private String tp;
	RestClientUtils() {
		System.out.println(tp);
	}
	public void init() {
		System.out.println("init=" + tp);
	}
	public void test() {
		System.out.println("test");
	}
	public String getTp() {
		return tp;
	}
	public void setTp(String tp) {
		this.tp = tp;
	}
	
}

package com.bilibili.test.javaweb;

public class HellofoolController {
	
	   String text = "我是正确的数据";

	    public String test(){
	        System.out.println("这里会被插入代码==");
	        return text;
	    }

	    public String test1(){
	        return text;
	    }
		private void echoHi() throws InterruptedException {
			System.out.println("hi agent==");
			Thread.sleep((long) (Math.random() * 500));
		}
	
	public static void main(String[] args) throws Exception {
		HellofoolController apiTest = new HellofoolController();
		apiTest.test();
	}


}

package org.main.demo;

/**
 * Hello world!
 *
 */
public class MainDemo {

	public static void main(String[] args) throws Exception {
		MainDemo apiTest = new MainDemo();
		apiTest.call();
		System.out.println("MainDemo end");
	}
	
	private void call() throws InterruptedException {
		System.out.println("hi call==");
		Thread.sleep((long) (Math.random() * 500));
	}
	
	private void echoHi() throws InterruptedException {
		System.out.println("hi agent==");
		Thread.sleep((long) (Math.random() * 500));
	}

}
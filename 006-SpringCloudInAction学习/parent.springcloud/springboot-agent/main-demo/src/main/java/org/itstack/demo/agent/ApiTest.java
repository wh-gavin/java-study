package org.itstack.demo.agent;

/**
 * 博客：http://itstack.org
 * 论坛：http://bugstack.cn
 * 公众号：bugstack虫洞栈  ｛获取学习源码｝
 * create by fuzhengwei on 2019
 *
 * VM options：
 * -javaagent:E:\itstack\GIT\itstack.org\itstack-demo-agent\itstack-demo-agent-02\target\itstack-demo-agent-02-1.0.0-SNAPSHOT.jar=testargs
 *
 */
public class ApiTest {

    public static void main(String[] args) {
        ApiTest apiTest = new ApiTest();
        apiTest.echoHi();
        apiTest.test("xzw");
    }

    private void echoHi(){
        System.out.println("hi agent");
    }

    private void test(String name) {
    	System.out.println(name);
    }
}

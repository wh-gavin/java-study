package org.agent.demo;


import java.lang.instrument.Instrumentation;

public class JavaAgentTest {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("==============启动之前执行===================");
        System.err.println("agentArgs : " + agentArgs);
        // 添加Transformer
        inst.addTransformer(new TransformerTest());
    }
}


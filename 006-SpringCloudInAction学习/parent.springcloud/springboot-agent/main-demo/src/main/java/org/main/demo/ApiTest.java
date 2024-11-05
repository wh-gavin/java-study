package org.main.demo;

import java.util.Random;

public class ApiTest {
    private double π = 3.14D;
    //S = πr²
    public double calculateCircularArea(int r) {
        return π * r * r;
    }
    //S = a + b
    public double sumOfTwoNumbers(double a, double b) {
        return a + b;
    }

    
    public String queryGirlfriendCount(String boyfriendName) {
        return boyfriendName + "的前女友数量：" + (new Random().nextInt(10) + 1) + " 个";
    }
}

package com.plugin.impl;

import org.springframework.stereotype.Service;

@Service
public class TestDataBeanServiceImpl implements ITestDataBeanService {
    @Override
    public TestResult customAccess(TestDataBean testData) {
        TestResult result = new TestResult();
        result.message="测试接口调用";
        return result;
    }
}

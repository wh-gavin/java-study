package com.plugin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginServiceImpl implements IPluginService {
    @Autowired
    private ITestDataBeanService testDataBeanService;

    @Override
    public String sayHello(String name) {
        TestResult result = testDataBeanService.customAccess(new TestDataBean());
        return result.message;
    }
}

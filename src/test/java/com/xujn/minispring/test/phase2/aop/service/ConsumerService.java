package com.xujn.minispring.test.phase2.aop.service;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class ConsumerService {

    @Autowired
    private TestService testService;

    public TestService getTestService() {
        return testService;
    }
}

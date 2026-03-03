package com.xujn.minispring.test.phase2.aop.service;

import com.xujn.minispring.context.annotation.Component;

@Component
public class TestServiceImpl implements TestService {

    @Override
    public String doSomething() {
        return "result";
    }
}

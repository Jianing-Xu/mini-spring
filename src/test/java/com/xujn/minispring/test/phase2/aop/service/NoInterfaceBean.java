package com.xujn.minispring.test.phase2.aop.service;

import com.xujn.minispring.context.annotation.Component;

@Component
public class NoInterfaceBean {

    public String call() {
        return "plain";
    }
}

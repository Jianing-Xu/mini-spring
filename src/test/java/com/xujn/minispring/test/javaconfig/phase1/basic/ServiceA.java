package com.xujn.minispring.test.javaconfig.phase1.basic;

import com.xujn.minispring.context.annotation.Autowired;

public class ServiceA {

    @Autowired
    private ServiceB serviceB;

    public ServiceB getServiceB() {
        return serviceB;
    }
}

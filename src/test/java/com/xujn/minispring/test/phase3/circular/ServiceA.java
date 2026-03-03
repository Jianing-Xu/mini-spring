package com.xujn.minispring.test.phase3.circular;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class ServiceA {

    @Autowired
    private ServiceB b;

    public ServiceB getB() {
        return b;
    }
}

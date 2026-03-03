package com.xujn.minispring.test.phase1.multilevel;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class ServiceA {

    @Autowired
    private ServiceB serviceB;

    public ServiceB getServiceB() {
        return serviceB;
    }
}

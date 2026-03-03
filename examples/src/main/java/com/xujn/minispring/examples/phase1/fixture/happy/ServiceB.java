package com.xujn.minispring.examples.phase1.fixture.happy;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class ServiceB {

    @Autowired
    private ServiceC serviceC;

    public ServiceC getServiceC() {
        return serviceC;
    }
}

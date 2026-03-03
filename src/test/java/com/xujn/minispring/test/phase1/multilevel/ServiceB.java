package com.xujn.minispring.test.phase1.multilevel;

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

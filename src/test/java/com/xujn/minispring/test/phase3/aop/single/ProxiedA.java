package com.xujn.minispring.test.phase3.aop.single;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class ProxiedA {

    @Autowired
    private ProxiedB b;

    public ProxiedB getB() {
        return b;
    }
}

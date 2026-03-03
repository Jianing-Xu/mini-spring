package com.xujn.minispring.examples.phase3.fixture;

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

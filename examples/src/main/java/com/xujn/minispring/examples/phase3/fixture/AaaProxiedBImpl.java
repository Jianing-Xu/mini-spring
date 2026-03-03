package com.xujn.minispring.examples.phase3.fixture;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component("proxiedBImpl")
public class AaaProxiedBImpl implements ProxiedB {

    @Autowired
    private ProxiedA a;

    @Override
    public String work() {
        return "phase3";
    }
}

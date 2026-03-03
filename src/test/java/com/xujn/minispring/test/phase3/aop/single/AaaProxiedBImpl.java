package com.xujn.minispring.test.phase3.aop.single;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component("proxiedBImpl")
public class AaaProxiedBImpl implements ProxiedB {

    @Autowired
    private ProxiedA a;

    @Override
    public String doSomething() {
        return "proxiedB";
    }

    public ProxiedA getA() {
        return a;
    }
}

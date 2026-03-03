package com.xujn.minispring.test.phase3.aop.dual;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class DualProxyBImpl implements InterfaceB {

    @Autowired
    private InterfaceA a;

    @Override
    public String pingB() {
        return "B";
    }

    @Override
    public InterfaceA getA() {
        return a;
    }
}

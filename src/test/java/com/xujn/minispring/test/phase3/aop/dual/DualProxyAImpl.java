package com.xujn.minispring.test.phase3.aop.dual;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class DualProxyAImpl implements InterfaceA {

    @Autowired
    private InterfaceB b;

    @Override
    public String pingA() {
        return "A";
    }

    @Override
    public InterfaceB getB() {
        return b;
    }
}

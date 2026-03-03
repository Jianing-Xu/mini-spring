package com.xujn.minispring.test.phase3.circular;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleA {

    @Autowired
    private CycleB b;

    public CycleB getB() {
        return b;
    }
}

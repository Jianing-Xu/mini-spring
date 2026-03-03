package com.xujn.minispring.test.phase3.circular;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleC {

    @Autowired
    private CycleA a;

    public CycleA getA() {
        return a;
    }
}

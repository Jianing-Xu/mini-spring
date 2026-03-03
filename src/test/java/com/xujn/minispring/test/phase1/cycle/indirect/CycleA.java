package com.xujn.minispring.test.phase1.cycle.indirect;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleA {

    @Autowired
    private CycleB cycleB;

    public CycleB getCycleB() {
        return cycleB;
    }
}

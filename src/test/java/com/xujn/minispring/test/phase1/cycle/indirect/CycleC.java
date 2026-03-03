package com.xujn.minispring.test.phase1.cycle.indirect;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleC {

    @Autowired
    private CycleA cycleA;

    public CycleA getCycleA() {
        return cycleA;
    }
}

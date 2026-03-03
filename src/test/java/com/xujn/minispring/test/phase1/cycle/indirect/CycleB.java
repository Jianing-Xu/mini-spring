package com.xujn.minispring.test.phase1.cycle.indirect;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleB {

    @Autowired
    private CycleC cycleC;

    public CycleC getCycleC() {
        return cycleC;
    }
}

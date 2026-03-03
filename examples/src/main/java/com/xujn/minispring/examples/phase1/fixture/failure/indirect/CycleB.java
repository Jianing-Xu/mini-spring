package com.xujn.minispring.examples.phase1.fixture.failure.indirect;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleB {

    @Autowired
    private CycleC cycleC;
}

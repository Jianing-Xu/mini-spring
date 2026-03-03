package com.xujn.minispring.examples.phase1.fixture.failure;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleA {

    @Autowired
    private CycleB cycleB;
}

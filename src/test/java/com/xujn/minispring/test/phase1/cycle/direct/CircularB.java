package com.xujn.minispring.test.phase1.cycle.direct;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CircularB {

    @Autowired
    private CircularA circularA;

    public CircularA getCircularA() {
        return circularA;
    }
}

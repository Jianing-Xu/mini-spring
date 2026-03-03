package com.xujn.minispring.test.phase1.cycle.direct;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CircularA {

    @Autowired
    private CircularB circularB;

    public CircularB getCircularB() {
        return circularB;
    }
}

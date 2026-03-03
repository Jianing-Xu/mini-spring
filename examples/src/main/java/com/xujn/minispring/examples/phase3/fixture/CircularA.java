package com.xujn.minispring.examples.phase3.fixture;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CircularA {

    @Autowired
    private CircularB b;

    public CircularB getB() {
        return b;
    }
}

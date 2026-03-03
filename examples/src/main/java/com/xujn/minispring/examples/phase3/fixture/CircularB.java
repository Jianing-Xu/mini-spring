package com.xujn.minispring.examples.phase3.fixture;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CircularB {

    @Autowired
    private CircularA a;

    public CircularA getA() {
        return a;
    }
}

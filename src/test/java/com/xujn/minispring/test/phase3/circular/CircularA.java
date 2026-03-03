package com.xujn.minispring.test.phase3.circular;

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

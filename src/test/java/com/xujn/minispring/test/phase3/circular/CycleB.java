package com.xujn.minispring.test.phase3.circular;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CycleB {

    @Autowired
    private CycleC c;

    public CycleC getC() {
        return c;
    }
}

package com.xujn.minispring.test.phase3.circular;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class SelfRefBean {

    @Autowired
    private SelfRefBean self;

    public SelfRefBean getSelf() {
        return self;
    }
}

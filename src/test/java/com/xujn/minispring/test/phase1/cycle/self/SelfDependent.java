package com.xujn.minispring.test.phase1.cycle.self;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class SelfDependent {

    @Autowired
    private SelfDependent selfDependent;

    public SelfDependent getSelfDependent() {
        return selfDependent;
    }
}

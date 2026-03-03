package com.xujn.minispring.examples.phase1.fixture.failure.self;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class SelfDependent {

    @Autowired
    private SelfDependent selfDependent;
}

package com.xujn.minispring.examples.phase1.fixture.failure.direct;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class CircularB {

    @Autowired
    private CircularA circularA;
}

package com.xujn.minispring.test.phase3.unsupported.mix;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class MixB {

    @Autowired
    private MixA a;
}

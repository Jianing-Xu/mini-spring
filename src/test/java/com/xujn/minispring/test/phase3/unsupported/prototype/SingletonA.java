package com.xujn.minispring.test.phase3.unsupported.prototype;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class SingletonA {

    @Autowired
    private PrototypeB b;
}

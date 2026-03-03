package com.xujn.minispring.test.phase3.unsupported.prototype;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.context.annotation.Scope;

@Component
@Scope("prototype")
public class PrototypeB {

    @Autowired
    private SingletonA a;
}

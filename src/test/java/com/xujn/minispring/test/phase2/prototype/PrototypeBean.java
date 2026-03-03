package com.xujn.minispring.test.phase2.prototype;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.context.annotation.Scope;

@Component
@Scope("prototype")
public class PrototypeBean {

    @Autowired
    private SingletonDep dep;

    public SingletonDep getDep() {
        return dep;
    }
}

package com.xujn.minispring.test.javaconfig.phase2.ambiguous;

import com.xujn.minispring.context.annotation.Component;

@Component
public class SecondClient implements Client {

    @Override
    public String name() {
        return "second";
    }
}

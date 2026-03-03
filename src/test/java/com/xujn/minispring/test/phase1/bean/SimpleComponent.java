package com.xujn.minispring.test.phase1.bean;

import com.xujn.minispring.context.annotation.Component;

@Component
public class SimpleComponent {

    public String ping() {
        return "pong";
    }
}

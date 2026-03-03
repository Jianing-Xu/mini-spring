package com.xujn.minispring.examples.phase1.fixture.failure;

import com.xujn.minispring.context.annotation.Component;

@Component
public class H2DataSource implements DataSource {

    @Override
    public String type() {
        return "h2";
    }
}

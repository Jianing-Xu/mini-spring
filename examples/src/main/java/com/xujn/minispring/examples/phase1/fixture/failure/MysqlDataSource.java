package com.xujn.minispring.examples.phase1.fixture.failure;

import com.xujn.minispring.context.annotation.Component;

@Component
public class MysqlDataSource implements DataSource {

    @Override
    public String type() {
        return "mysql";
    }
}

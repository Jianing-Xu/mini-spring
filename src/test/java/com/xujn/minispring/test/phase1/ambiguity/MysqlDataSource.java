package com.xujn.minispring.test.phase1.ambiguity;

import com.xujn.minispring.context.annotation.Component;

@Component
public class MysqlDataSource implements DataSource {

    @Override
    public String type() {
        return "mysql";
    }
}

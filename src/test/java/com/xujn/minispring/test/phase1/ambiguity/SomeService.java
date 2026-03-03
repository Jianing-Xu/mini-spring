package com.xujn.minispring.test.phase1.ambiguity;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class SomeService {

    @Autowired
    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }
}

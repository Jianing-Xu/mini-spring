package com.xujn.minispring.examples.javaconfig.phase2.fixture.failure.conflict;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class ConflictConfig {

    @Bean(name = "duplicateExampleBean")
    public DuplicateExampleBean duplicateExampleBean() {
        return new DuplicateExampleBean();
    }
}

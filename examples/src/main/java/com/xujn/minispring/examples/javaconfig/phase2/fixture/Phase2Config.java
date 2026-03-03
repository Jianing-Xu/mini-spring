package com.xujn.minispring.examples.javaconfig.phase2.fixture;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class Phase2Config {

    @Bean(initMethod = "init", destroyMethod = "cleanup")
    public Phase2ExampleService phase2ExampleService(Phase2Repository repository) {
        return new Phase2ExampleService(repository);
    }
}

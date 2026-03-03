package com.xujn.minispring.examples.javaconfig.phase2.fixture.failure.missing;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class MissingConfig {

    @Bean
    public MissingService missingService(MissingDependency dependency) {
        return new MissingService(dependency);
    }
}

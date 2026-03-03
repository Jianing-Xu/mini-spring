package com.xujn.minispring.test.javaconfig.phase2.missing;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class MissingConfig {

    @Bean
    public MissingService missingService(MissingDependency dependency) {
        return new MissingService(dependency);
    }
}

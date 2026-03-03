package com.xujn.minispring.examples.javaconfig.phase1.fixture.failure.nullcase;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class NullConfig {

    @Bean
    public NullBean nullBean() {
        return null;
    }
}

package com.xujn.minispring.examples.javaconfig.phase1.fixture.failure.throwing;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class ThrowingConfig {

    @Bean
    public ThrowingBean failingBean() {
        throw new RuntimeException("init failed");
    }
}

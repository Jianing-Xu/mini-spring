package com.xujn.minispring.test.javaconfig.phase1.throwing;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class ThrowingConfig {

    @Bean
    public ThrowingBean failingBean() {
        throw new RuntimeException("init failed");
    }
}

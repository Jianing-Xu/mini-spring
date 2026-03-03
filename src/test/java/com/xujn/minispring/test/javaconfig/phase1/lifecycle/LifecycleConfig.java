package com.xujn.minispring.test.javaconfig.phase1.lifecycle;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class LifecycleConfig {

    @Bean
    public LifecycleService lifecycleService() {
        return new LifecycleService();
    }
}

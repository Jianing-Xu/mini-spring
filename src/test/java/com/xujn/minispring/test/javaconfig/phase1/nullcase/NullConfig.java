package com.xujn.minispring.test.javaconfig.phase1.nullcase;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class NullConfig {

    @Bean
    public NullBean nullBean() {
        return null;
    }
}

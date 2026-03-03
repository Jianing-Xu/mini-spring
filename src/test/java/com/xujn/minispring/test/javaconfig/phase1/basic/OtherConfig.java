package com.xujn.minispring.test.javaconfig.phase1.basic;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class OtherConfig {

    @Bean
    public ExtraService extraService() {
        return new ExtraService();
    }
}

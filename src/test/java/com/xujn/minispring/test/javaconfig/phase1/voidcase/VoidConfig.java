package com.xujn.minispring.test.javaconfig.phase1.voidcase;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class VoidConfig {

    @Bean
    public void invalidBean() {
    }

    @Bean
    public ValidBean validBean() {
        return new ValidBean();
    }
}

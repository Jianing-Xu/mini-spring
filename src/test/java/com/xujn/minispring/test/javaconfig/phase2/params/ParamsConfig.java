package com.xujn.minispring.test.javaconfig.phase2.params;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class ParamsConfig {

    @Bean
    public ConfigCreatedService configCreatedService(Repository repository) {
        return new ConfigCreatedService(repository);
    }
}

package com.xujn.minispring.test.javaconfig.phase2.conflict;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class ConflictConfig {

    @Bean(name = "duplicateService")
    public DuplicateService duplicateService() {
        return new DuplicateService();
    }
}

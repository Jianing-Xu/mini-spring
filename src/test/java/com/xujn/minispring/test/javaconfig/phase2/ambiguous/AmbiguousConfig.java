package com.xujn.minispring.test.javaconfig.phase2.ambiguous;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class AmbiguousConfig {

    @Bean
    public AmbiguousService ambiguousService(Client client) {
        return new AmbiguousService(client);
    }
}

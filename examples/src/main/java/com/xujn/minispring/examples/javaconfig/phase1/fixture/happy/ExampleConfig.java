package com.xujn.minispring.examples.javaconfig.phase1.fixture.happy;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class ExampleConfig {

    @Bean
    public DataSource dataSource() {
        return new DataSource("example");
    }

    @Bean
    public AppService appService() {
        return new AppService();
    }
}

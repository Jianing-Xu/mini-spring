package com.xujn.minispring.test.javaconfig.phase1.coexist;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        return new DataSource("coexist");
    }

    @Bean
    public MyService myService() {
        return new MyService();
    }
}

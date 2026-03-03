package com.xujn.minispring.test.javaconfig.phase1.basic;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        return new DataSource("primary");
    }

    @Bean(name = "myDs")
    public NamedDataSource namedDataSource() {
        return new NamedDataSource("named");
    }

    @Bean
    public ServiceA serviceA() {
        return new ServiceA();
    }

    @Bean
    public ServiceC serviceC() {
        return new ServiceC();
    }

    public String helper() {
        return "helper";
    }
}

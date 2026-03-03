package com.xujn.minispring.test.javaconfig.phase1.basic;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Component;

@Component
public class PlainComponent {

    @Bean
    public IgnoredBean ignoredBean() {
        return new IgnoredBean();
    }
}

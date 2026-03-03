package com.xujn.minispring.test.javaconfig.phase2.lifecycle;

import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
public class LifecyclePhase2Config {

    @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
    public ManagedLifecycleBean managedLifecycleBean(LifecycleDependency dependency) {
        return new ManagedLifecycleBean(dependency);
    }
}

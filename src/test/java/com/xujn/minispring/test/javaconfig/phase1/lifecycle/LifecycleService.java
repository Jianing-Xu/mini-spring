package com.xujn.minispring.test.javaconfig.phase1.lifecycle;

import com.xujn.minispring.beans.factory.InitializingBean;
import com.xujn.minispring.context.annotation.Autowired;

public class LifecycleService implements InitializingBean {

    @Autowired
    private LifecycleDependency dependency;

    private boolean initialized;

    @Override
    public void afterPropertiesSet() {
        initialized = true;
        LifecycleState.dependencyVisibleDuringInit = dependency != null;
    }

    public boolean isInitialized() {
        return initialized;
    }
}

package com.xujn.minispring.test.javaconfig.phase2.lifecycle;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.InitializingBean;

public class ManagedLifecycleBean implements InitializingBean, DisposableBean {

    private final LifecycleDependency dependency;
    private boolean afterPropertiesSetCalled;

    public ManagedLifecycleBean(LifecycleDependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public void afterPropertiesSet() {
        afterPropertiesSetCalled = dependency != null;
    }

    @Override
    public void destroy() {
        LifecyclePhase2State.interfaceDestroyCalled = true;
    }

    public void customInit() {
        LifecyclePhase2State.customInitCalled = true;
    }

    public void customDestroy() {
        LifecyclePhase2State.customDestroyCalled = true;
    }

    public boolean isAfterPropertiesSetCalled() {
        return afterPropertiesSetCalled;
    }
}

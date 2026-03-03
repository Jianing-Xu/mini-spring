package com.xujn.minispring.test.phase2.lifecycle.init;

import com.xujn.minispring.beans.factory.InitializingBean;
import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class LifecycleBean implements InitializingBean {

    private static boolean initialized;
    private static boolean dependencyVisibleDuringInit;
    private static long afterPropertiesSetTime;

    @Autowired
    private LifecycleDependency lifecycleDependency;

    @Override
    public void afterPropertiesSet() {
        initialized = true;
        dependencyVisibleDuringInit = lifecycleDependency != null;
        afterPropertiesSetTime = System.nanoTime();
        LifecycleEvents.EVENTS.add("afterPropertiesSet");
    }

    public static void reset() {
        initialized = false;
        dependencyVisibleDuringInit = false;
        afterPropertiesSetTime = 0L;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isDependencyVisibleDuringInit() {
        return dependencyVisibleDuringInit;
    }

    public static long getAfterPropertiesSetTime() {
        return afterPropertiesSetTime;
    }
}

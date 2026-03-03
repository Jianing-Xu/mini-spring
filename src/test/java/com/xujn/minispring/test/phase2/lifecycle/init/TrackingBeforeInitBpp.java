package com.xujn.minispring.test.phase2.lifecycle.init;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

@Component
public class TrackingBeforeInitBpp implements BeanPostProcessor {

    private static long beforeInitTime;

    public static void reset() {
        beforeInitTime = 0L;
    }

    public static long getBeforeInitTime() {
        return beforeInitTime;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof LifecycleBean lifecycleBean) {
            if (lifecycleBean.isDependencyVisibleDuringInit() || !LifecycleEvents.EVENTS.contains("populateBean")) {
                LifecycleEvents.EVENTS.add("populateBean");
            }
            beforeInitTime = System.nanoTime();
            LifecycleEvents.EVENTS.add("bppBefore");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof LifecycleBean) {
            LifecycleEvents.EVENTS.add("bppAfter");
        }
        return bean;
    }
}

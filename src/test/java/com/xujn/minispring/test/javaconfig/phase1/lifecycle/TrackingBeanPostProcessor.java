package com.xujn.minispring.test.javaconfig.phase1.lifecycle;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

@Component
public class TrackingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if ("lifecycleService".equals(beanName)) {
            LifecycleState.beforeInitializationCalled = true;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("lifecycleService".equals(beanName)) {
            LifecycleState.afterInitializationCalled = true;
        }
        return bean;
    }
}

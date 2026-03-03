package com.xujn.minispring.test.phase2.bpp;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

@Component
public class TrackingBpp implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.startsWith("businessBean")) {
            TrackingState.BEFORE.add(beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.startsWith("businessBean")) {
            TrackingState.AFTER.add(beanName);
        }
        return bean;
    }
}

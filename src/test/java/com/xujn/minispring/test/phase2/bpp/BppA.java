package com.xujn.minispring.test.phase2.bpp;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

@Component
public class BppA implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.startsWith("businessBean")) {
            TrackingState.ORDER.add("BPP_A.before:" + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.startsWith("businessBean")) {
            TrackingState.ORDER.add("BPP_A.after:" + beanName);
        }
        return bean;
    }
}

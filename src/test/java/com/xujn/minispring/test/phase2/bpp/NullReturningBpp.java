package com.xujn.minispring.test.phase2.bpp;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

@Component
public class NullReturningBpp implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if ("businessBeanOne".equals(beanName)) {
            return null;
        }
        return bean;
    }
}

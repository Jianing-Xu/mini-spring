package com.xujn.minispring.test.phase2.bpp;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

@Component
public class ReplacingBpp implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("replaceableServiceImpl".equals(beanName) && bean instanceof ReplaceableService) {
            return (ReplaceableService) () -> "wrapped";
        }
        return bean;
    }
}

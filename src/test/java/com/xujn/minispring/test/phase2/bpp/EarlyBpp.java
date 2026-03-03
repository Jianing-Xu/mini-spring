package com.xujn.minispring.test.phase2.bpp;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

@Component
public class EarlyBpp implements BeanPostProcessor {

    private static boolean businessBeanProcessed;

    public static void reset() {
        businessBeanProcessed = false;
    }

    public static boolean isBusinessBeanProcessed() {
        return businessBeanProcessed;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if ("earlyBusinessBean".equals(beanName)) {
            businessBeanProcessed = true;
        }
        return bean;
    }
}

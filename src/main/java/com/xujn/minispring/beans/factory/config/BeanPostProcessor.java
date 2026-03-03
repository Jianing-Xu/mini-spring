package com.xujn.minispring.beans.factory.config;

/**
 * Extension hook around bean initialization.
 * Constraint: processors are applied in registration order for every created bean.
 * Thread-safety: depends on the implementing processor.
 */
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}

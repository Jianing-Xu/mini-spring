package com.xujn.minispring.beans.factory.config;

/**
 * BeanPostProcessor variant that can expose an early bean reference before full initialization.
 * Constraint: Phase 3 uses this hook only for singleton circular dependency resolution.
 * Thread-safety: depends on the implementing processor.
 */
public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}

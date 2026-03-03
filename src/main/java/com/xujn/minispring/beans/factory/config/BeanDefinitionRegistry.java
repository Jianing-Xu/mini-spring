package com.xujn.minispring.beans.factory.config;

/**
 * Registry of bean definitions discovered during bootstrap.
 * Constraint: bean names must be unique within a registry.
 * Thread-safety: depends on implementation.
 */
public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    BeanDefinition getBeanDefinition(String beanName);

    boolean containsBeanDefinition(String beanName);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    void setAllowOverride(boolean allowOverride);

    boolean isAllowOverride();
}

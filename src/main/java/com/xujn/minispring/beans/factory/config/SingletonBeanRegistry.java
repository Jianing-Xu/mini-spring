package com.xujn.minispring.beans.factory.config;

/**
 * Registry of singleton bean instances.
 * Constraint: only fully created singleton instances are stored in Phase 1.
 * Thread-safety: depends on implementation.
 */
public interface SingletonBeanRegistry {

    Object getSingleton(String beanName);

    void registerSingleton(String beanName, Object singletonObject);

    boolean containsSingleton(String beanName);
}

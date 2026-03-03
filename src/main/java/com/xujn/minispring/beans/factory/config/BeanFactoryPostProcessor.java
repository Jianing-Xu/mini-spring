package com.xujn.minispring.beans.factory.config;

/**
 * Registry post-processor hook executed after component scanning and before bean instantiation.
 * Constraint: current usage is limited to internal JavaConfig parsing.
 * Thread-safety: implementations are expected to run during single-threaded bootstrap.
 */
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(BeanDefinitionRegistry registry);
}

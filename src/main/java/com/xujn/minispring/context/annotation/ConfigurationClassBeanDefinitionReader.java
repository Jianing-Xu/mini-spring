package com.xujn.minispring.context.annotation;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanDefinitionRegistry;

import java.util.Set;

/**
 * Registers factory-method bean definitions parsed from {@link Configuration} classes.
 * Constraint: JavaConfig Phase 1 writes singleton factory-method metadata only.
 * Thread-safety: intended for single-threaded bootstrap.
 */
public class ConfigurationClassBeanDefinitionReader {

    public void loadBeanDefinitions(Set<ConfigurationClass> configClasses, BeanDefinitionRegistry registry) {
        for (ConfigurationClass configurationClass : configClasses) {
            for (BeanMethod beanMethod : configurationClass.getBeanMethods()) {
                BeanDefinition beanDefinition =
                        new BeanDefinition(beanMethod.getReturnType(), beanMethod.getBeanName());
                beanDefinition.setFactoryBeanName(configurationClass.getBeanName());
                beanDefinition.setFactoryMethodName(beanMethod.getMethod().getName());
                beanDefinition.setSource("JavaConfig:" + configurationClass.getConfigClass().getName());
                registry.registerBeanDefinition(beanMethod.getBeanName(), beanDefinition);
            }
        }
    }
}

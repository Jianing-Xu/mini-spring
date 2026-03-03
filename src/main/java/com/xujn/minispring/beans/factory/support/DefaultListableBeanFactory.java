package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanDefinitionRegistry;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.exception.NoSuchBeanDefinitionException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default bean factory that stores bean definitions and eagerly creates singleton beans.
 * Constraint: bean definition names must stay unique within the factory.
 * Thread-safety: definition registration is intended for bootstrap-time use only.
 */
public class DefaultListableBeanFactory extends AutowireCapableBeanFactory implements BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitionMap = new LinkedHashMap<>();
    private final List<String> beanDefinitionNames = new ArrayList<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Objects.requireNonNull(beanName, "beanName must not be null");
        Objects.requireNonNull(beanDefinition, "beanDefinition must not be null");
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new BeansException("BeanDefinition named '" + beanName + "' is already registered");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
        beanDefinitionNames.add(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return beanDefinition;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionNames.toArray(String[]::new);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionNames.size();
    }

    public void preInstantiateSingletons() {
        for (String beanName : List.copyOf(beanDefinitionNames)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition != null && beanDefinition.isSingleton()) {
                getBean(beanName);
            }
        }
    }

    @Override
    protected String[] getBeanDefinitionNamesForLookup() {
        return getBeanDefinitionNames();
    }
}

package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanDefinitionRegistry;
import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.exception.BeanDefinitionOverrideException;
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
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    private boolean allowOverride;

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Objects.requireNonNull(beanName, "beanName must not be null");
        Objects.requireNonNull(beanDefinition, "beanDefinition must not be null");
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition existing = beanDefinitionMap.get(beanName);
            if (!allowOverride) {
                throw new BeanDefinitionOverrideException(
                        beanName,
                        existing == null ? "unknown" : existing.getSource(),
                        beanDefinition.getSource()
                );
            }
            beanDefinitionMap.put(beanName, beanDefinition);
            return;
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

    @Override
    public void setAllowOverride(boolean allowOverride) {
        this.allowOverride = allowOverride;
    }

    @Override
    public boolean isAllowOverride() {
        return allowOverride;
    }

    public void preInstantiateSingletons() {
        for (String beanName : List.copyOf(beanDefinitionNames)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition != null && beanDefinition.isSingleton()) {
                getBean(beanName);
            }
        }
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.remove(beanPostProcessor);
        beanPostProcessors.add(beanPostProcessor);
    }

    public int getBeanPostProcessorCount() {
        return beanPostProcessors.size();
    }

    public String[] getBeanNamesForType(Class<?> type) {
        List<String> beanNames = new ArrayList<>();
        for (String beanName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition != null && type.isAssignableFrom(beanDefinition.getBeanClass())) {
                beanNames.add(beanName);
            }
        }
        return beanNames.toArray(String[]::new);
    }

    @Override
    protected String[] getBeanDefinitionNamesForLookup() {
        return getBeanDefinitionNames();
    }

    @Override
    protected List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }
}

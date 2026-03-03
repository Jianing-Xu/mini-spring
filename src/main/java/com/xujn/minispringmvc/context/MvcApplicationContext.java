package com.xujn.minispringmvc.context;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MVC-facing wrapper around the mini-spring application context.
 * Constraint: MVC init reads bean metadata and bean instances only after mini-spring refresh has completed.
 * Thread-safety: delegates to the underlying application context and is intended for bootstrap-time assembly.
 */
public class MvcApplicationContext {

    private final AnnotationConfigApplicationContext applicationContext;

    public MvcApplicationContext(AnnotationConfigApplicationContext applicationContext) {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext must not be null");
    }

    public AnnotationConfigApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public BeanDefinition getBeanDefinition(String beanName) {
        return applicationContext.getBeanDefinition(beanName);
    }

    public String[] getBeanDefinitionNames() {
        return applicationContext.getBeanDefinitionNames();
    }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return applicationContext.getBean(beanName, requiredType);
    }

    public Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public <T> List<T> getBeansOfType(Class<T> type) {
        List<T> beans = new ArrayList<>();
        for (String beanName : applicationContext.getBeanFactory().getBeanNamesForType(type)) {
            beans.add(applicationContext.getBean(beanName, type));
        }
        return beans;
    }

    public List<String> getBeanNamesForType(Class<?> type) {
        return List.of(applicationContext.getBeanFactory().getBeanNamesForType(type));
    }
}

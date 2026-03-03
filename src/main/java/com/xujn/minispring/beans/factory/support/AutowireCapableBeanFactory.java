package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.core.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Bean factory capable of reflective instantiation and field-based dependency injection.
 * Constraint: only @Autowired field injection is supported in Phase 1.
 * Thread-safety: designed for single-threaded bootstrap.
 */
public abstract class AutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        Object bean = createBeanInstance(beanName, beanDefinition);
        populateBean(beanName, bean, beanDefinition);
        return bean;
    }

    protected Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
        return ReflectionUtils.instantiateClass(beanDefinition.getBeanClass(), beanName);
    }

    protected void populateBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        for (Field field : ReflectionUtils.getDeclaredFields(beanDefinition.getBeanClass())) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object dependency = getBean(field.getType());
            ReflectionUtils.setField(field, bean, dependency, beanName);
        }
    }
}

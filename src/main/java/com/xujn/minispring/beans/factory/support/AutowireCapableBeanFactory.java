package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.InitializingBean;
import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.core.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Bean factory capable of reflective instantiation and field-based dependency injection.
 * Constraint: Phase 2 adds BeanPostProcessor hooks and lifecycle callbacks on top of field injection.
 * Thread-safety: designed for single-threaded bootstrap.
 */
public abstract class AutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        Object bean = createBeanInstance(beanName, beanDefinition);
        populateBean(beanName, bean, beanDefinition);
        Object exposedObject = initializeBean(beanName, bean);
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);
        return exposedObject;
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

    protected Object initializeBean(String beanName, Object bean) {
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        invokeInitMethods(beanName, wrappedBean);
        return applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    protected Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        Object current = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object candidate = beanPostProcessor.postProcessBeforeInitialization(current, beanName);
            if (candidate != null) {
                current = candidate;
            }
        }
        return current;
    }

    protected Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        Object current = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object candidate = beanPostProcessor.postProcessAfterInitialization(current, beanName);
            if (candidate != null) {
                current = candidate;
            }
        }
        return current;
    }

    protected void invokeInitMethods(String beanName, Object bean) {
        if (bean instanceof InitializingBean initializingBean) {
            initializingBean.afterPropertiesSet();
        }
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && bean instanceof DisposableBean disposableBean) {
            registerDisposableBean(beanName, disposableBean);
        }
    }

    protected abstract java.util.List<BeanPostProcessor> getBeanPostProcessors();
}
